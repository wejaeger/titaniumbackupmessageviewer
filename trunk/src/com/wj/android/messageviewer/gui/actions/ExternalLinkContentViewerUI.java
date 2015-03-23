/*
 * $Id$
 *
 * File:   ExternalLinkContentViewerUI.java
 * Author: Werner Jaeger
 *
 * Created on Mar 23, 2015, 8:09:27 AM
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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.JHelpContentViewer;
import javax.help.plaf.basic.BasicContentViewerUI;
import javax.swing.JComponent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.ComponentUI;

/**
 * A UI subclass that will open external links (website or mail links) in an
 * external browser.
 *
 * @author Werner Jaeger
 */
public class ExternalLinkContentViewerUI extends BasicContentViewerUI
{
   private static final long serialVersionUID = 8129767974182528187L;
   private static final Logger LOGGER = Logger.getLogger(ExternalLinkContentViewerUI.class.getName());

   /**
    * Constructs a new {@code ExternalLinkContentViewerUI}.
    *
    * @param ctxViewer the embedded help viewer.
    */
   public ExternalLinkContentViewerUI(final JHelpContentViewer ctxViewer)
   {
      super(ctxViewer);
   }

   /**
    * Creates a {@code ExternalLinkContentViewerUI} component UI.
    *
    * @param component the component to create the UI for.
    *
    * @return a newly constructed {@code ExternalLinkContentViewerUI} or
    *         {@code null} if component is not an instance of
    *         {@code JHelpContentViewer}.
    */
   public static ComponentUI createUI(final JComponent component)
   {
      final JHelpContentViewer helpviewer;
      if (component instanceof JHelpContentViewer)
         helpviewer = JHelpContentViewer.class.cast(component);
      else
         helpviewer = null;

      return(helpviewer == null ? null : new ExternalLinkContentViewerUI(helpviewer));
   }

   /**
    * Called when a hypertext link is updated.
    *
    * <p>
    *    If protocol is {@code mailto}, {@code http(s)} or {@code ftp} use
    *    desktop browser.
    * </p>
    *
    * @param evt the event responsible for the update
    */
   @Override
   public void hyperlinkUpdate(final HyperlinkEvent evt)
   {
      if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED && Desktop.isDesktopSupported())
      {
         try
         {
            final URL url = evt.getURL();

            if (url.getProtocol().equalsIgnoreCase("mailto") || url.getProtocol().equalsIgnoreCase("http") || url.getProtocol().equalsIgnoreCase("https") || url.getProtocol().equalsIgnoreCase("ftp"))
            {
               Desktop.getDesktop().browse(url.toURI());
               return;
            }
         }
         catch (final URISyntaxException | IOException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
         }
      }
      super.hyperlinkUpdate(evt);
   }
}
