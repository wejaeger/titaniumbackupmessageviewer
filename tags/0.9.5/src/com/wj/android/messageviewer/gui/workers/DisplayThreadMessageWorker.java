/*
 * $Id$
 *
 * File:   DisplayThreadMessageWorker.java
 * Author: Werner Jaeger
 *
 * Created on Mar 3, 2015, 9:24:56 AM
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

import com.wj.android.messageviewer.gui.MessageViewer;
import com.wj.android.messageviewer.message.IMessage;
import com.wj.android.messageviewer.message.MessageThread;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * Adds messages of a selected thread to the {@link MessageViewer}.
 *
 * @author Werner Jaeger
 */
public class DisplayThreadMessageWorker extends AbstractDisabelingUIWorker<Void, Void>
{
   private static final Logger LOGGER = Logger.getLogger(ExportWorker.class.getName());

   private final MessageThread m_SelectedMessageThread;
   private final MessageViewer m_MessageViewer;

   /**
    * Constructs a new {@code DisplayThreadMessageWorker}.
    *
    * @param frame the view main. Must not be {@code null}.
    * @param selectedThread the selected thread. Must not be {@code null}.
    * @param messageViewer the pane that displays thread message.
    */
   public DisplayThreadMessageWorker(final JFrame frame, final MessageThread selectedThread, final MessageViewer messageViewer)
   {
      super(frame, null);

      m_SelectedMessageThread = selectedThread;
      m_MessageViewer = messageViewer;
   }

   /**
    * Adds messages of a selected thread to the {@link MessageViewer}.
    *
    * <p>
    *    Set cursor to wait, disables menu and all visible frame components
    *    and add messages.
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
   protected Void doInBackground() throws Exception
   {
      final Void result = super.doInBackground();

      m_MessageViewer.setVisible(false);

      if (m_SelectedMessageThread != null)
      {
         final Collection<IMessage> selectedMessages = m_SelectedMessageThread.getMessages();
         m_MessageViewer.clear();

         for (IMessage selectedMessage : selectedMessages)
            m_MessageViewer.appendMessage(selectedMessage);
      }

      return(result);
   }

   /**
    * Executed on the Event Dispatch Thread after the {@code doInBackground()}
    * method is finished.
    *
    * <p>
    *    Sets cursor to default
    *    and enables all frame menus and visible components
    * </p>
    */
   @Override
   protected void done()
   {
      try
      {
         get();
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

      m_MessageViewer.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
      m_MessageViewer.setVisible(true);

      super.done();
   }
}
