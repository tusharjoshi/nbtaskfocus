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
package com.nbtaskfocus.model.internal;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public class PathTree implements Serializable {

    private Map<String, String> pathMap = new HashMap<String, String>();

    private int countPartialOccurence(String path) {
        int count = 0;
        Set<String> keySet = pathMap.keySet();
        for (String part : keySet) {
            if (part.indexOf(path) > -1) {
                count++;
            }
        }

        return count;
    }

    private static String[] splitPath(String path) {
        File filePath = new File(path);
        List<String> parts = new ArrayList<String>();

        while (filePath != null) {
            parts.add(filePath.getName());
            filePath = filePath.getParentFile();
        }
        Collections.reverse(parts);

        return parts.toArray(new String[0]);
    }

    static String[] createIncrementalPaths(String path) {
        String[] pathParts = splitPath(path);
        List<String> incPaths = new ArrayList<String>();

        String recurringPath = "";
        for (String part : pathParts) {
            if ("".equals(recurringPath)) {
                recurringPath = part;
            } else {
                recurringPath = recurringPath + File.separator + part;
            }
            incPaths.add(recurringPath);
        }

        return incPaths.toArray(new String[0]);
    }

    public void addPath(String path) {
        String[] pathParts = createIncrementalPaths(path);
        for (String part : pathParts) {
            pathMap.put(part, part);
        }
    }

    public void removePath(String path) {
        String[] pathParts = createIncrementalPaths(path);
        for (int i = pathParts.length - 1; i >= 0; i--) {
            String part = pathParts[i];
            if (countPartialOccurence(part) == 1) {
                pathMap.remove(part);
            } else {
                break;
            }
        }
    }

    public boolean isPathActive(String path) {

        String[] pathParts = splitPath(path);
        String ourPath = "";
        for (String part : pathParts) {
            if ("".equals(ourPath)) {
                ourPath = part;
            } else {
                ourPath = ourPath + File.separator + part;
            }
        }

        return (null != pathMap.get(ourPath));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        Set<String> keySet = pathMap.keySet();
        for (String part : keySet) {
            builder.append("\n");
            builder.append(part);
        }

        return builder.toString();
    }

    public void clear() {
        pathMap.clear();
    }
}
