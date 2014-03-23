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

import com.nbtaskfocus.model.api.Task;
import com.nbtaskfocus.model.api.TaskList;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tushar Joshi <tusharvjoshi@gmail.com>
 */
public final class TaskListPersister {

    private static final Logger logger =
            Logger.getLogger(TaskListPersister.class.getName());
    public static final String NBTASKFOCUS_CONFIG_FOLDER =
            "nbtaskfocus"; //NOI18N
    public static final String NBTASKFOCUS_DATAFILE =
            "taskdata.dat"; //NOI18N
    private static RequestProcessor requestProcessor =
            new RequestProcessor("TaskList Persister"); //NOI18N
    private static PersisterProcess process = new PersisterProcess();
    private static volatile boolean processBusy = false;

    private static List<Task> createLocalTaskList() {
        List<Task> list = new ArrayList<Task>();
        int count = TaskList.getDefault().getCount();
        for (int i = 0; i < count; i++) {
            Task task = TaskList.getDefault().getTask(i);
            list.add(task);
        }
        return list;
    }

    private static FileObject getConfigFolder() {
        FileObject configRoot = FileUtil.getConfigRoot();
        FileObject configFolder =
                configRoot.getFileObject(NBTASKFOCUS_CONFIG_FOLDER);
        if (null == configFolder) {
            try {
                configFolder =
                        configRoot.createFolder(NBTASKFOCUS_CONFIG_FOLDER);
            } catch (IOException ex) {
                logger.log(Level.FINE,
                        "Exception while getting config folder.", ex);
            }
        }
        return configFolder;
    }

    private static FileObject getTaskDataFile(boolean create) {
        FileObject configFolder = getConfigFolder();
        FileObject taskData = null;
        try {
            taskData = configFolder.getFileObject(NBTASKFOCUS_DATAFILE);

            if (create) {
                if (null != taskData) {
                    taskData.delete();
                }
                taskData = configFolder.createData(NBTASKFOCUS_DATAFILE);
            }
        } catch (IOException ex) {
            logger.log(Level.FINE, "Exception while getting data file.", ex);
        }
        return taskData;
    }

    private static List<Task> readTaskList(FileObject taskData) {
        List<Task> list = null;
        ObjectInputStream oin = null;
        try {
            InputStream inputStream = taskData.getInputStream();
            oin = new ObjectInputStream(inputStream);
            list = (List<Task>) oin.readObject();
        } catch (Exception ex) {
            logger.log(Level.FINE, "Exception while reading task list.", ex);
        } finally {
            if (null != oin) {
                try {
                    oin.close();
                } catch (IOException ex) {
                    logger.log(Level.FINE,
                            "Exception while closing stream.", ex);
                }
            }
        }
        return list;
    }

    private static void addToTaskList(List<Task> list) {
        for (Task task : list) {
            task.setActive(false);
            TaskList.getDefault().addTask(task);
        }
    }

    private static void writeTaskList(FileObject taskData) {
        ObjectOutputStream oout = null;
        try {
            OutputStream outputStream = taskData.getOutputStream();
            oout = new ObjectOutputStream(outputStream);
            List<Task> list = createLocalTaskList();
            oout.writeObject(list);
        } catch (Exception ex) {
            logger.log(Level.FINE, "Exception while writing task list.", ex);
        } finally {
            if (null != oout) {
                try {
                    oout.close();
                } catch (IOException ex) {
                    logger.log(Level.FINE,
                            "Exception while closing stream.", ex);
                }
            }
        }
    }

    private static class PersisterProcess implements Runnable {

        @Override
        public void run() {
            if (!processBusy) {
                processBusy = true;
                /*
                 * perform the saving task
                 */
                //TODO; add logger
                //System.out.println("Saving Task List" + new Date().toString());
                TaskListPersister.storeTaskList();
                /*
                 * schedule for next run after certain time period
                 */
                requestProcessor.post(this, 30000);

                processBusy = false;
            }
        }
    }

    public static void loadTaskList() {

        FileObject taskData = getTaskDataFile(false);

        List<Task> list = null;

        if (null != taskData) {
            list = readTaskList(taskData);
        }

        if (null != list) {
            addToTaskList(list);
        }
    }

    public static void storeTaskList() {

        FileObject taskData = getTaskDataFile(true);

        if (null != taskData) {
            writeTaskList(taskData);
        }
    }

    public static void startScheduler() {
        /*
         * schedule for next run after certain time period
         */
        requestProcessor.post(process, 1000);
    }
}
