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
package com.nbtaskfocus.idutil;

import com.nbtaskfocus.model.api.Task;
import com.nbtaskfocus.model.api.TaskIdProvider;
import com.nbtaskfocus.model.api.TaskList;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
@ServiceProvider(service=TaskIdProvider.class)
public class TaskIdProviderImpl implements TaskIdProvider {
    
    public static final String TASK_PREFIX = "Task"; //NOI18N

    @Override
    public String getNextTaskId() {
        TaskList taskList = TaskList.getDefault();
        int count = taskList.getCount();
        int maxId = 0;
        for( int i = 0 ; i < count; i++ ) {
            Task task = taskList.getTask(i);
            if( task.getTaskId().startsWith(TASK_PREFIX)) {
                String idText = task.getTaskId().substring(4);
                int idNum = 0;
                try {
                    idNum = Integer.parseInt(idText);
                } catch( Exception ex) {
                    
                }
                if( maxId < idNum) {
                    maxId = idNum;
                }
            }
        }
        maxId++;
        
        return TASK_PREFIX+maxId;
    }
    
}
