/*
 * $Id$
 *
 * File:   AbstractDisabelingUIWorker.java
 * Author: Werner Jaeger
 *
 * Created on Mar 3, 2015, 5:19:22 PM
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

import java.awt.Component;
import java.awt.Cursor;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingWorker;

/**
 * Base class for all concrete workers.
 *
 * <p>
 *    Provides an implementation of {@code doInBackground()} and {@code done()}
 *   that disables/enables all menus and visible components of the frame.
 * </p>
 *
 * @param <T> the result type returned by this SwingWorker's doInBackground and
 *            get methods
 * @param <V> the type used for carrying out intermediate results by this
 *            SwingWorker's publish and process methods
 *
 * @author Werner Jaeger
 */
abstract class AbstractDisabelingUIWorker<T, V> extends SwingWorker<T, V>
{
   private final JFrame m_Frame;
   private final T m_Result;

   /**
    * Constructs a new {@code AbstractDisabelingUIWorker}.
    *
    * @param frame the menus and visible components are disabled/enabled on this
    *        frame. Must not be {@code null}.
    * @param result the result to return in {@code doInBackground()}.
    */
   AbstractDisabelingUIWorker(final JFrame frame, final T result)
   {
      m_Frame = frame;
      m_Result = result;
   }

   /**
    * Set cursor to wait, disables all menus and all visible content frame
    * components
    *
    * @return the result.
    *
    * @throws Exception never thrown.
    */
   @Override
   protected T doInBackground() throws Exception
   {
      m_Frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      enableMenu(false);
      enableComponents(false);
      return(m_Result);
   }

   /**
    * Executed on the Event Dispatch Thread after the {@code doInBackground}
    * method is finished.
    *
    * <p>
    *    Sets cursor to default and enables all menus and all visible content
    *    frame components.
    * </p>
    */
   @Override
   protected void done()
   {
      enableMenu(true);
      enableComponents(true);
      m_Frame.setCursor(Cursor.getDefaultCursor());
   }

   private void enableMenu(final boolean fEnable)
   {
      final JMenuBar mnBar = m_Frame.getRootPane().getJMenuBar();
      for (int i = 0; i < mnBar.getMenuCount(); i++)
         mnBar.getMenu(i).setEnabled(fEnable);
   }

   private void enableComponents(final boolean fEnable)
   {
      for (final Component component : m_Frame.getContentPane().getComponents())
         enableComponent(component, fEnable);
   }

   private void enableComponent(final Component component, final boolean fEnable)
   {
      if (component.isDisplayable())
      {
         component.setEnabled(fEnable);

         if (component instanceof JComponent)
         {
            final JComponent jComponent = JComponent.class.cast(component);

            for (final Component childComponent : jComponent.getComponents())
               enableComponent(childComponent, fEnable);
         }
      }
   }
}
