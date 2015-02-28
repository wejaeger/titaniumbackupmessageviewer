/*
 * $Id$
 *
 * File:   RightArrowBubble.java
 * Author: Werner Jaeger
 *
 * Created on Feb 25, 2015, 11:10:38 AM
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

import com.wj.android.messageviewer.message.IMessage;
import java.awt.Color;
import java.awt.Polygon;

/**
 * Subclass of ArrowBubble that will display a sent message as a
 * conversation bubble with arrow on the right.
 *
 * @author Werner Jaeger
 */
class RightArrowBubble extends ArrowBubble
{
   /**
    * Creates new {@code RightArrowBubble}.
    *
    * @param message the message to draw within the bubble.
    * @param viewer the viewer. Must not be {@code null}.
    * @param type the type of the bubble.
    */
   protected RightArrowBubble(final Type type, final IMessage message, final MessageViewer viewer)
   {
      super(type, message, viewer);

      setFillColor(new Color(0.5f, 0.8f, 1f));
   }

   @Override
   protected void setArrow(final Polygon arrow)
   {
      arrow.addPoint(getLocation().x + getWidth(), getLocation().y + 8);
      arrow.addPoint(getLocation().x + getWidth() + m_iArrowSize, getLocation().y + 8);
      arrow.addPoint(getLocation().x + getWidth(), getLocation().y + 12);
   }
}