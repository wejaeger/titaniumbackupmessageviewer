/*
 * $Id$
 *
 * File:   QuitAction.java
 * Author: Werner Jaeger
 *
 * Created on Mar 8, 2015, 7:34:44 AM
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


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * Shutdown the Application.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class QuitAction extends AbstractAction
{
   private static final long serialVersionUID = -8686644247857317227L;

   private final JFrame m_Frame;

   /**
    * Constructs a new {@code QuitAction}.
    *
    * @param frame application main window
    */
   public QuitAction(final JFrame frame)
   {
      putValue(Action.NAME, "Quit");
      putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
      putValue(Action.SHORT_DESCRIPTION, "Quit application");

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
      // this will make sure WindowListener.windowClosing() et al. will be called.
      final WindowEvent wev = new WindowEvent(m_Frame, WindowEvent.WINDOW_CLOSING);
      Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
   }
}
