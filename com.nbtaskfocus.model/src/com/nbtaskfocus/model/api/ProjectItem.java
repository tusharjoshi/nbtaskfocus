/*
 * The MIT License
 *
 * Copyright 2011 Tushar Joshi <tusharvjoshi@gmail.com>.
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

import java.beans.PropertyChangeListener;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public interface ProjectItem {
    
    String EVENT_ITEM_ADDED = "ProjectItemAdded"; //NOI18N
    String EVENT_ITEM_REMOVED = "ProjectItemRemoved"; //NOI18N

    String getItemPath();

    void setItemPath(String itemPath);
    
    boolean isSticky();

    void setSticky(boolean sticky);
    
    boolean isOpened();

    void setOpened(boolean opened);    
    
    void addPropertyChangeListener(PropertyChangeListener listener);
    
    void removePropertyChangeListener(PropertyChangeListener listener);
    
}
