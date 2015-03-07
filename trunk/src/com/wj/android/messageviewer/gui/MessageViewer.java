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
class MessageViewer extends JPanel
{
   private static final long serialVersionUID = 6976417460980887099L;

   private static final int VGAP = 20;
   private static final int LEFT_MARGIN = 10;
   private static final int RIGHT_MARGIN = 10;
   private static final Dimension PREFERREDSIZE = new Dimension(0, 0);

   /**
    * Storage for all the ArrorBubbles drawn on the panel
    */
   final private List<Bubble> m_Bubbles;

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

      m_Bubbles = new ConcurrentList<>(new ArrayList<Bubble>());

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
   public void addArrowBubble(final Bubble bubble)
   {
      if (!m_Bubbles.contains(bubble))
         m_Bubbles.add(bubble);
   }

   /**
    * Removes the passed-in bubble from the panel.
    *
    * @param bubble the bubble to remove
    */
   public void removeArrowBubble(final Bubble bubble)
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
         // append message info date/time sent/received
         final SMSMessage messageInfo = new SMSMessage(message.getServiceCenter(), message.getMessageAddress(), message.getMessageDate(), message.getFormattedMessageDate(), message.getMessageBox());

         final Bubble b;
         if (IMessage.MessageBox.SENT == message.getMessageBox())
         {
            setLocation(Bubble.Type.RIGHTARROW.newBubble(message, this));
            b = Bubble.Type.RIGHTINFO.newBubble(messageInfo, this);
         }
         else if (IMessage.MessageBox.DRAFT == message.getMessageBox())
         {
            setLocation(Bubble.Type.DRAFT.newBubble(message, this));
            b = Bubble.Type.RIGHTINFO.newBubble(messageInfo, this);
         }
         else
         {
            setLocation(Bubble.Type.LEFTARROW.newBubble(message, this));
            b = Bubble.Type.LEFTINFO.newBubble(messageInfo, this);
         }

         b.setBorderWidth(0);
         setLocation(b);
         repaint();
      }
   }

   private void setLocation(final Bubble b)
   {
      final int ix;

      int iVGap = 0;

      setWidth(b);

      switch (b.getType())
      {
         case LEFTINFO:
            iVGap = VGAP;
            ix = LEFT_MARGIN;
            break;

         case LEFTARROW:
            ix = LEFT_MARGIN;
            break;

         case RIGHTINFO:
            iVGap = VGAP;
            ix = getWidth() - b.getWidth() - RIGHT_MARGIN;
            break;

         case RIGHTARROW:
         case DRAFT:
            ix = getWidth() - b.getWidth() - RIGHT_MARGIN;
            break;

         default:
            ix = LEFT_MARGIN;
      }

      b.setLocation(ix, m_iHeight);
      m_iHeight += b.getHeight() + iVGap;
   }

   private int calcBubbleWidth()
   {
      return((getWidth() - LEFT_MARGIN - RIGHT_MARGIN) * 5/6 - LEFT_MARGIN - RIGHT_MARGIN);
   }

   private void setWidth(final Bubble b)
   {
      m_iWidth = calcBubbleWidth();

      b.setWidth(m_iWidth);
   }

   @Override
   public Dimension getPreferredSize()
   {
      if (m_fScroll)
         PREFERREDSIZE.setSize(Math.max(m_iWidth, 0), m_iHeight);
      else
         PREFERREDSIZE.setSize(Math.max(m_iWidth, 0), 0);

      return(PREFERREDSIZE);
   }

   @Override
   protected void paintComponent(final Graphics g)
   {
      super.paintComponent(g);

      // cast to Graphics2D (in everything java 1.2 and later, always get G2D)
      final Graphics2D g2 = (Graphics2D)g;

      m_iHeight = 0;
      for (final Bubble bubble : m_Bubbles)
      {
         setLocation(bubble);
         bubble.draw(g2);
       }
   }
}