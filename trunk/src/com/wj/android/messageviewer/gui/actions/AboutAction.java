/*
 * $Id$
 *
 * File:   AboutAction.java
 * Author: Werner Jaeger
 *
 * Created on Mar 8, 2015, 6:40:29 AM
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
import com.wj.android.messageviewer.resources.Resources;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Display the about dialog.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class AboutAction extends AbstractAction
{
   private static class HyperlinkListenerImpl implements HyperlinkListener
   {
      HyperlinkListenerImpl()
      {
      }

      @Override
      public void hyperlinkUpdate(final HyperlinkEvent evt)
      {
         if (HyperlinkEvent.EventType.ACTIVATED == evt.getEventType())
         {
            if(Desktop.isDesktopSupported())
            {
               try
               {
                  Desktop.getDesktop().browse(evt.getURL().toURI());
               }
               catch (final IOException | URISyntaxException ex)
               {
                  LOGGER.log(Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      }
   }

   private static final long serialVersionUID = -3119845028768148725L;
   private static final Logger LOGGER = Logger.getLogger(BackupMessageViewerFrame.class.getName());

   private final JFrame m_Frame;

   /**
    * Constructs a new {@code AboutAction}
    *
    * @param frame application main window
    */
   public AboutAction(final JFrame frame)
   {
      putValue(Action.NAME, "Abut " + Resources.getApplicationName());

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
      final StringBuffer strText = new StringBuffer("<html><body><center><h4>");
      strText.append(Resources.getApplicationName());
      strText.append("</h4></center>");
      strText.append("<center><h5>");
      strText.append(Resources.getApplicationVersion());
      strText.append(" (");
      strText.append(Resources.getReleaseDate());
      strText.append(")</h5></center>");
      strText.append("<p><center>View messages from your Android device in a conversation like style.</center></p>");
      strText.append("<p><center>Copyright &copy; ");
      strText.append(Resources.getReleaseDate().substring(0, 4));
      strText.append(" Werner Jaeger</center></p>");
      strText.append("<p><center>For more information please visit</center>");
      strText.append("<center><a href=\"https://sourceforge.net/projects/titaniumbackupmessageviewer/\">sourceforge.net/projects/titaniumbackupmessageviewer</a></center></p>");
      strText.append("</body></html>");

      final JEditorPane textPane = new JEditorPane();
      textPane.setContentType("text/html");
      textPane.setEditable(false);
      textPane.setText(strText.toString());
      textPane.setBackground((Color)UIManager.get("OptionPane.background"));
      textPane.addHyperlinkListener(new HyperlinkListenerImpl());

      JOptionPane.showMessageDialog(m_Frame, textPane, "About", JOptionPane.INFORMATION_MESSAGE, Resources.getIcon(Resources.APPICON32));
   }
}
