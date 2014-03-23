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
package com.nbtaskfocus.core.ui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerManager.Provider;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;

/**
 * <p>
 * <code>ContextTreeView</code> class is based on the implementation done in the
 * <code>ProjectTab</code> class found in the projectui module. It contains
 * utility methods to save the expanded nodes and restore them back.</p>
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class CustomTreeView extends BeanTreeView {
    
    private final ExplorerManager.Provider explorerManagerProvider;
    
    public CustomTreeView(ExplorerManager.Provider explorerManagerProvider ){
        this.explorerManagerProvider = explorerManagerProvider;
    }

    public Provider getExplorerManagerProvider() {
        return explorerManagerProvider;
    }
    
    public void scrollToNode(Node n) {
        TreeNode tn = Visualizer.findVisualizer(n);
        if (tn == null) {
            return;
        }

        TreeModel model = tree.getModel();
        if (!(model instanceof DefaultTreeModel)) {
            return;
        }

        TreePath path = new TreePath(((DefaultTreeModel) model).getPathToRoot(tn));
        Rectangle r = tree.getPathBounds(path);
        if (r != null) {
            tree.scrollRectToVisible(r);
        }
    }

    public List<String[]> getExpandedPaths() {

        List<String[]> result = new ArrayList<String[]>();

        TreeNode rtn = Visualizer.findVisualizer(getRootContext());
        TreePath tp = new TreePath(rtn); // Get the root

        Enumeration<TreePath> paths = tree.getExpandedDescendants(tp);
        if (null != paths) {
            while (paths.hasMoreElements()) {
                TreePath ep = paths.nextElement();
                Node en = Visualizer.findNode(ep.getLastPathComponent());
                String[] path = NodeOp.createPath(en, getRootContext());  
                result.add(path);
            }
        }

        return result;

    }

    /** Expands all the paths, when exists
         */
    public void expandNodes(List<String[]> exPaths) {
        for (Iterator<String[]> it = exPaths.iterator(); it.hasNext();) {
            String[] sp = it.next();
            TreePath tp = stringPath2TreePath(sp);

            if (tp != null) {
                showPath(tp);
            }
        }
    }
    
    /*
    private void printPaths(String[] paths) {
        System.out.println("");
        for( String path : paths) {
            System.out.print(path);
            System.out.print("/");
        }
    }
    */
    
    private Node getRootContext() {
        return explorerManagerProvider.getExplorerManager().getRootContext();
    }

    /** Converts path of strings to TreePath if exists null otherwise
         */
    private TreePath stringPath2TreePath(String[] sp) {

        try {
            Node n = NodeOp.findPath(getRootContext(), sp);

            // Create the tree path
            TreeNode tns[] = new TreeNode[sp.length + 1];

            for (int i = sp.length; i >= 0; i--) {
                tns[i] = Visualizer.findVisualizer(n);
                n = n.getParentNode();
            }
            return new TreePath(tns);
        } catch (NodeNotFoundException e) {
            return null;
        }
    }
}
