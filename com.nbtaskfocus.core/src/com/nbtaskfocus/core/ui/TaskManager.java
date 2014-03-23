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
package com.nbtaskfocus.core.ui;

import com.nbtaskfocus.model.api.Task;
import java.util.HashMap;
import java.util.Map;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public final class TaskManager {
    
    private static final TaskManager DEFAULT = new TaskManager();
    
    private TaskManager() {
        
    }
    
    public static TaskManager getDefault() {
        return DEFAULT;
    }
    
    private Map<Task, TopComponent> taskMap = new HashMap<Task, TopComponent>();
    
    public TopComponent getTopComponent( Task task ) {
        return taskMap.get(task); 
    }
    
    public TopComponent getOrCreateTopComponent( Task task ) {
        TopComponent topComponent = taskMap.get(task); 
        if( null == topComponent ) {
            topComponent = new TaskEditorTopComponent(task);
            taskMap.put(task, topComponent);
        }
        return topComponent;        
    }
    
    public void removeTopComponent(Task task) {
        TopComponent topComponent = taskMap.get(task); 
        if( null != topComponent ) {
            taskMap.remove(task);
        }
    }
    
}
