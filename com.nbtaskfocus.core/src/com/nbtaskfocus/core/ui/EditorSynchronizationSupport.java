/*
 * The MIT License
 *
 * Copyright 2011 tushar_joshi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.nbtaskfocus.core.ui;

import java.awt.EventQueue;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.windows.TopComponent;

/**
 *
 * @author tushar_joshi
 */
public class EditorSynchronizationSupport {
    
    private final EditorSynchronizable synchronizable;
    public static final RequestProcessor RP = new RequestProcessor(EditorSynchronizationSupport.class);
    private boolean synchronizeViews = false;
    
    public EditorSynchronizationSupport(EditorSynchronizable synchronizable) {
        this.synchronizable = synchronizable;      
        
        Preferences nbPrefs = NbPreferences.root().node("org/netbeans/modules/projectui");
        synchronizeViews = nbPrefs.getBoolean("synchronizeEditorWithViews", false);
        nbPrefs.addPreferenceChangeListener(new EditorSynchronizationSupport.NbPrefsListener());
        
        selectionTask = createSelectionTask();
    }
    
    private class NbPrefsListener implements PreferenceChangeListener {

        @Override
        public void preferenceChange(PreferenceChangeEvent evt) {
            if ("synchronizeEditorWithViews".equals(evt.getKey())) {
                synchronizeViews = Boolean.parseBoolean(evt.getNewValue());
            }
        }
    
    };
    
    // SEARCHING NODES
    
    private static final Lookup context = Utilities.actionsGlobalContext();
    
    private static final Lookup.Result<FileObject> foSelection = context.lookup(new Lookup.Template<FileObject>(FileObject.class));
    
    private static final Lookup.Result<DataObject> doSelection = context.lookup(new Lookup.Template<DataObject>(DataObject.class));
    
    private FileObject objectToSelect;

    private RequestProcessor.Task selectionTask;

    private static final int NODE_SELECTION_DELAY = 200;

    private final LookupListener baseListener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {
            if (TopComponent.getRegistry().getActivated() == synchronizable.getTopComponent()) {
                return;
            }
            
            if (synchronizeViews) {
                Collection<? extends FileObject> fos = foSelection.allInstances();
                if (fos.size() == 1) {
                    selectNodeAsyncNoSelect(fos.iterator().next());
                } else {
                    Collection<? extends DataObject> dos = doSelection.allInstances();
                    if (dos.size() == 1) {
                        selectNodeAsyncNoSelect((dos.iterator().next()).getPrimaryFile());
                    }
                }
            }
        }
        
    };
    
    private final LookupListener weakListener = WeakListeners.create(LookupListener.class, baseListener, null);
        
    public void startListening() {
        foSelection.addLookupListener(weakListener);
        doSelection.addLookupListener(weakListener);
        baseListener.resultChanged(null);
    }
    
    public void stopListening() {
        foSelection.removeLookupListener(weakListener);
        doSelection.removeLookupListener(weakListener);
    }
    
    private void selectNodeAsyncNoSelect(FileObject object) {
        objectToSelect = object;
        selectionTask.schedule(NODE_SELECTION_DELAY);
    }
    
    private RequestProcessor.Task createSelectionTask() {
        RequestProcessor.Task task = RP.create(new Runnable() {

            @Override
            public void run() {
                if (objectToSelect == null) {
                    return;
                }
                Node tempNode = synchronizable.findNode(objectToSelect);
                
                if( null != tempNode ) {
                    final Node selectedNode = tempNode;
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if ( selectedNode != null ) {
                                try {
                                    synchronizable.getExplorerManager().setSelectedNodes( new Node[] { selectedNode } );
                                    synchronizable.getCustomTreeView().scrollToNode(selectedNode);
                                } catch(PropertyVetoException e) {
                                     // Bad day node found but can't be selected
                                }
                            }
                        }
                    });
                }
            }
            
        });
        return task;
    }
    
}
