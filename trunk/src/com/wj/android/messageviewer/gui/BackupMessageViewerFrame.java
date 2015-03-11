/*
 * $Id$
 *
 * File:   BackupMessageViewerFrame.java
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

import com.wj.android.messageviewer.BackupMessageViewerApplication;
import com.wj.android.messageviewer.gui.actions.AboutAction;
import com.wj.android.messageviewer.gui.actions.ExportAllMessagesAction;
import com.wj.android.messageviewer.gui.actions.ExportSelectedMessagesAction;
import com.wj.android.messageviewer.gui.actions.HelpAction;
import com.wj.android.messageviewer.gui.actions.OpenAction;
import com.wj.android.messageviewer.gui.actions.OpenRecentFileAction;
import com.wj.android.messageviewer.gui.actions.QuitAction;
import com.wj.android.messageviewer.util.Pair;
import com.wj.android.messageviewer.gui.workers.DisplayThreadMessageWorker;
import com.wj.android.messageviewer.message.MessageThread;
import com.wj.android.messageviewer.resources.Resources;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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
 *    {@code BackupMessageViewerFrame} implements a threaded messaging
 *    viewer. that means it displays both sides of a messaging (SMS/MMS)
 *    conversation on one right screen side , in chronological order. On the
 *    left side a list box of message threads is displayed. In this
 *     way it groups messages from the same person and phone number together.
 * <p/>
 *
 * @author Werner Jaeger
 */
public final class BackupMessageViewerFrame extends JFrame
{
   private static final long serialVersionUID = 2666836619664770903L;

   private static class AbstractListModelImpl extends AbstractListModel<MessageThread>
   {
      private static final long serialVersionUID = -3879053127396522633L;
      private final MessageThread m_Thread;

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
      public MessageThread getElementAt(final int iIndex)
      {
         return(m_Thread);
      }
   }

   private JTextField m_NumberOfMessagesField;
   private JMenu m_mnRecentFiles;
   private JList<MessageThread> m_ThreadListBox;
   private MessageViewer m_MessageViewer;

   /**
    * Creates new {@code BackupMessageViewerFrame}.
    *
    */
   public BackupMessageViewerFrame()
   {
      final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
      final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      final int iEffectiveScreenHeight = screenSize.height - (screenInsets.top + screenInsets.bottom);
      final int iEffectiveScreenWidth = screenSize.width - (screenInsets.left + screenInsets.right);
      int iPreferredWidth = 700;
      int iPreferredHeight = 550;

      if (iEffectiveScreenWidth < iPreferredWidth)
         iPreferredWidth = iEffectiveScreenWidth;

      if (iEffectiveScreenHeight < iPreferredHeight)
         iPreferredHeight = iEffectiveScreenHeight;

      setSize(new Dimension(iPreferredWidth, iPreferredHeight));
      setLocationRelativeTo(null);

      setTitle(Resources.getApplicationName());
      setIconImages(Resources.getFrameIcons());
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      initComponents();
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
    * @param threads the thread containing the messages that where read from
    *        the specified file
    *
    * @param iNoOfMessages the total number of messages read
    * @param files2load the files (message file and optionally contacts
    *        database) where messages were loaded from
    */
   public void onMessagesLoaded(final MessageThread[] threads, final int iNoOfMessages, final Pair<String, String> files2load)
   {
      m_MessageViewer.clear();
      m_ThreadListBox.clearSelection();
      m_ThreadListBox.setListData(threads);
      m_ThreadListBox.setEnabled(true);
      if (0 < m_ThreadListBox.getModel().getSize())
         m_ThreadListBox.setSelectedIndex(0);

      m_NumberOfMessagesField.setText(Integer.toString(iNoOfMessages));

      setTitle(Resources.getApplicationName() + " - " + new File(files2load.getFirst()).getName());
      BackupMessageViewerApplication.getInstance().add2RecentFileList(files2load);
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
    * @param files2load at least on of this pair is not found
    */
   public void onMessageFileNotFound(final Pair<String, String> files2load)
   {
      BackupMessageViewerApplication.getInstance().removeFromRecentFileList(files2load);
      syncRecentFiles();
   }

   /**
    * This method is called from within the constructor to initialize the form.
    */
   private void initComponents()
   {
      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowStateChanged(final WindowEvent windowEvent)
         {
            BackupMessageViewerApplication.getInstance().savePreferences();
         }

         @Override
         public void windowClosing(final WindowEvent windowEvent)
         {
            BackupMessageViewerApplication.getInstance().savePreferences();
            setVisible(false);
         }
      });

      configureNumberOfMessages();
      configureMessageViewer();
      configureThreadList();
      configureMenus();
      configureLayout();
   }

