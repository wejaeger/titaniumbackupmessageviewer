/*
 * $Id$
 *
 * File:   TitaniumBackupMessageViewer.java
 * Author: Werner Jaeger
 *
 * Created on Feb 25, 2015, 11:10:38 AM
 *
 * Copyright (C) 2015 Werner Jaeger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wj.android.messageviewer.gui;

import com.wj.android.messageviewer.gui.actions.AboutAction;
import com.wj.android.messageviewer.gui.actions.ExportAllMessagesAction;
import com.wj.android.messageviewer.gui.actions.ExportSelectedMessagesAction;
import com.wj.android.messageviewer.gui.actions.HelpAction;
import com.wj.android.messageviewer.gui.actions.OpenAction;
import com.wj.android.messageviewer.gui.actions.OpenRecentFileAction;
import com.wj.android.messageviewer.gui.actions.QuitAction;
import com.wj.android.messageviewer.util.RecentCollection;
import com.wj.android.messageviewer.util.Pair;
import com.wj.android.messageviewer.gui.workers.DisplayThreadMessageWorker;
import com.wj.android.messageviewer.gui.workers.LoadMessagesWorker;
import com.wj.android.messageviewer.message.MessageThread;
import com.wj.android.messageviewer.resources.Resources;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Main class and window for this application.
 *
 * <p>
 *    {@code TitaniumBackupMessageViewer} implements a threaded messaging
 *    viewer. that means it displays both sides of a messaging (SMS/MMS)
 *    conversation on one right screen side , in chronological order. On the
 *    left side a list box of message threads is displayed. In this
 *     way it groups messages from the same person and phone number together.
 * <p/>
 *
 * @author >Werner Jaeger
 */
public final class TitaniumBackupMessageViewer
{
   private static class AbstractListModelImpl extends AbstractListModel<MessageThread>
   {
      private static final long serialVersionUID = -3879053127396522633L;
      final MessageThread m_Thread;

      public AbstractListModelImpl()
      {
         m_Thread = new MessageThread("Threads load here...", "");
      }

      @Override
      public int getSize()
      {
         return(1);
      }

      @Override
      public MessageThread getElementAt(int iIndex)
      {
         return(m_Thread);
      }
   }

   private static final Logger LOGGER = Logger.getLogger(TitaniumBackupMessageViewer.class.getName());

   private static final Preferences PREFERENCES = Preferences.userNodeForPackage(TitaniumBackupMessageViewer.class);

   private static final String recentFilesKey = "recentfile";
   private static final String windowXKey = "window.x";
   private static final String windowYKey = "window.y";
   private static final String windowWidthKey = "window.width";
   private static final String windowHeightKey = "window.height";
   private static final String windowStateKey = "window.state";
   private static final String windowVisibleKey = "window.visible";

   private final RecentCollection<Pair<String, String>> m_RecentCollection;

   private JFrame m_ReaderFrame;
   private JTextField m_NumberSMSField;
   private JMenu m_mnRecentFiles;
   private JList<MessageThread> m_ThreadListBox;
   private MessageViewer m_MessageViewer;

   /**
    * Creates new {@code GUI}.
    *
    * @param strMessageFileName the full pathname to a message file that should
    *        be loaded or {@code null} if no message file should be loaded.
    * @param strContactsDBFileName the SQLLite contacts database that should be
    *        used to query contact names from message addresses or {@code null}
    *        if no contacts database should be used.
    */
   public TitaniumBackupMessageViewer(final String strMessageFileName, final String strContactsDBFileName)
   {
      m_RecentCollection = new RecentCollection<>(PREFERENCES, recentFilesKey);

      initialize(new Pair<>(strMessageFileName, strContactsDBFileName));
   }

   /**
    * Launch the application and display the main window.
    *
    * @param asArgs the first optional argument is the full path to the message
    *               file to open at start up, the second optional argument is
    *               the full path to a contacts database file.
    */
   public static void main(final String[] asArgs)
   {
      EventQueue.invokeLater(new Runnable()
      {
         boolean fIsVisible = PREFERENCES.getBoolean(windowVisibleKey, true);

         @Override
         public void run()
         {
            if (asArgs.length == 1)
               new TitaniumBackupMessageViewer(asArgs[0], null).m_ReaderFrame.setVisible(fIsVisible);
            else if (asArgs.length == 2)
               new TitaniumBackupMessageViewer(asArgs[0], asArgs[1]).m_ReaderFrame.setVisible(fIsVisible);
            else if (asArgs.length == 0)
               new TitaniumBackupMessageViewer(null, null).m_ReaderFrame.setVisible(fIsVisible);
         }
      });
   }

