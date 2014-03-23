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

import com.nbtaskfocus.core.ui.actions.AddTaskAction;
import java.awt.Image;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class TaskRootNode extends AbstractNode {
    public static final String IMAGEPATH_ICON = "com/nbtaskfocus/core/ui/resources/tasklist_icon.png";
    public static final String IMAGEPATH_OPENEDICON = "com/nbtaskfocus/core/ui/resources/tasklist_icon.png";

    public TaskRootNode() {
        super(Children.create(new TaskChildren(), true));
        setDisplayName(NbBundle.getMessage(TaskRootNode.class, "ND_TaskList"));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(IMAGEPATH_ICON);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return ImageUtilities.loadImage(IMAGEPATH_OPENEDICON);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new AddTaskAction()};
    }
}
