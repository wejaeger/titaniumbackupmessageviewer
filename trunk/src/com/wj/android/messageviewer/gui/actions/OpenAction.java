/*
 * $Id$
 *
 * File:   OpenAction.java
 * Author: Werner Jaeger
 *
 * Created on Mar 7, 2015, 6:27:18 PM
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
import com.wj.android.messageviewer.io.IMessageReader;
import com.wj.android.messageviewer.util.Pair;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Triggered by the open files menu item to load the message and optionally
 * the contacts database file recently used.
 *
 * @author Werner Jaeger
 */
public class OpenAction extends AbstractAction
{
   private static final long serialVersionUID = -1759188821667390901L;

   private final transient BackupMessageViewerFrame m_Frame;

   private final JFileChooser m_MessageFileChooser;
   private final JFileChooser m_ContactsDatabaseChooser;

   /**
    * Constructs a new {@code OpenAction}.
    *
    * @param frame application main window frame
    */
   public OpenAction(final BackupMessageViewerFrame frame)
   {
      putValue(NAME, "Open ...");
      putValue(MNEMONIC_KEY, KeyEvent.VK_O);
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

      m_Frame = frame;

      UIManager.put("FileChooser.readOnly", Boolean.TRUE);
      m_MessageFileChooser = new JFileChooser();
      m_MessageFileChooser.setDialogTitle("Open message file");
      m_MessageFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
      m_MessageFileChooser.addChoosableFileFilter(new SMSBackupAndResoreMessageFileNameFilter());
      m_MessageFileChooser.addChoosableFileFilter(new SkypeFileNameFilter());
      m_MessageFileChooser.setFileFilter(new TitaniumBackupMessageFileNameFilter());
      m_MessageFileChooser.setApproveButtonToolTipText("You can open the commpressd (.gz) or plain (.xml) message file");


      UIManager.put("FileChooser.readOnly", Boolean.TRUE);
      m_ContactsDatabaseChooser = new JFileChooser();
      m_ContactsDatabaseChooser.setDialogTitle("Open contacts database");
      m_ContactsDatabaseChooser.setDialogType(JFileChooser.OPEN_DIALOG);
      m_ContactsDatabaseChooser.setFileFilter(new TitaniumBackupContactsFileNameFilter());
      m_ContactsDatabaseChooser.setApproveButtonToolTipText("You can open the archived (.tar.gz) or the plain (.db) contacts database");
   }

   /**
    * {@inheritDoc}
    *
    * @param e the generated event
    */
   @Override
   public void actionPerformed(final ActionEvent e)
   {
      final String strMessageFilePath = chooseMessageFilePath();

      if (null != strMessageFilePath)
      {
         final IMessageReader.MessageFileType messageReaderType = IMessageReader.MessageFileType.getMessageFileType(strMessageFilePath);

         if (null != messageReaderType)
         {
            final String strContactDatabaseFilePath;
            if (IMessageReader.MessageFileType.TITANIUM == messageReaderType)
               strContactDatabaseFilePath = chooseContactsDBFilePath(strMessageFilePath);
            else
               strContactDatabaseFilePath = null;

            if (IMessageReader.MessageFileType.SKYPE == messageReaderType)
               new LoadMessagesWorker(m_Frame, new Pair<>((String)null, strMessageFilePath), messageReaderType).execute();
            else
               new LoadMessagesWorker(m_Frame, new Pair<>(strMessageFilePath, strContactDatabaseFilePath), messageReaderType).execute();
         }
         else
            JOptionPane.showMessageDialog(m_Frame, "Specified file is a unknown backup message file", "Error", JOptionPane.ERROR_MESSAGE);
      }
   }

   private String chooseMessageFilePath()
   {
      final String strMessageFilePath;

      m_MessageFileChooser.setSelectedFile(new File(""));
      final int iRet = m_MessageFileChooser.showOpenDialog(m_Frame);

      if (JFileChooser.APPROVE_OPTION == iRet)
         strMessageFilePath = m_MessageFileChooser.getSelectedFile().getAbsolutePath();
      else
         strMessageFilePath = null;

      return(strMessageFilePath);
   }

   private String chooseContactsDBFilePath(final String strMessageFilePath)
   {
      m_ContactsDatabaseChooser.setCurrentDirectory(m_MessageFileChooser.getCurrentDirectory());
      m_ContactsDatabaseChooser.setSelectedFile(new File(""));

      final int iRet = m_ContactsDatabaseChooser.showOpenDialog(m_Frame);

      final String strContactDatabaseFilePath;

      if (JFileChooser.APPROVE_OPTION == iRet)
         strContactDatabaseFilePath = m_ContactsDatabaseChooser.getSelectedFile().getAbsolutePath();
      else
         strContactDatabaseFilePath = null;

      return(strContactDatabaseFilePath);
   }
}
