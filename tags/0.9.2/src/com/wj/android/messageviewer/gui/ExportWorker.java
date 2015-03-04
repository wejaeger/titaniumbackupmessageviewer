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
package com.wj.android.messageviewer.gui;

import com.wj.android.messageviewer.io.TitaniumBackupMessageReader;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Exports selected or all messages to a plan text file.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
class ExportWorker extends AbstractDisabelingUIWorker<Boolean, Void>
{
   private static final Logger LOGGER = Logger.getLogger(ExportWorker.class.getName());

   private final JFrame m_Frame;
   private final boolean m_fAll;
   private final File m_FileToExport;
   private final int m_iSelectedIndex;
   private final TitaniumBackupMessageReader m_Reader;

   /**
    * Constructs a new {@code ExportWorker}.
    *
    * @param frame the menus and visible components are disabled/enabled on this
    *        frame. Must not be {@code null}.
    * @param fAll if {@code true} all messages are exported, otherwise only the
    *        messages of the selected thread are exported.
    * @param fileToExport the file to export messages to. Must not be
    *        {@code null}.
    * @param iSelectedIndex if {@code fAll} if {@code false} this is the index
    *        in the thread collection, determining which thread messages to
    *        export.
    * @param reader the handler instance that actually exports the messages.
    *               Must net be {@code null}.
    */
   ExportWorker(final JFrame frame, final boolean fAll, final File fileToExport, final int iSelectedIndex, final TitaniumBackupMessageReader reader)
   {
      super(frame, true);

      m_Frame = frame;
      m_fAll = fAll;
      m_FileToExport = fileToExport;
      m_iSelectedIndex = iSelectedIndex;
      m_Reader = reader;
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
            fResult = m_Reader.exportAllMessages(m_FileToExport);
         else
            fResult = m_Reader.exportThreadMessages(m_FileToExport, m_iSelectedIndex);
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
               JOptionPane.showMessageDialog(m_Frame, "All messages exported successfully!");
            else
               JOptionPane.showMessageDialog(m_Frame, "Messages with selected threads exported successfully!");
         }
         else
         {
            if (m_fAll)
               JOptionPane.showMessageDialog(m_Frame, "Failed to export all messages!");
            else
               JOptionPane.showMessageDialog(m_Frame, "Failed to export selected messages!");
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
}
