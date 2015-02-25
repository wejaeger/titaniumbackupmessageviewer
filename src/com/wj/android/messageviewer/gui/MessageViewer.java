/*
 * $Id$
 *
 * File:   MessageViewer.java
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
import com.wj.android.messageviewer.message.SMSMessage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Modified JPanel for easy drawing of ArrowBubbles. Keeps track of all bubbles
 * that have been drawn on it and overrides paintComponent(...) to properly
 * redraw bubbles. Also forwards mouse input to the appropriate bubbles mouse
 * listening methods.
 *
 * @author Werner Jaeger
 */
public class MessageViewer extends JPanel
{
   private static final int VGAP = 20;
   private static final int LEFT_MARGIN = 10;
   private static final int RIGHT_MARGIN = 10;
   private static final Dimension PREFERREDSIZE = new Dimension(0, 0);

   /**
    * Storage for all the ArrorBubbles drawn on the panel
    */
   final private List<ArrowBubble> m_Bubbles;

   private int m_iWidth;
   private int m_iHeight;
   private boolean m_fScroll;

   /**
    * Creates new {@code MessageViewer} panel with size 0x0.
    */
   public MessageViewer()
   {
      this(PREFERREDSIZE);
   }

   /**
    * Creates {@code MessageViewer} panel with the passed-in dimensions.
    *
    * @param iWidth the width of Panel
    * @param iHeight the height of the Panel
    */
   public MessageViewer(final int iWidth, final int iHeight)
   {
      this(new Dimension(iWidth, iHeight));
   }

   /**
    * Creates {@code MessageViewer} panel with the passed-in dimension.
    *
    * @param d the dimension of the Panel
    */
   public MessageViewer(final Dimension d)
   {
      //no layout (absolute positioning) and double-buffering to aid drawing
      super(null, true);

      //set desired dimensions
      setMaximumSize(d);
      setMinimumSize(d);

      //default BG white
      setBackground(Color.white);

      m_Bubbles = new ArrayList<>();

      m_iWidth = d.width;
      m_iHeight = d.height;
      m_fScroll = true;
   }

   /**
    * Removes all bubble components from this panel.
    */
   public void clear()
   {
      m_Bubbles.clear();
      m_iWidth = 0;
      m_iHeight = 0;
      removeAll();
   }

   /**
    * Enable or disable automatic scrolling after appending bubbles by
    * disabling the scroll bar.
    *
    * @param fScroll if {@code true} enable scrolling, otherwise disable it.
    */
   public void setScroll(final boolean fScroll)
   {
      m_fScroll = fScroll;
   }

   /**
    * Adds a bubble to the panel.
    *
    * @param bubble the graphic to add
    */
   public void addArrowBubble(final ArrowBubble bubble)
   {
      if (!m_Bubbles.contains(bubble))
         m_Bubbles.add(bubble);
   }

   /**
    * Removes the passed-in bubble from the panel.
    *
    * @param bubble the bubble to remove
    */
   public void removeArrowBubble(final ArrowBubble bubble)
   {
      m_Bubbles.remove(bubble);
   }

   /**
    * Appends the given message to the view.
    *
    * @param message the message to append.
    */
   public void appendMessage(final IMessage message)
   {
      if (null != message.getMessageBox())
      {
         // append message info date/zime sent/received
         final SMSMessage messageInfo = new SMSMessage(message.getMessageAddress(), message.getMessageDate(), message.getMessageDate().toString(), message.getMessageBox(), 0);

         final ArrowBubble b;
         if (message.getMessageBox() == IMessage.MessageBox.SENT)
         {
            setLocation(ArrowBubble.Type.RIGHTARROW.newArrowBubble(message, this));
            b = ArrowBubble.Type.RIGHTNOARROW.newArrowBubble(messageInfo, this);
         }
         else
         {
            setLocation(ArrowBubble.Type.LEFTARROW.newArrowBubble(message, this));
            b = ArrowBubble.Type.LEFTNOARROW.newArrowBubble(messageInfo, this);
         }

         b.setBorderWidth(0);
         setLocation(b);
         repaint();
      }
   }

   private void setLocation(final ArrowBubble b)
   {
      final int ix;

      int iVGap = 0;

      switch (b.getType())
      {
         case LEFTNOARROW:
            iVGap = VGAP;
         case LEFTARROW:
            ix = LEFT_MARGIN;
            break;

         case RIGHTNOARROW:
            iVGap = VGAP;
         case RIGHTARROW:
            ix = getWidth() - calcBubbleWidth() - RIGHT_MARGIN;
            break;

         default:
            ix = LEFT_MARGIN;
      }

      b.setLocation(ix, m_iHeight);
      setWidth(b);
      m_iHeight += b.getHeight() + iVGap;
   }

   private int calcBubbleWidth()
   {
      return((getWidth() - LEFT_MARGIN - RIGHT_MARGIN) * 5/6 - LEFT_MARGIN - RIGHT_MARGIN);
   }

   private void setWidth(final ArrowBubble b)
   {
      m_iWidth = calcBubbleWidth();
      b.setWidth(m_iWidth);
   }

   @Override
   public Dimension getPreferredSize()
   {
      if (m_fScroll)
         PREFERREDSIZE.setSize(m_iWidth, m_iHeight);
      else
         PREFERREDSIZE.setSize(m_iWidth, 0);

      return(PREFERREDSIZE);
   }

   @Override
   protected void paintComponent(final Graphics g)
   {
      super.paintComponent(g);

      // cast to Graphics2D (in everything java 1.2 and later, always get G2D)
      final Graphics2D g2 = (Graphics2D)g;

      m_iHeight = 0;
      for (final ArrowBubble bubble : m_Bubbles)
      {
         setLocation(bubble);
         bubble.draw(g2);
       }
   }
}