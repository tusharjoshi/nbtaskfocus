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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.nbtaskfocus.core.ui;

import com.nbtaskfocus.core.ui.nodes.ContextRootNode;
import com.nbtaskfocus.model.api.TaskList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//com.nbtaskfocus.core.ui//ProjectContext//EN",
autostore = false)
@TopComponent.Description(preferredID = "ProjectContextTopComponent",
iconBase = "com/nbtaskfocus/core/ui/resources/application_form.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = false)
@ActionID(category = "Window", id = "com.nbtaskfocus.core.ui.ProjectContextTopComponent")
@ActionReference(path = "Menu/Window/Task Focus", position = 733)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ProjectContextAction",
preferredID = "ProjectContextTopComponent")
public final class ProjectContextTopComponent extends TopComponent
        implements ExplorerManager.Provider, EditorSynchronizable {

    private transient final ExplorerManager manager;
    
    private transient final EditorSynchronizationSupport editorSynchronizationSupport;

    public ProjectContextTopComponent() {

        manager = new ExplorerManager();

        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));

        initComponents();
        setName(NbBundle.getMessage(ProjectContextTopComponent.class,
                "CTL_ProjectContextTopComponent"));
        setToolTipText(NbBundle.getMessage(ProjectContextTopComponent.class,
                "HINT_ProjectContextTopComponent"));

        associateLookup(ExplorerUtils.createLookup(manager, map));

        TaskList.getDefault().addPropertyChangeListener(
                WeakListeners.create(PropertyChangeListener.class,
                ((PropertyChangeListener) treeViewPanel),
                TaskList.getDefault()));

        ContextManager.getDefault().addPropertyChangeListener(
                WeakListeners.create(PropertyChangeListener.class,
                ((PropertyChangeListener) treeViewPanel),
                ContextManager.getDefault()));
        
        ContextManager.getDefault().addPropertyChangeListener(
                WeakListeners.create(PropertyChangeListener.class,
                propertyChangeListener,
                ContextManager.getDefault()));
        
        editorSynchronizationSupport = new EditorSynchronizationSupport(this);

    }
    
    private boolean onlyButtonChange = false;
    
    PropertyChangeListener propertyChangeListener =
        new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if (ContextManager.EVENT_TREESTATECHANGED.equals(propertyName)) {
                    int treeState = ContextManager.getDefault().getTreeState();
                    if( treeState == ContextManager.CONTEXT_TREE_EXCLUDE) {
                        if( grayedButton.isSelected()) {
                            onlyButtonChange = true;
                            excludeButton.setSelected(true);
                        }
                    } else {
                        if( excludeButton.isSelected()) {
                            onlyButtonChange = true;
                            grayedButton.setSelected(true);
                        }
                    }
                }
            }
        };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        grayedButton = new javax.swing.JToggleButton();
        excludeButton = new javax.swing.JToggleButton();
        treeViewPanel = new ContextTreeView(this, ContextTreeView.PROJECTTABLOGICAL_TC);

        jToolBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jToolBar1.setRollover(true);

        buttonGroup.add(grayedButton);
        grayedButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/nbtaskfocus/core/ui/resources/context_grayed.png"))); // NOI18N
        grayedButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(grayedButton, org.openide.util.NbBundle.getMessage(ProjectContextTopComponent.class, "ProjectContextTopComponent.grayedButton.text")); // NOI18N
        grayedButton.setFocusable(false);
        grayedButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        grayedButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        grayedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grayedButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(grayedButton);

        buttonGroup.add(excludeButton);
        excludeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/nbtaskfocus/core/ui/resources/context_exluded.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(excludeButton, org.openide.util.NbBundle.getMessage(ProjectContextTopComponent.class, "ProjectContextTopComponent.excludeButton.text")); // NOI18N
        excludeButton.setFocusable(false);
        excludeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        excludeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        excludeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excludeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(excludeButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(treeViewPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treeViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void grayedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grayedButtonActionPerformed
        if( !onlyButtonChange ) {
            ContextManager.getDefault().setTreeState(ContextManager.CONTEXT_TREE_GRAYED);
        } else {
            onlyButtonChange = false;
        }
    }//GEN-LAST:event_grayedButtonActionPerformed

    private void excludeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excludeButtonActionPerformed
        if( !onlyButtonChange ) {
            ContextManager.getDefault().setTreeState(ContextManager.CONTEXT_TREE_EXCLUDE);
        } else {
            onlyButtonChange = false;
        }
    }//GEN-LAST:event_excludeButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JToggleButton excludeButton;
    private javax.swing.JToggleButton grayedButton;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane treeViewPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }  
    
    @Override
    protected void componentShowing() {
        super.componentShowing();
        editorSynchronizationSupport.startListening();
    }
    
    @Override
    protected void componentHidden() {
        super.componentHidden();
        editorSynchronizationSupport.stopListening();
    }

    @Override
    public void requestActive() {
        super.requestActive();
        treeViewPanel.requestFocusInWindow();
    }

    @Override
    protected void componentActivated() {
        ExplorerUtils.activateActions(manager, true);
    }

    @Override
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(manager, false);
    }
    
    

    @Override
    public TopComponent getTopComponent() {
        return this;
    }

    @Override
    public CustomTreeView getCustomTreeView() {
        return (CustomTreeView)treeViewPanel;
    }

    @Override
    public Node findNode(FileObject objectToSelect) {
        ContextRootNode rootContext = (ContextRootNode)manager.getRootContext();
        return rootContext.findNode(objectToSelect);
    }
}
