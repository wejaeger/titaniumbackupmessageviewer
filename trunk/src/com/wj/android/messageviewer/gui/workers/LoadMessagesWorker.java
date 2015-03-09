/*
 * $Id$
 *
 * File:   LoadMessagesWorker.java
 * Author: Werner Jaeger
 *
 * Created on Mar 3, 2015, 12:55:02 AM
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

import com.wj.android.messageviewer.util.Pair;
import com.wj.android.messageviewer.gui.TitaniumBackupMessageViewer;
import com.wj.android.messageviewer.io.IMessageReader;
import com.wj.android.messageviewer.io.TitaniumBackupMessageReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Read and Load message files in the background.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class LoadMessagesWorker extends AbstractDisabelingUIWorker<Integer, Integer>
{
   private static final Logger LOGGER = Logger.getLogger(LoadMessagesWorker.class.getName());

   private final IMessageReader m_Reader;
   private final TitaniumBackupMessageViewer m_Caller;
   private final JFrame m_ReaderFrame;
   private final Pair<String, String> m_FileLocations;

   /**
    * Constructs a new {@code LoadMessagesWorker}.
    *
    * @param caller the calling application
    * @param readerFrame application main window
    * @param fileLocations message and contact database file path
    */
   public LoadMessagesWorker(final TitaniumBackupMessageViewer caller, final JFrame readerFrame, final Pair<String, String> fileLocations)
   {
      super(readerFrame, 0);

      m_Reader = new TitaniumBackupMessageReader();

      m_Caller = caller;
      m_ReaderFrame = readerFrame;
      m_FileLocations = fileLocations;
   }

   /**
    * Reads and loads messages, or throws an exception if unable to do so.
    *
    * <p>
    *    Set cursor to wait, disables frame and load messages
    * </p>
    *
    * <p>
    *    Note: this method is executed in a background thread.
    * </p>
    *
    * @return the result, 0 = no error, 1, 2, 3 an error occurred.
    *
    * @throws Exception never thrown.
    */
   @Override
   protected Integer doInBackground() throws Exception
   {
      int iRet = super.doInBackground();

      if (0 == iRet)
      {
         InputStream is = null;

         try
         {
            final File messageFile = new File(m_FileLocations.getFirst());

            if (m_FileLocations.getFirst().endsWith(".gz"))
               is = new GZIPInputStream(new FileInputStream(messageFile));
            else
               is = new FileInputStream(messageFile);

            final File contactsDBFile;
            final String strContactsDBFileName = m_FileLocations.getSecond();
            if (null != strContactsDBFileName && !strContactsDBFileName.trim().isEmpty())
               contactsDBFile= new File(strContactsDBFileName);
            else
               contactsDBFile = null;

            iRet = m_Reader.loadMessages(is, contactsDBFile);
         }
         catch (FileNotFoundException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            iRet = -1;
         }
         catch (IOException ex)
         {
            iRet = 1;
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
         }
         finally
         {
            try
            {
               if (is != null)
                  is.close();
            }
            catch (IOException ex)
            {
               iRet = -2;
               LOGGER.log(Level.SEVERE, ex.toString(), ex);
            }
         }
      }

      return(iRet);
   }

   /**
    * Executed on the Event Dispatch Thread after the {@code doInBackground()}
    * method is finished.
    *
    * <p>
    *    Displays an error message in case of an error, sets cursor to default
    *    and enables all frame menus and visible components.
    * </p>
    */
   @Override
   protected void done()
   {
      try
      {
         final int iResult = get();

         String strErrorMessage = "Failed to load messages!\n";
         switch (iResult)
         {
            case 0:
               m_Caller.onMessagesLoaded(m_Reader.getThreadArray(), m_Reader.getNumberOfMessages(), m_FileLocations);
               break;

            case -1:
               strErrorMessage = strErrorMessage + "Error Code " + iResult + ": Problem message file not found!\n";
               m_Caller.onMessageFileNotFound(m_FileLocations);
               break;

            case -2:
               strErrorMessage = strErrorMessage + "Error Code " + iResult + ": Problem closing the file!\n";
               break;

            case -3:
               strErrorMessage = strErrorMessage + "Error Code " + iResult + ": Problem contact database file file not found!\n";
               m_Caller.onMessageFileNotFound(m_FileLocations);
               break;

            case 1:
               strErrorMessage = strErrorMessage + "Error Code " + iResult + ": Problem reading file!\n";
               break;

            case 2:
               strErrorMessage = strErrorMessage + "Error Code " + iResult + ": Invalid Message file!\n";
               break;

            case 3:
               strErrorMessage = strErrorMessage + "Error Code " + iResult + ": Problem reading the file!\n";
               break;

            default:
               strErrorMessage = strErrorMessage + "Error Code " + iResult + ": Unknown error!\n";
               break;
         }

         if (iResult != 0)
            JOptionPane.showMessageDialog(m_ReaderFrame, strErrorMessage);
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
