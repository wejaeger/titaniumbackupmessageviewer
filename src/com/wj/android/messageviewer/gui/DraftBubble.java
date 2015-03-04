/*
 * $Id$
 *
 * File:   DraftBubble.java
 * Author: Werner Jaeger
 *
 * Created on Mar 3, 2015, 8:54:41 AM
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
 * Subclass of Bubble that will display a draft message as a
 conversation bubble with arrow on the right.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
class DraftBubble extends Bubble
{
   private static final long serialVersionUID = -2767611142482264383L;

   /**
    * Creates new {@code DraftBubble}.
    *
    * @param message the message to draw within the bubble.
    * @param viewer the viewer. Must not be {@code null}.
    * @param type the type of the bubble.
    */
   protected DraftBubble(final Type type, final IMessage message, final MessageViewer viewer)
   {
      super(type, message, viewer);

      setFillColor(Color.PINK);
   }

   @Override
   protected void setArrow(final Polygon arrow)
   {
      arrow.addPoint(getLocation().x + getWidth(), getLocation().y + 8);
      arrow.addPoint(getLocation().x + getWidth() + m_iArrowSize, getLocation().y + 8);
      arrow.addPoint(getLocation().x + getWidth(), getLocation().y + 12);
   }
}
