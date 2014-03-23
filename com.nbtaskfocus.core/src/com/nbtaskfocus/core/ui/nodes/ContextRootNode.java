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
package com.nbtaskfocus.core.ui.nodes;

import java.awt.Image;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class ContextRootNode extends FilterNode {
    
    public ContextRootNode(Node original) {
        super(original,new ContextNode.ContextChildren(original) );
    }

    @Override
    public String getHtmlDisplayName() {
        String label = "<font color='#c2c2c2'>" + getDisplayName() + "</font>";
        return label;
    }

    @Override
    public Image getIcon(int type) {
        Image icon = super.getIcon(type);        
        return ImageUtilities.createDisabledImage(icon);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image icon = super.getOpenedIcon(type);        
        return ImageUtilities.createDisabledImage(icon);
    }
    
    public ContextNode.ContextChildren getContextChildren() {
        return (ContextNode.ContextChildren)getChildren();
    }

    public ContextNode findNode(FileObject objectToSelect) {
        Node[] nodes = getChildren().getNodes();
        for( Node node : nodes) {
            ContextNode foundNode = ((ContextNode)node).findNode(objectToSelect);
            if( null != foundNode ) {
                return foundNode;
            }
        }
        return null;
    }
}
