/*
 * The MIT License
 *
 * Copyright 2011 tushar_joshi.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.nbtaskfocus.model.internal;

import org.netbeans.junit.NbTestCase;


/**
 *
 * @author tushar_joshi
 */
public class PathTreeTest extends NbTestCase {
    
    public PathTreeTest(String testName) {
        super(testName);
    }
    
    public void testPathTree() {
        PathTree pathTree = new PathTree();
        pathTree.addPath("a\\b\\c\\d");
        pathTree.addPath("a\\b\\c");
        pathTree.addPath("a\\b\\r\\g");
        
        pathTree.removePath("a\\b\\r\\g");
    }
    
    public void testCreateIncrementalPaths() {
        PathTree pathTree = new PathTree();
        String[] createIncrementalPaths = PathTree.createIncrementalPaths("a\\b\\c\\d");
        
        assertEquals("a",createIncrementalPaths[0]);
        assertEquals("a\\b",createIncrementalPaths[1]);
        assertEquals("a\\b\\c",createIncrementalPaths[2]);
        assertEquals("a\\b\\c\\d",createIncrementalPaths[3]);
    }
    
    public void testIsPathActive() {
        PathTree pathTree = new PathTree();
        pathTree.addPath("/src/com/meterware/httpunit/javascript/JavaScript.java");
        pathTree.addPath("/src/Hello.java");
        
        assertEquals(true, pathTree.isPathActive("/src/com/meterware/httpunit/javascript/JavaScript.java"));
        assertEquals(true, pathTree.isPathActive("/src/Hello.java"));
        
        assertEquals(false, pathTree.isPathActive("/src/com/meterware/httpunit/javascript/Script.java"));
        assertEquals(false, pathTree.isPathActive("/src/com/meterware/httpunit/Script.java"));
        
    }
    
}
