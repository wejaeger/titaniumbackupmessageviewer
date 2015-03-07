/*
 * $Id$
 *
 * File:   InfoBubble.java
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
package com.wj.android.messageviewer.gui.shapes;

import com.wj.android.messageviewer.gui.MessageViewer;
import com.wj.android.messageviewer.message.IMessage;
import java.awt.Polygon;

/**
 * Subclass of Bubble that will display a message info as a
 conversation bubble with no arrow.
 *
 * @author Werner Jaeger
 */
class InfoBubble extends Bubble
{
   private static final long serialVersionUID = 3904447495064329055L;

   /**
    * Creates new {@code InfoBubble}.
    *
    * @param message the message to draw within the bubble.
    * @param viewer the viewer. Must not be {@code null}.
    * @param type the type of the bubble.
    */
   protected InfoBubble(final Type type, final IMessage message, final MessageViewer viewer)
   {
      super(type, message, viewer);

      setFillColor(viewer.getBackground());
   }

   @Override
   protected void setArrow(final Polygon arrow)
   {
   }
}