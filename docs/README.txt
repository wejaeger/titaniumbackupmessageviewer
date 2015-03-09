
Run the stand-alone distribution from the command line using:
        java -jar -Xmx256m AndroidMessageReader-all.jar


Change log:
0.9.0
 - first release

0.9.1
 - removed time offset
 - file chooser dialogs filter contacts database files and message files
 - added tool tips to some file chooser buttons
 - overview.html completed
 - format message dates according to users locale
 - also read messages service center
 - added application icon
 - added thread list box icons
 - thread list box name/address text word wraps now if it is long
 - added version management to build.xml
 - improved about dialog
 - UI redesign, use menu instead of buttons for open and export actions
 - changed application title
 - save/restore main window state in preferences
 - added recent file list to file menu
 - added image/png
 - wait cursor for possibly lengthy actions

0.9.2
 - handle draft messages
 - various bug fixes

0.9.3
 - fixed bug: wrong number of messages displayed in some cases
 - fixed bug: mms are alway the last in order, they do not correctly appear in
              date/time order
 - left and right aligned message bubbles in message viewer, width of bubbles no
   longer fixed, adapts now do the text width.
 - display long recent files path names shortened
 - Now also supports backup message XML files from SMS Backup and Restore App by
   Ritesh (not only Titanium Backup)
