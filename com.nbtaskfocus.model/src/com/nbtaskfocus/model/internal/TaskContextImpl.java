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
package com.nbtaskfocus.model.internal;

import com.nbtaskfocus.model.api.ProjectItem;
import com.nbtaskfocus.model.api.TaskContext;
import com.nbtaskfocus.model.api.TaskItem;
import java.io.Serializable;
import java.util.*;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class TaskContextImpl implements TaskContext, Serializable {

    private final List<TaskItem> itemList;
    private final Map<ProjectItem, List<TaskItem>> projectMap =
            new HashMap<ProjectItem, List<TaskItem>>();
    private final ProjectItem externalResources;
    private final List<TaskItem> externalItemList;
    
    private final PathTree pathTree = new PathTree();

    public TaskContextImpl() {
        itemList = new ArrayList<TaskItem>();
        externalItemList = new ArrayList<TaskItem>();
        externalResources = new ProjectItemImpl();
        externalResources.setItemPath(
                NbBundle.getMessage(TaskContextImpl.class, 
                "ND_ExternalResources"));
    }

    @Override
    public List<ProjectItem> getProjectItems() {
        List<ProjectItem> list = new ArrayList<ProjectItem>();
        Set<ProjectItem> keySet = projectMap.keySet();
        if (null != keySet) {
            list.addAll(keySet);
        }
        return list;
    }

    @Override
    public void addTaskItem(String itemPath, String projectPath) {
        if (!contains(itemPath)) {
            TaskItem taskItem = new TaskItemImpl();
            taskItem.setProjectPath(projectPath);
            taskItem.setItemPath(itemPath);
            if( null != projectPath ) {
                String name = itemPath.substring(projectPath.length());
                taskItem.setItemName(name);
            } else {
                taskItem.setItemName(itemPath);
            }
            taskItem.setOpened(true);
            taskItem.setSticky(false);

            itemList.add(taskItem);
            pathTree.addPath(itemPath);

            addToProjectItem(taskItem);

        } else {
            for (TaskItem item : itemList) {
                if (item.getItemPath().equals(itemPath)) {
                    item.setOpened(true);
                }
            }
        }
    }

    @Override
    public boolean removeTaskItem(String itemPath) {
        TaskItem keyItem = null;
        for (TaskItem item : itemList) {
            if (item.getItemPath().equals(itemPath)) {
                keyItem = item;
                break;
            }
        }

        if (null != keyItem) {
            if (!keyItem.isSticky()) {
                removeFromProjectItem(keyItem);
                itemList.remove(keyItem);                
                pathTree.removePath(itemPath);
                return true;
            } else {
                keyItem.setOpened(false);
            }
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public TaskItem getTaskItem(int index) {
        return itemList.get(index);
    }

    @Override
    public void clear() {
        itemList.clear();
        pathTree.clear();
    }

    @Override
    public boolean contains(FileObject primaryFile) {
        String path = primaryFile.getPath();
        for (TaskItem item : itemList) {
            if (item.getItemPath().startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ProjectItem getExternalResources() {
        return externalResources;
    }
    
    @Override
    public boolean isItemActive(String path) {
        return pathTree.isPathActive(path);
    }

    private boolean contains(String itemPath) {
        TaskItem keyItem = null;
        for (TaskItem item : itemList) {
            if (item.getItemPath().equals(itemPath)) {
                keyItem = item;
                break;
            }
        }

        return (null != keyItem);
    }

    private void removeFromProjectItem(TaskItem taskItem) {
        String projectPath = taskItem.getProjectPath();
        if (null != projectPath) {
            ProjectItem projectItem = findProject(projectPath);
            if (null != projectItem) {
                List<TaskItem> tempList = projectMap.get(projectItem);
                if (null != tempList) {
                    tempList.remove(taskItem);
                    ((ProjectItemImpl) projectItem).firePropertyChange(
                            ProjectItem.EVENT_ITEM_REMOVED,
                            null, taskItem);
                    if (tempList.isEmpty() && !projectItem.isSticky()) {
                        projectMap.remove(projectItem);
                    }
                }
            }
        } else {
            externalItemList.remove(taskItem);
            ((ProjectItemImpl) externalResources).firePropertyChange(
                    ProjectItem.EVENT_ITEM_REMOVED,
                    null, taskItem);
        }
    }

    private void addToProjectItem(TaskItem taskItem) {
        String projectPath = taskItem.getProjectPath();
        if (null != projectPath) {
            ProjectItem projectItem = findProject(projectPath);
            List<TaskItem> tempList;
            if (null == projectItem) {
                projectItem = new ProjectItemImpl();
                projectItem.setItemPath(projectPath);
                tempList = new ArrayList<TaskItem>();
                projectMap.put(projectItem, tempList);
            } else {
                tempList = projectMap.get(projectItem);
            }
            if (null == tempList) {
                tempList = new ArrayList<TaskItem>();
                projectMap.put(projectItem, tempList);
            }
            tempList.add(taskItem);
            ((ProjectItemImpl) projectItem).firePropertyChange(
                    ProjectItem.EVENT_ITEM_ADDED,
                    null, taskItem);
        } else {
            externalItemList.add(taskItem);
            ((ProjectItemImpl) externalResources).firePropertyChange(
                    ProjectItem.EVENT_ITEM_ADDED,
                    null, taskItem);
        }

    }

    private ProjectItem findProject(String projectPath) {
        if (null != projectPath) {
            for (ProjectItem item : getProjectItems()) {
                if (projectPath.equals(item.getItemPath())) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public List<TaskItem> getTaskItems(ProjectItem projectItem) {
        if( projectItem == externalResources) {
            return externalItemList;
        } else {
            return projectMap.get(projectItem);
        }
    }
}
