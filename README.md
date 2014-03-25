NBTaskFocus
===========

## What is Task Focused Interface

Task Focused Interface is a way of working in IDE which makes the IDE aware 
of Tasks. Task is a concept of something developer is working on to achieve 
a target issue fix or enhancement addition to the software. All the files 
which are required for a certain task are added to Task context and maintained 
with the Task in a Task Focused Interface.

## NBTaskFocus project

This project provides features of Task Focused Interface for NetBeans IDE. 

## Main Features

Task Explorer - A list of tasks which are created by developer
Task Details - Active Task with the tracked context that is open files in the 
editor for that active task 

## Using the features

1. Make sure you open the panels Window > Task Focus > Task Explorer 
and Window > Task Focus > Task Details
1. Right click on the Task List node of Task Explorer and choose Add Task 
to add one task, repeat this to add more tasks
1. Right click on the Task node and select Activate Task to make it activated, 
or double click a Task
1. Open some files from favorites panel and you can see those get added in 
the task details panel as well as nodes
1. Activate another task to see all your opened files get closed and the 
editor area will become empty
1. Open some other files which become context of the currently selected task
1. Now try activating previously active task and see the switch between 
your open files. 

The context tracking works with files included in projects as well as 
independent files opened from Favorites panel. 
