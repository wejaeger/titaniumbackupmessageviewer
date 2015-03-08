/*
 * $Id$
 *
 * File:   OpenRecentFileAction.java
 * Author: Werner Jaeger
 *
 * Created on Mar 7, 2015, 11:44:35 AM
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
package com.wj.android.messageviewer.gui.actions;

import com.wj.android.messageviewer.gui.MessageViewer;
import com.wj.android.messageviewer.gui.TitaniumBackupMessageViewer;
import com.wj.android.messageviewer.gui.workers.LoadMessagesWorker;
import com.wj.android.messageviewer.message.MessageThread;
import com.wj.android.messageviewer.util.Pair;
import com.wj.android.messageviewer.util.RecentCollection;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Triggered by the recent files menu item to load the message and optionally
 * the contacts database file recently used.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class OpenRecentFileAction extends AbstractAction
{
   private static final long serialVersionUID = 8988608054135001078L;

   private static final String FILELOCATIONSEPARATOR = ":";

   private transient final TitaniumBackupMessageViewer m_Caller;
   private final JFrame m_frame;
   private transient final RecentCollection<Pair<String, String>> m_RecentCollection;
  private final JList<MessageThread> m_ThreadListBox;
   private final JTextField m_NumberSMSField;
   private final MessageViewer m_MessageViewer;
   private final JScrollPane m_ScrollPaneThread;

   /**
    * Constructs a new {@code OpenRecentFileAction}.
    *
    * @param caller the calling application
    * @param frame application main window
    * @param files2Open contains the absolute path to the message and contacts
    *                   database files to be loaded
    * @param recentCollection recent file list
    * @param threadListBox list of currently loaded threads
    * @param numberSMSField displays the number of loaded messages
    * @param messageViewer the pane that displays thread message
    * @param scrollPaneThread scroll pane for thread list
    */
   public OpenRecentFileAction(final TitaniumBackupMessageViewer caller, final JFrame frame, final Pair<String, String> files2Open, final RecentCollection<Pair<String, String>> recentCollection,
                               final JList<MessageThread> threadListBox, final JTextField numberSMSField, final MessageViewer messageViewer, final JScrollPane scrollPaneThread)
   {
      putValue(Action.NAME, makeName(files2Open));

      final String strCmd = makeCommand(files2Open);
      putValue(Action.ACTION_COMMAND_KEY, strCmd.trim().isEmpty() ? null : strCmd);

      m_Caller = caller;
      m_frame = frame;
      m_RecentCollection = recentCollection;
      m_ThreadListBox = threadListBox;
      m_NumberSMSField = numberSMSField;
      m_MessageViewer = messageViewer;
      m_ScrollPaneThread = scrollPaneThread;
   }

   /**
    * {@inheritDoc}
    *
    * @param e the generated event
    */
   @Override
   public void actionPerformed(final ActionEvent e)
   {
      final String[] strFiles = e.getActionCommand().split("\\" + FILELOCATIONSEPARATOR, 2);
      final String strFirstFile;
      final String strSecondFile;

      if (1 <= strFiles.length)
      {
         if (!strFiles[0].isEmpty())
            strFirstFile = strFiles[0];
         else
             strFirstFile = null;
      }
      else
         strFirstFile = null;

      if (2 <= strFiles.length)
        strSecondFile = strFiles[1];
      else
         strSecondFile = null;

      if (null == strFirstFile)
      {
         m_RecentCollection.remove(new Pair<>((String)null, strSecondFile));
         m_Caller.syncRecentFiles();
      }
      else
         new LoadMessagesWorker(m_Caller, m_frame, new Pair<>(strFirstFile, strSecondFile), m_RecentCollection, m_ThreadListBox, m_NumberSMSField, m_MessageViewer, m_ScrollPaneThread).execute();
   }

   private static String makeName(final Pair<String, String> files2Open)
   {
      final StringBuffer strFirtShortName = shortenFilePath(files2Open.getFirst());
      final StringBuffer strSecondShortName = shortenFilePath(files2Open.getSecond());
      final String strName;
      if (0 == strSecondShortName.length())
         strName = strFirtShortName.toString();
      else
         strName = strFirtShortName.append(" && ").append(strSecondShortName).toString();

      return(strName);
   }

   private static String makeCommand(final Pair<String, String> files2Open)
   {
      final String strCmd = (files2Open.getFirst() == null ? "" : files2Open.getFirst()) + (files2Open.getSecond() == null ? "" :  FILELOCATIONSEPARATOR) + (files2Open.getSecond() == null ? "" :  files2Open.getSecond());
      return(strCmd);
   }

   private static StringBuffer shortenFilePath(final String strPath)
   {
      final int iFirstPartLength = 10;
      final int iSecondPartName = 42;

      final StringBuffer strStartName = new StringBuffer();
      final StringBuffer strEndName = new StringBuffer();

      if (null != strPath)
      {
         if (strPath.length() > iFirstPartLength)
            strStartName.append(strPath.substring(0, iFirstPartLength));
         else
            strStartName.append(strPath);

         if (strPath.length() > iSecondPartName)
         {
            int iBeginIndex = strPath.length() - iSecondPartName;
            strEndName.append(strPath.substring(iBeginIndex));
         }
         else
            strEndName.append(strPath);
      }

      final StringBuffer strName = new StringBuffer();

      if (0 != strStartName.length() && 0 != strEndName.length())
         strName.append(strStartName.append(" ... ").append(strEndName));
      else if (0 != strStartName.length() && 0 == strEndName.length())
         strName.append(strStartName);
      else if (0 == strStartName.length() && 0 != strEndName.length())
         strName.append(strEndName);

      return(strName);
   }
}