   private void configureNumberOfMessages()
   {
      m_NumberOfMessagesField = new JTextField();
      m_NumberOfMessagesField.setEditable(false);
      m_NumberOfMessagesField.setColumns(10);
      m_NumberOfMessagesField.setBorder(BorderFactory.createEmptyBorder());
   }

   private void configureMessageViewer()
   {
      m_MessageViewer = new MessageViewer();
      m_MessageViewer.setBackground(getContentPane().getBackground());
   }

   private void configureThreadList()
   {
      m_ThreadListBox = new MessageThreadList(300);
      m_ThreadListBox.setBackground(getContentPane().getBackground());
      m_ThreadListBox.addListSelectionListener(new ListSelectionListener()
      {
         /** @inherited */
         @Override
         public void valueChanged(final ListSelectionEvent evt)
         {
            threadListValueChanged(evt);
         }
      });

      m_ThreadListBox.setModel(new AbstractListModelImpl());
      m_ThreadListBox.setEnabled(false);
      m_ThreadListBox.setBorder(new EtchedBorder(1, null, null));
   }

   private void configureMenus()
   {
      final JMenuBar menuBar = new JMenuBar();
      setJMenuBar(menuBar);

      final JMenu mnFile = new JMenu("File");
      menuBar.add(mnFile);

      mnFile.add(new JMenuItem(new OpenAction(this)));

      createRecentFileMenu(mnFile);

      mnFile.add(new JSeparator()); // SEPARATOR
      mnFile.add(new JMenuItem(new ExportSelectedMessagesAction(this, m_ThreadListBox)));
      mnFile.add(new JMenuItem(new ExportAllMessagesAction(this, m_ThreadListBox)));
      mnFile.add(new JSeparator()); // SEPARATOR
      mnFile.add(new JMenuItem(new QuitAction(this)));

      final JMenu mnHelp = new JMenu("Help");
      menuBar.add(mnHelp);

      mnHelp.add(new JMenuItem(new HelpAction()));
      mnHelp.add(new JMenuItem(new AboutAction(this)));
   }

   private void configureLayout()
   {
      final JLabel lblNumberOfMessages = new JLabel("Number of messages:");
      final JScrollPane scrollPaneThread = new JScrollPane(m_ThreadListBox);
      final JScrollPane scrollPaneMessages = new JScrollPane(m_MessageViewer);

      final Box h1Box = Box.createHorizontalBox();
      h1Box.add(Box.createVerticalStrut(lblNumberOfMessages.getPreferredSize().height * 4));
      h1Box.add(lblNumberOfMessages);
      h1Box.add(Box.createHorizontalStrut(10));
      h1Box.add(m_NumberOfMessagesField);
      h1Box.setMaximumSize(h1Box.getPreferredSize());
      h1Box.setAlignmentX(Component.LEFT_ALIGNMENT);

      final Box h2Box = Box.createHorizontalBox();
      h2Box.add(scrollPaneThread);
      h2Box.add(Box.createHorizontalStrut(10));
      h2Box.add(scrollPaneMessages);
      h2Box.setAlignmentX(Component.LEFT_ALIGNMENT);

      add(h1Box, BorderLayout.PAGE_START);
      add(h2Box, BorderLayout.CENTER);

      scrollPaneThread.setMinimumSize(new Dimension(100,  100));
      scrollPaneThread.setMaximumSize(new Dimension(m_ThreadListBox.getPreferredSize().width, Short.MAX_VALUE));
      scrollPaneMessages.getVerticalScrollBar().setUnitIncrement(10);
      scrollPaneMessages.setMinimumSize(new Dimension(300, 100));
   }

   private void createRecentFileMenu(final JMenu mnFile)
   {
      m_mnRecentFiles = new JMenu("Open Recent File");

      syncRecentFiles();

      mnFile.add(m_mnRecentFiles);
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

         for (final Pair<String, String> pair : BackupMessageViewerApplication.getInstance().getRecentFiles())
            m_mnRecentFiles.add(new JMenuItem(new OpenRecentFileAction(this, pair)));
      }
   }

   private void threadListValueChanged(final ListSelectionEvent evt)
   {
      if (!evt.getValueIsAdjusting())
         new DisplayThreadMessageWorker(this, m_ThreadListBox.getSelectedValue(), m_MessageViewer).execute();
   }
}