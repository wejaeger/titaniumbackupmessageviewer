/*
 * $Id$
 *
 * File:   ExportSelectedMessagesAction.java
 * Author: Werner Jaeger
 *
 * Created on Mar 7, 2015, 7:38:00 PM
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

import com.wj.android.messageviewer.gui.workers.ExportWorker;
import com.wj.android.messageviewer.message.MessageThread;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Triggered by the export menu item to export message.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class ExportSelectedMessagesAction extends AbstractAction
{
   private static final long serialVersionUID = -7450368171750903020L;

   private final JFileChooser m_SaveChooser;
   private final JFrame m_Frame;
   private final JList<MessageThread> m_ThreadListBox;

   /**
    * Constructs a new {@code ExportSelectedMessagesAction}.
    *
    * @param frame application main window
    * @param threadListBox list of currently loaded threads
    */
   public ExportSelectedMessagesAction(final JFrame frame, final JList<MessageThread> threadListBox)
   {
      putValue(Action.NAME, "Export selected messages ...");
      putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));

      m_Frame = frame;
      m_ThreadListBox = threadListBox;

      UIManager.put("FileChooser.readOnly", Boolean.TRUE);
      m_SaveChooser = new JFileChooser();
      m_SaveChooser.setDialogTitle("Choose a file to save as...");
      m_SaveChooser.setDialogType(JFileChooser.SAVE_DIALOG);
  }

   /**
    * {@inheritDoc}
    *
    * @param e the generated event
    */
   @Override
   public void actionPerformed(final ActionEvent e)
   {
      if (m_ThreadListBox.isEnabled())
      {
         if (m_ThreadListBox.getSelectedIndex() != -1)
         {
            m_SaveChooser.setSelectedFile(new File("SelectedMessagesExport.txt"));

            final int iRet = m_SaveChooser.showSaveDialog(m_Frame);
            if (iRet == 0)
               new ExportWorker(m_Frame, false, m_SaveChooser.getSelectedFile(), m_ThreadListBox).execute();
         }
         else
            JOptionPane.showMessageDialog(m_Frame, "Select a thread first!", "Info", JOptionPane.INFORMATION_MESSAGE);
      }
      else
         JOptionPane.showMessageDialog(m_Frame, "Load messages first!", "Info", JOptionPane.INFORMATION_MESSAGE);
   }
}