   /**
    * Called to notify that messages are successfully loaded from file.
    *
    * <p>
    *    Adds the read thread to the thread list box, set the number of SMS
    *    field, clears the message viewer and add the files from where the
    *    messages where loaded to the recent file list.
    * </p>
    *
    * @param treads the thread containing the messages that where read from
    *        the specified file
    *
    * @param iNoOfMessages the total number of messages read
    * @param files2load the files (message file and optionally contacts
    *        database) where messages were loaded from
    */
   public void onMessagesLoaded(final MessageThread[] treads, final int iNoOfMessages, final Pair<String, String> files2load)
   {
      m_ThreadListBox.clearSelection();
      m_ThreadListBox.setListData(treads);
      m_ThreadListBox.setEnabled(true);
      if (0 < m_ThreadListBox.getModel().getSize())
         m_ThreadListBox.setSelectedIndex(0);

      m_NumberSMSField.setText(Integer.toString(iNoOfMessages));
      m_MessageViewer.clear();

      m_RecentCollection.add(files2load);
      syncRecentFiles();
   }

   /**
    * Called to notify that loading messages from the specified files failed
    * because the specified  message and/or contacts database file were not
    * found.
    *
    * <p>
    *    Removes the files not found from the recent files list.
    * </p>
    * @param files2load
    */
   public void onMessageFileNotFound(final Pair<String, String> files2load)
   {
      m_RecentCollection.remove(files2load);
      syncRecentFiles();
   }

