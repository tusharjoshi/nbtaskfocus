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
package com.nbtaskfocus.model.api;

import org.openide.util.Lookup;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public final class TaskIdGenerator {

    private static TaskIdProvider taskIdProvider = null;

    public static String getNextTaskId() {

        if (null == taskIdProvider) {
            taskIdProvider = Lookup.getDefault().lookup(TaskIdProvider.class);
            if (null == taskIdProvider) {
                taskIdProvider = new TaskIdProviderInternal();
            }
        }

        return taskIdProvider.getNextTaskId();
    }

    private static class TaskIdProviderInternal implements TaskIdProvider {

        private static int index;

        @Override
        public String getNextTaskId() {
            index++;
            return "Task" + index;  //NOI18N
        }
    }
}
