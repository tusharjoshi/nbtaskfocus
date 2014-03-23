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
package com.nbtaskfocus.core.ui.nodes;

import com.nbtaskfocus.model.api.ProjectItem;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class ProjectItemNode extends AbstractNode implements PropertyChangeListener {
    
    private static final Logger logger = 
            Logger.getLogger(ProjectItemNode.class.getName());
    
    public static final String IMAGEPATH_ICON = 
            "com/nbtaskfocus/core/ui/resources/taskitem_icon.png";
    public static final String IMAGEPATH_STICKY_ICON = 
            "com/nbtaskfocus/core/ui/resources/taskitem_sticky_icon.png";
    
    private final ProjectItem projectItem;
    private Node projectNode  = null;

    public ProjectItemNode(ProjectItem projectItem, BeanTreeView beanTreeView) {
        super(Children.create(
                new TaskItemChildren(projectItem, beanTreeView), false), 
                Lookups.singleton(projectItem));
        this.projectItem = projectItem;
        Project project = null;        
        
        String projectPath = projectItem.getItemPath();
        if (null != projectPath) {
            File file = new File(projectPath);
            if (file.isDirectory()) {
                FileObject fileObject = FileUtil.toFileObject(file);

                try {
                    project = ProjectManager.getDefault().findProject(fileObject);
                } catch (IOException ex) {
                    logger.log(Level.FINE, "Exception while findProject", ex);
                } catch (IllegalArgumentException ex) {
                    logger.log(Level.FINE, "Exception while findProject", ex);
                }
                DataObject dataObject;
                try {
                    dataObject = DataObject.find(fileObject);
                    this.projectNode = dataObject.getNodeDelegate();
                } catch (DataObjectNotFoundException ex) {
                    logger.log(Level.FINE, "Exception.", ex);
                }
            }
        }
        String projectName;
        if( null != project ) {
            ProjectInformation info = project.getLookup().lookup(ProjectInformation.class);
            projectName = info.getName();
        } else {
            projectName = projectItem.getItemPath();
        }
        setDisplayName(projectName);
        setShortDescription(projectItem.getItemPath());
        
        projectItem.addPropertyChangeListener(WeakListeners.propertyChange(this, projectItem));
     
    }

    @Override
    public Image getIcon(int type) {
        if( null != projectNode) {
            return projectNode.getIcon(type);
        }
        if( projectItem.isSticky()) {            
            return ImageUtilities.loadImage(IMAGEPATH_STICKY_ICON);
        } else {
            return ImageUtilities.loadImage(IMAGEPATH_ICON);
        }
    }

    @Override
    public Image getOpenedIcon(int type) {
        if( null != projectNode) {
            return projectNode.getOpenedIcon(type);
        }
        if( projectItem.isSticky()) {            
            return ImageUtilities.loadImage(IMAGEPATH_STICKY_ICON);
        } else {
            return ImageUtilities.loadImage(IMAGEPATH_ICON);
        }
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actionList = new ArrayList<Action>();
        
        return actionList.toArray(new Action[0]);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireIconChange();
    }

    @Override
    public String getHtmlDisplayName() {
        return "<b>" + getDisplayName() + "</b>";
    }

    Node findNode(FileObject objectToSelect) {
        Node[] nodes = getChildren().getNodes();
        for( Node node : nodes) {
            TaskItemNode taskItemNode = (TaskItemNode) node;
            Node foundNode = taskItemNode.findNode(objectToSelect);
            if( null != foundNode ) {
                return foundNode;
            }
        }
        return null;
    }
    
    
}
