/*
 * $Id$
 *
 * File:   BubbleText.java
 * Author: Werner Jaeger
 *
 * Created on Mar 6, 2015, 9:16:30 AM
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

import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.JTextArea;

/**
 * Used in {@link MessagePanel} to display message texts.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
class BubbleText extends JTextArea
{
   private static final long serialVersionUID = -4098589709524169577L;

   private int m_iMinimalWidth;

   /**
    * Constructs a new {@code BubbleText}
    */
   BubbleText()
   {
      this(null);
   }

   /**
    * Constructs a new {@code BubbleText} with the specified text displayed.
    * A default model is created and rows/columns are set to 0.
    *
    * @param strText  the text to be displayed, or {@code null}
    */
   BubbleText(final String strText)
   {
      super(strText);
      m_iMinimalWidth = getMinimalWidth();
   }

   /**
    * Sets the text of this {@code BubbleText} to the specified text.
    *
    * <p>
    *    If the text is null or empty, has the effect of simply deleting the
    *    old text.
    * </p>
    *
    * @param strText the new text to be set
    */
   @Override
   public void setText(final String strText)
   {
      super.setText(strText);
      m_iMinimalWidth = getMinimalWidth();
   }

   /**
    * Returns the preferred size of the {@code BubleText}.
    *
    * <p>
    *    This is the minimum of the size needed to display the text and the
    *    size requested for the viewport.
    * </p>
    *
    * @return the size
    */
   @Override
   public Dimension getPreferredSize()
   {
      final Dimension prefD = super.getPreferredSize();

      prefD.height += 15;

      if (prefD.width > m_iMinimalWidth)
         prefD.width = m_iMinimalWidth;

      return(prefD);
   }

   private int getMinimalWidth()
   {
      int iRet = 0;

      final String strText = getText();

      if (null != strText && !strText.trim().isEmpty())
      {
         final String[] strLines = strText.split("\\n|\\n\\r");

         for (final String strLine : strLines)
         {
            final FontMetrics metrics = getFontMetrics(getFont());
            final int iLineWidth = metrics.stringWidth(strLine) + 20;
            iRet = Math.max(iRet, iLineWidth);
         }
      }
      else
         iRet = 20;

      return(iRet);
   }
}
