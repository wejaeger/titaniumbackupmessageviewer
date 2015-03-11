/*
 * $Id$
 *
 * File:   ExportWorker.java
 * Author: Werner Jaeger
 *
 * Created on Mar 3, 2015, 6:42:50 PM
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
package com.wj.android.messageviewer.gui.workers;

import com.wj.android.messageviewer.message.IMessage;
import com.wj.android.messageviewer.message.MessageThread;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

/**
 * Exports selected or all messages to a plan text file.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class ExportWorker extends AbstractDisabelingUIWorker<Boolean, Void>
{
   private static final Logger LOGGER = Logger.getLogger(ExportWorker.class.getName());

   private final static String DEFAULTCHARSET = Charset.defaultCharset().name();

   private final JFrame m_Frame;
   private final boolean m_fAll;
   private final File m_FileToExport;
   private final JList<MessageThread> m_ThreadListBox;

   /**
    * Constructs a new {@code ExportWorker}.
    *
    * @param frame the menus and visible components are disabled/enabled on this
    *        frame. Must not be {@code null}.
    * @param fAll if {@code true} all messages are exported, otherwise only the
    *        messages of the selected thread are exported.
    * @param fileToExport the file to export messages to. Must not be
    *        {@code null}.
    * @param threadListBox list of currently loaded threads
    */
   public ExportWorker(final JFrame frame, final boolean fAll, final File fileToExport, final JList<MessageThread> threadListBox)
   {
      super(frame, true);

      m_Frame = frame;
      m_fAll = fAll;
      m_FileToExport = fileToExport;
      m_ThreadListBox = threadListBox;
   }

   /**
    * Exports all messages or messages of a selected thread to the file.
    * specified in the constructor.
    *
    * <p>
    *    Set cursor to wait, disables menu and all visible frame components
    *    and exports messages.
    * </p>
    *
    * <p>
    *    Note: this method is executed in a background thread.
    * </p>
    *
    * @return the result {@code true} if and only if all messages where
    * exported successfully.
    *
    * @throws Exception never thrown.
    */
   @Override
   protected Boolean doInBackground() throws Exception
   {
      boolean fResult = super.doInBackground();
      if (true == fResult && null != m_FileToExport)
      {
         if (true == m_fAll)
            fResult = exportAllMessages(m_FileToExport);
         else
            fResult = exportThreadMessages(m_FileToExport);
      }

      return(fResult);
   }

   /**
    * Executed on the Event Dispatch Thread after the {@code doInBackground()}
    * method is finished.
    *
    * <p>
    *    Displays an error or success message and sets cursor to
    *    default and enables all frame menus and visible components.
    * </p>
    */
   @Override
   protected void done()
   {
      try
      {
         final boolean fResult = get();

         if (fResult)
         {
            if (m_fAll)
               JOptionPane.showMessageDialog(m_Frame, "All messages exported successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
            else
               JOptionPane.showMessageDialog(m_Frame, "Messages with selected threads exported successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
         }
         else
         {
            if (m_fAll)
               JOptionPane.showMessageDialog(m_Frame, "Failed to export all messages!", "Error", JOptionPane.ERROR_MESSAGE);
            else
               JOptionPane.showMessageDialog(m_Frame, "Failed to export selected messages!", "Error", JOptionPane.ERROR_MESSAGE);
         }
      }
      catch (final ExecutionException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
      }
      catch (final InterruptedException ex)
      {
         Thread.currentThread().interrupt();
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
      }

      super.done();
   }

   /**
    * Export all messages as plain text to the specified file.
    *
    * @param saveFile the file to save the messages to.
    * @return {@code true} if and only if messages are exported successfully.
    */
   private boolean exportAllMessages(final File saveFile)
   {
      boolean fRet = true;

      try (final BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), DEFAULTCHARSET)))
      {
         final ListModel<MessageThread> list = m_ThreadListBox.getModel();
         final int iNoThread = list.getSize();
         for(int i = 0; i < iNoThread; i++)
         {
            final MessageThread thread = list.getElementAt(i);
            printThreadMessages(outputWriter, thread, i == (iNoThread - 1));
         }
      }
      catch (final IOException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
         fRet = false;
      }

      return(fRet);
   }

   /**
    * Export the messages as plain text of all selected threads to the
    * specified file.
    *
    * @param saveFile the file to save the messages to.
    *
    * @return {@code true} if and only if messages are exported successfully.
    */
   private boolean exportThreadMessages(final File saveFile)
   {
      boolean fRet = true;

      try
      {
         try (final BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), DEFAULTCHARSET)))
         {
            final List<MessageThread> threads = m_ThreadListBox.getSelectedValuesList();
            final int iNoThread = threads.size();

            int i = 0;
            for (final MessageThread thread : threads)
               printThreadMessages(outputWriter, thread, i++ == (iNoThread - 1));
         }
      }
      catch (IOException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
         fRet = false;
      }

      return(fRet);
   }

   private void printThreadMessages(final BufferedWriter outputWriter, final MessageThread thread, final boolean fIsLast) throws IOException
   {
      final Collection<IMessage> selectedMessages = thread.getMessages();

      outputWriter.write(thread.toString());
      outputWriter.newLine();
      outputWriter.newLine();

      for (final IMessage selectedMessage : selectedMessages)
      {
         outputWriter.write(selectedMessage.toString());
         outputWriter.newLine();
      }

      if (!fIsLast)
      {
         outputWriter.write("++++++++++++++++++++++++++++++++++++++++++++++++++");
         outputWriter.newLine();
         outputWriter.newLine();
      }
   }
}
