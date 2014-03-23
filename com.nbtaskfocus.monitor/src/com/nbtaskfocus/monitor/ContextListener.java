/*
 * The MIT License
 *
 * Copyright 2011 Tushar Joshi, Nagpur.
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
package com.nbtaskfocus.monitor;

import com.nbtaskfocus.model.api.TaskList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class ContextListener implements PropertyChangeListener {
    

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                notifyContextChange(evt);
            }
        });
    }

    public void notifyContextChange(PropertyChangeEvent evt) {

        String propertyName = evt.getPropertyName();
        Object newValue = evt.getNewValue();

        boolean tcOpened = TopComponent.Registry.PROP_TC_OPENED.equals(propertyName);
        boolean tcClosed = TopComponent.Registry.PROP_TC_CLOSED.equals(propertyName);

        if (tcOpened || tcClosed) {
            TopComponent tc = (TopComponent) newValue;
            final Lookup lookup = tc.getLookup();
            FileObject fileObject = getFileObject(lookup);
            if (null != fileObject) {

                Project project = FileOwnerQuery.getOwner(fileObject);

                TaskList taskList = TaskList.getDefault();

                String itemPath = fileObject.getPath();
                String projectPath = null;
                
                if (null != project) {
                    projectPath = project.getProjectDirectory().getPath();
                }

                if (tcOpened) {
                    taskList.addContextItem(itemPath, projectPath);
                } else {
                    taskList.removeContextItem(itemPath, projectPath);
                }

            } 

        }
    }
    
    private static FileObject getFileObject(Lookup lookup) {
        FileObject fileObject = lookup.lookup(FileObject.class);
        if( null == fileObject) {
            DataObject dataObject = lookup.lookup(DataObject.class);
            if( null != dataObject ) {
                fileObject = dataObject.getPrimaryFile();
            }
        }
        
        return fileObject;
    }
}
