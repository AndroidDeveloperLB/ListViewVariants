ListViewVariants
================

Provides special ways to handle ListViews, including PinnedHeaderListView in Lollipop's Contacts' app style

Screenshot
----------
![enter image description here](https://raw.githubusercontent.com/AndroidDeveloperLB/ListViewVariants/master/device-2014-12-28-230610.png)

Known Issues
------------

 1. Missing some documentations and also some samples. Hope to work on this when I get the time. :)
 2. There is a huge issue on a very specific case, which I'm not sure what causes it (posted about it [here](http://stackoverflow.com/q/27676367/878126), in case you can help) :
   - The device is set to an RTL locale (like Hebrew).
  - The listView has a lot of items, and some of them are in RTL language (like Hebrew)
  - Occurs on some devices and/or Android versions, but not on all. 
  
  The bug that occurs is that the fast-scrollbar goes "crazy" when using it (or when scrolling normally), meaning that, for example, if you scroll to the top, it might just stop at the middle. Another example is that when you scroll down, it might disappear and appear later...
  Another bug that's related to the same case (and can appear together with it or instead of it) is that the headers won't appear at all, except for the pinned header.
