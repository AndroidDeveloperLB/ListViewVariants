ListViewVariants
================

Provides special ways to handle ListViews, including PinnedHeaderListView in Lollipop's Contacts-app style

Screenshot
----------
Here's a screenshot of how it shows the contacts of the device, very similar to how Lollipop's Contacts-app shows it , except for the blur, which I've added myself ... :)  

![enter image description here](https://raw.githubusercontent.com/AndroidDeveloperLB/ListViewVariants/master/device-2014-12-28-230610.png)

Requirements
------------
The min API level is 8.

Also, in case you wish to use any class that causes the headers to be ordered, you must sort the items accordingly.

In case the headers should all be in uppercase, the sorting should make sure that items that start with each letter (uppercase or not) will be together (for example "Dan" and "duke" should be together on the same chunk that belongs to "D").

Known Issues
------------

Just missing some documentations and also some samples. Hope to work on this when I get the time. :)

