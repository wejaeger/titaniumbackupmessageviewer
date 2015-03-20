/*
 * $Id$
 *
 * File:   HelpAction.java
 * Author: Werner Jaeger
 *
 * Created on Mar 8, 2015, 11:30:28 AM
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

import com.wj.android.messageviewer.resources.Resources;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.AbstractAction;
import static javax.swing.Action.ACCELERATOR_KEY;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * Display the help system for the application.
 *
 * <p>
 *    Display one of table of contents, index, or search tab, according
 *    to argument passed to the constructor. This implementation uses
 *    Sun's <a href="http://java.sun.com/products/javahelp">JavaHelp</a> tool.
 * </p>
 * <p>
 *    This action activates the Help key (often {@code F1}) for this
 *    application. When the help key is pressed, the help system's table of
 *    contents is displayed.
 * </p>
 * <p>
 *    This action is unusual in that it corresponds to more than one menu item
 *    (Contents, Index, and Search).
 * </p>
 * <p>
 *    Note: the displayed JavaHelp screen is not centered; it's left as is,
 *    since the JavaHelp GUI is often cut off at the bottom anyway, and
 *    centering would make this problem worse.
 * </p>
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class HelpAction extends AbstractAction
{
   /**
    * Enumeration for the style of presentation of the the Help system.
    */
   public enum View
   {
      /** Display table of content tab */
      CONTENTS("TOC"),
      /** Display index tab */
      INDEX("Index"),
      /** Display search tab */
      SEARCH("Search");

      private View(final String strName)
      {
         m_strName = strName;
      }

      /**
       * Returns a string representation of this view instance.
       *
       * @return the name of this view
       */
      @Override public String toString()
      {
         return(m_strName);
      }

      private final String m_strName;
   }

   private static final long serialVersionUID = 440742451932953906L;

   private static final Logger LOGGER = Logger.getLogger(HelpAction.class.getName());
   private static final String PATH_TO_JAVA_HELP = "JavaHelp/jhelpset.hs";

   private final JFrame m_Frame;
   private final View m_View;

   private transient HelpBroker m_HelpBroker;
   private transient CSH.DisplayHelpFromSource m_DisplayHelp;

   /**
    * Constructs a new {@code HelpAction}
    *
    * @param frame parent window to which the help window is attached
    * @param strText name of the menu item for this help action
    * @param mnemonicKeyEvent mnemonic for {@code strText}
    * @param icon possibly-{@code null} graphic to be displayed alongside the
    *        text, or in a tool bar
    * @param view determines which help window is to be displayed: Contents,
    *        Index, or Search
    */
   public HelpAction(final JFrame frame, final String strText, final int mnemonicKeyEvent, final Icon icon, final View view)
   {
      super(strText, icon);

      putValue(SHORT_DESCRIPTION, Resources.getApplicationTitle() + " Help");
      putValue(LONG_DESCRIPTION, "Displays JavaHelp for" + Resources.getApplicationTitle() + ".");
      putValue(MNEMONIC_KEY, mnemonicKeyEvent);
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(mnemonicKeyEvent, 0));

      m_Frame = frame;
      m_View = view;

      initHelpSystem();
   }

   /**
    * {@inheritDoc}
    *
    * @param e the generated event
    */
   @Override
   public void actionPerformed(final ActionEvent e)
   {
      m_HelpBroker.setCurrentView(m_View.toString());
      m_DisplayHelp.actionPerformed(e);
   }

   /**
    * Initialize the JavaHelp system.
    */
   private void initHelpSystem()
   {
      //optimization to avoid repeated init
      if (m_HelpBroker == null || m_DisplayHelp == null)
      {
         final ClassLoader loader = getClass().getClassLoader();
         final URL helpSetURL = HelpSet.findHelpSet(loader, PATH_TO_JAVA_HELP);

         assert helpSetURL != null : "Cannot find help system.";

         try
         {
            final HelpSet helpSet = new HelpSet(null, helpSetURL);
            helpSet.setTitle(Resources.getApplicationTitle());
            m_HelpBroker = helpSet.createHelpBroker();
            m_HelpBroker.enableHelpKey(m_Frame, "N1000E", helpSet);
            m_DisplayHelp = new CSH.DisplayHelpFromSource(m_HelpBroker);
         }
         catch (final HelpSetException ex)
         {
            LOGGER.log(Level.SEVERE, "Cannot create help system with: {0}", helpSetURL);
         }

         assert m_HelpBroker != null : "HelpBroker is null.";
      }
   }
}
