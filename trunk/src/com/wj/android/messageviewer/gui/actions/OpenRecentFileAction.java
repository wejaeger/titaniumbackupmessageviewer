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

import com.wj.android.messageviewer.gui.BackupMessageViewerFrame;
import com.wj.android.messageviewer.gui.workers.LoadMessagesWorker;
import com.wj.android.messageviewer.util.Pair;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Triggered by the recent files menu item to load the message and optionally
 * the contacts database file recently used.
 *
 * @author Werner Jaeger
 */
public class OpenRecentFileAction extends AbstractAction
{
   private static final long serialVersionUID = 8988608054135001078L;

   private static final String FILELOCATIONSEPARATOR = ":";

   private final transient BackupMessageViewerFrame m_Frame;

   /**
    * Constructs a new {@code OpenRecentFileAction}.
    *
    * @param frame application main window frame
    * @param files2Open contains the absolute path to the message and contacts
    *                   database files to be loaded
    */
   public OpenRecentFileAction(final BackupMessageViewerFrame frame, final Pair<String, String> files2Open)
   {
      putValue(Action.NAME, makeName(files2Open));

      final String strCmd = makeCommand(files2Open);
      putValue(Action.ACTION_COMMAND_KEY, strCmd.trim().isEmpty() ? null : strCmd);

      m_Frame = frame;
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

      if (null != strFirstFile)
         new LoadMessagesWorker(m_Frame, new Pair<>(strFirstFile, strSecondFile), null).execute();
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
            final int iBeginIndex = strPath.length() - iSecondPartName;
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