   /**
    * This method is called from within the constructor to initialize the form.
    */
   private void initialize(Pair<String, String> files2Open)
   {
      m_ReaderFrame = new JFrame();
      m_ReaderFrame.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(final WindowEvent windowEvent)
         {
            savePreferences();
         }
      });

      m_ReaderFrame.setTitle(Resources.getApplicationName());
      m_ReaderFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      initReaderFrameFromPrefs();

      m_ReaderFrame.setIconImages(Resources.getFrameIcons());

      final JLabel lblNumberOfSms = new JLabel("Number of SMS:");

      m_NumberSMSField = new JTextField();
      m_NumberSMSField.setEditable(false);
      m_NumberSMSField.setColumns(10);
      m_NumberSMSField.setBorder(BorderFactory.createEmptyBorder());

      m_MessageViewer = new MessageViewer();
      m_MessageViewer.setBackground(m_ReaderFrame.getContentPane().getBackground());
      JScrollPane scrollPaneMessages = new JScrollPane(m_MessageViewer);
      scrollPaneMessages.getVerticalScrollBar().setUnitIncrement(10);

      m_ThreadListBox = new MessageThreadList(300);
      m_ThreadListBox.setBackground(m_ReaderFrame.getContentPane().getBackground());
      m_ThreadListBox.addListSelectionListener(new ListSelectionListener()
      {
         /** @inherited */
         @Override
         public void valueChanged(final ListSelectionEvent evt)
         {
            threadListValueChanged(evt);
         }
      });

      JScrollPane scrollPaneThread = new JScrollPane(m_ThreadListBox);
      m_ThreadListBox.setModel(new AbstractListModelImpl());

      m_ThreadListBox.setEnabled(false);
      m_ThreadListBox.setBorder(new EtchedBorder(1, null, null));

      createMenu();

      final Box h1Box = Box.createHorizontalBox();
      h1Box.add(Box.createVerticalStrut(lblNumberOfSms.getPreferredSize().height * 4));
      h1Box.add(lblNumberOfSms);
      h1Box.add(Box.createHorizontalStrut(10));
      h1Box.add(m_NumberSMSField);
      h1Box.setMaximumSize(h1Box.getPreferredSize());
      h1Box.setAlignmentX(Component.LEFT_ALIGNMENT);

      final Box h2Box = Box.createHorizontalBox();
      h2Box.add(scrollPaneThread);
      h2Box.add(Box.createHorizontalStrut(10));
      h2Box.add(scrollPaneMessages);
      h2Box.setAlignmentX(Component.LEFT_ALIGNMENT);

      m_ReaderFrame.add(h1Box, BorderLayout.PAGE_START);
      m_ReaderFrame.add(h2Box, BorderLayout.CENTER);

      scrollPaneThread.setMinimumSize(new Dimension(100,  100));
      scrollPaneThread.setMaximumSize(new Dimension(m_ThreadListBox.getPreferredSize().width, Short.MAX_VALUE));
      scrollPaneMessages.setMinimumSize(new Dimension(300, 100));

      if (null != files2Open.getFirst())
         new LoadMessagesWorker(this, m_ReaderFrame, files2Open).execute();
   }

   private void createMenu()
   {
      final JMenuBar menuBar = new JMenuBar();
      m_ReaderFrame.setJMenuBar(menuBar);

      final JMenu mnFile = new JMenu("File");
      menuBar.add(mnFile);

      mnFile.add(new JMenuItem(new OpenAction(this, m_ReaderFrame)));

      createRecentFileMenu(mnFile);

      mnFile.add(new JSeparator()); // SEPARATOR
      mnFile.add(new JMenuItem(new ExportSelectedMessagesAction(m_ReaderFrame, m_ThreadListBox)));
      mnFile.add(new JMenuItem(new ExportAllMessagesAction(m_ReaderFrame, m_ThreadListBox)));
      mnFile.add(new JSeparator()); // SEPARATOR
      mnFile.add(new JMenuItem(new QuitAction(m_ReaderFrame)));

      final JMenu mnHelp = new JMenu("Help");
      menuBar.add(mnHelp);

      mnHelp.add(new JMenuItem(new HelpAction()));
      mnHelp.add(new JMenuItem(new AboutAction(m_ReaderFrame)));
   }

   private void createRecentFileMenu(final JMenu mnFile)
   {
      if (m_RecentCollection.loadFromPreferences())
      {
         m_mnRecentFiles = new JMenu("Open Recent File");

         syncRecentFiles();

         mnFile.add(m_mnRecentFiles);
      }
   }

   /**
    * Synchronizes recent files menu with preferences.
    *
    * <p>
    *    Populates recent file menu items form stored preferences.
    * </p>
    */
   private void syncRecentFiles()
   {
      if (null != m_mnRecentFiles)
      {
         m_mnRecentFiles.removeAll();

         for (final Pair<String, String> pair : m_RecentCollection)
            m_mnRecentFiles.add(new JMenuItem(new OpenRecentFileAction(this, m_ReaderFrame, pair)));
      }
   }

   private void threadListValueChanged(final ListSelectionEvent evt)
   {
      if (!evt.getValueIsAdjusting())
         new DisplayThreadMessageWorker(m_ReaderFrame, m_ThreadListBox.getSelectedValue(), m_MessageViewer).execute();
   }

   private void initReaderFrameFromPrefs()
   {
      final int ix = PREFERENCES.getInt(windowXKey, 100);
      final int iy = PREFERENCES.getInt(windowYKey, 100);
      final int iWidth = PREFERENCES.getInt(windowWidthKey, 700);
      final int iHeight = PREFERENCES.getInt(windowHeightKey, 500);
      final int iState = PREFERENCES.getInt(windowStateKey, JFrame.NORMAL);
      m_ReaderFrame.setLocation(ix, iy);
      m_ReaderFrame.setSize(iWidth, iHeight);
      m_ReaderFrame.setExtendedState(iState);
   }

   private void savePreferences()
   {
      if (JFrame.MAXIMIZED_BOTH != m_ReaderFrame.getExtendedState())
      {
         PREFERENCES.putInt(windowXKey, m_ReaderFrame.getLocation().x);
         PREFERENCES.putInt(windowYKey, m_ReaderFrame.getLocation().y);
         PREFERENCES.putInt(windowWidthKey, m_ReaderFrame.getWidth());
         PREFERENCES.putInt(windowHeightKey, m_ReaderFrame.getHeight());
      }

      PREFERENCES.putInt(windowStateKey, m_ReaderFrame.getExtendedState());
      PREFERENCES.putBoolean(windowVisibleKey, m_ReaderFrame.isVisible());

      try
      {
         PREFERENCES.flush();
      }
      catch (final BackingStoreException ex)
      {
          LOGGER.log(Level.SEVERE, ex.toString(), ex);
      }
   }
}