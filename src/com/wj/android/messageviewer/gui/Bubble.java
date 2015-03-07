/*
 * $Id$
 *
 * File:   Bubble.java
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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;

/**
 * Superclass for all bubbles that will display a message, like conversation
 * bubbles in cartoon.
 *
 * @author Werner Jaeger
 */
abstract class Bubble implements Serializable
{
   private static final long serialVersionUID = -3613617398312453469L;

   /**
    * What kind of arrow bubble.
    */
   public enum Type
   {
      LEFTARROW, RIGHTARROW, DRAFT, LEFTINFO, RIGHTINFO;

      /**
       * Factory method to construct the appropriate arrow bubble.
       *
       * @param message the message to be displayed within this bubble.
       *                Must not be {@code null}
       * @param viewer the parent component. Must not be {@code null}.
       *
       * @return a newly created instance of {@link Bubble}.
       *         or {@code null} if no appropriate implementation exists.
       */
      public final Bubble newBubble(final IMessage message, final MessageViewer viewer)
      {
         final Bubble bubble;

         switch (this)
         {
            case LEFTARROW:
               bubble = new LeftArrowBubble(this, message, viewer);
               break;

            case LEFTINFO:
            case RIGHTINFO:
               bubble = new InfoBubble(this, message, viewer);
               break;

            case RIGHTARROW:
               bubble = new RightArrowBubble(this, message, viewer);
               break;

            case DRAFT:
               bubble = new DraftBubble(this, message, viewer);
               break;

            default:
               bubble = null;
               break;
         }
         return(bubble);
      }
   }

   private static final int DEFAULT_WIDTH = 200;
   private static final int DEFAULT_BORDER_WIDTH = 10;

   protected MessageViewer m_Viewer;

   final private Type m_Type;
   final private MessagePanel m_MessagePanel;
   final private RenderingHints m_RenderingHints;
   final private Polygon m_Arrow;
   final private BasicStroke m_Stroke;
   final private RoundRectangle2D.Double m_Rect;

   private int m_iBorderWidth;
   private int m_ivGap;
   private Point m_Loc;
   private Color m_FillColor;
   private Color m_FrameColor;
   private Dimension m_Dim;

   protected int m_iRadius = 10;
   protected int m_iArrowSize = 8;
   protected int m_iStrokeThickness = 3;
   protected int m_iPadding = m_iStrokeThickness / 2;

   /**
    * Creates new {@code Bubble}.
    *
    * @param message the message to draw within the bubble.
    * @param viewer the viewer. Must not be {@code null}.
    * @param type the type of the bubble.
    */
   protected Bubble(final Type type, final IMessage message, final MessageViewer viewer)
   {
      m_Type = type;
      m_Viewer = viewer;
      m_RenderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      m_Arrow = new Polygon();
      m_Stroke = new BasicStroke(m_iStrokeThickness);
      m_Rect = new RoundRectangle2D.Double(0, 0, 0, 0, m_iRadius, m_iRadius);

      m_iBorderWidth = DEFAULT_BORDER_WIDTH;
      m_ivGap = m_iBorderWidth;

      // creates the message display
      m_MessagePanel = new MessagePanel(message);
      // defaults to same color as background, so appears transparent
      m_MessagePanel.setBackground(m_Viewer.getBackground());
      setFrameColor(Color.black);
      setFillColor(null);
      m_Dim = new Dimension();
      m_Loc = new Point();
      setSize(0, 0);
      setLocation(new Point(0, 0));
      setWidth(DEFAULT_WIDTH);

      show();
   }

   /**
    * Get the type of this arrow bubble,
    *
    * @return the type. Never {@code null}:
    */
   public Type getType()
   {
      return(m_Type);
   }

   /**
    * Sets the location of the Bubble. Overridden to position the text box
    * within the frame. (Calls to <code>setLocation(int, int)</code> forward to
    * <code>setLocation(java.awt.Point)</code>, so either one will work
    * correctly.)
    *
    * @param p the location to set. May be {@code null};
    */
   public final void setLocation(final Point p)
   {
      if (null != m_MessagePanel)
         m_MessagePanel.setLocation(new Point(p.x + m_iBorderWidth + m_iArrowSize, p.y + m_ivGap));

      m_Loc = p;
   }

   /**
    * Set the location of the Bubble.
    *
    * @param ix x coordinate of the upper-left corner of bounding box
    * @param iy y coordinate of the upper-left corner of bounding box
    */
   public void setLocation (final int ix, final int iy)
   {
      setLocation(new Point(ix, iy));
   }

   /**
    * Set the dimension of the shape.
    *
    * @param iWidth the width.
    * @param iHeight the height.
    */
   private void setSize(final int iWidth, final int iHeight)
   {
      setSize(new Dimension(iWidth, iHeight));
   }

   /**
    * Sets the size of the Bubble. NOTE: setting an explicit size for the
    * bubble may cut off some of the text. Use {@code setWidth(int)} to
    * specify a width for the bubble while maintaining view of all the text.
    *
    * @param d the dimension to set. May be {@code null};
    */
   private void setSize(final Dimension d)
   {
      if (null != m_MessagePanel)
      {
         // center the text vertically if there's room
         final int iNewTWidth = d.width - 2 * m_iBorderWidth - m_iArrowSize;

         // first set the width with any height so we can find
         // out the optimal height for the given width
         m_MessagePanel.setSize(new Dimension(iNewTWidth, 1));

         // find out the optimal dimension
         final Dimension prefD = getPreferredSize();

         // if the passed-in height is greater than the optimal height
         // for the text box...
         int iNewTHeight = d.height - 2 * m_iBorderWidth;
         if (iNewTHeight > prefD.height)
         {
            // set the height to the optimal height (visually the same, but
            // prevents the empty part of the text box from overlapping
            // the frame)
            iNewTHeight = prefD.height;
            // set the vertical borders so the text is centered vertically
            m_ivGap = (d.height - prefD.height) / 2;
         }
         else
         {
            // otherwise, vert gap is the same as horizontal gap
            m_ivGap = m_iBorderWidth;
         }

         // resize the text box
         m_MessagePanel.setSize(new Dimension(iNewTWidth, iNewTHeight));

         // update location to account for possible changes in vertical
         // spacing
         setLocation(getLocation());
      }
      m_Dim = d;
   }

   /**
    * Returns the preferred size.
    *
    * @return the preferred size.
    */
   public Dimension getPreferredSize()
   {
      return(m_MessagePanel.getPreferredSize());
   }

   /**
    * Returns the rectangles dimensions.
    *
    * @return the size.
    */
   public final Dimension getSize()
   {
      final Dimension dim;

      if (null != m_Dim)
         dim = new Dimension(m_Dim.width, m_Dim.height);
      else
         dim = new Dimension(0, 0);

      return(dim);
   }

   /**
    * Sizes the Bubble to the given width, but maintains full view of the
    * contents by adjusting the height if necessary.
    *
    * @param iWidth the width to set.
    */
   public final void setWidth(final int iWidth)
   {
      // set to any height so we can find out the optimal height for
      // the given width
      m_MessagePanel.setSize(new Dimension(iWidth - 2 * m_iBorderWidth - m_iArrowSize, 1));
      // set size to the optimal dimension
      final Dimension d = getPreferredSize();
      setSize(new Dimension(d.width + 2 * m_iBorderWidth - m_iArrowSize, d.height + 2 * m_iBorderWidth));
   }

   /**
    * Returns the rectangles width.
    *
    * @return the width.
    */
   public final int getWidth()
   {
      return(getSize().width);
   }

   /**
    * Returns the rectangles height.
    *
    * @return the height.
    */
   public final int getHeight()
   {
      return(getSize().height);
   }

   /**
    * Graphically shows the arrow bubble.
    */
   public final void show()
   {
      if (null != m_MessagePanel)
         m_Viewer.add(m_MessagePanel);

      m_Viewer.addArrowBubble(this);
      m_Viewer.repaint(getBounds());
   }

   /**
    * Graphically hides the conversation bubble.
    */
   public final void hide()
   {
      if (null != m_MessagePanel)
         m_Viewer.remove(m_MessagePanel);

      m_Viewer.removeArrowBubble(this);
      m_Viewer.repaint(getBounds());
   }

   /**
    * Changes the size of the border of whitespace between the text area and its
    * frame.
    *
    * @param iWidth the width to set. May be {@code null};
    */
   public final void setBorderWidth(final int iWidth)
   {
      if (iWidth >= 0)
      {
         m_iBorderWidth = iWidth;
         setSize(getSize());
         setLocation(getLocation());
      }
   }

   /**
    * Set the background and frame color of the Bubble.
    *
    * @param c the color to set. May be {@code null}.
    */
   public final void setColor(final Color c)
   {
      if (null != m_MessagePanel)
         m_MessagePanel.setBackground(c);

      m_Viewer.repaint(getBounds());
   }

   /**
    * Set the fill color of the Bubble.
    *
    * @param c the color to set. May be {@code null}.
    */
   public final void setFillColor(final Color c)
   {
      if (null != m_MessagePanel)
         m_MessagePanel.setBackground(c);

      m_FillColor = c;
      m_Viewer.repaint(getBounds());
   }

   /**
    * Returns the rectangles fill color.
    *
    * @return the color.
    */
   public final Color getFillColor()
   {
      return(m_FillColor);
   }

   /**
    * Returns the bounds of the arrow bubble.
    *
    * @return the bounds.
    */
   public final Rectangle getBounds()
   {
      final Rectangle b = m_Viewer.getBounds();

      b.grow(1, 1);

      return(b);
   }

   /**
    * Sets the color of the rectangles frame. If c is {@code null}, won't draw
    * the frame.
    *
    * @param c the color to set. May be {@code null}.
    */
   public final void setFrameColor(final Color c)
   {
      m_FrameColor = c;
      m_Viewer.repaint(getBounds());
   }

   /**
    * Returns the color of the rectangles frame.
    *
    * @return the color.
    */
   public final Color getFrameColor()
   {
      return(m_FrameColor);
   }

   /**
    * Returns the rectangles location.
    *
    * @return the location.
    */
   public final Point getLocation()
   {
      final Point loc;

      if (null != m_Loc)
         loc = m_Loc;
      else
         loc = new Point(0, 0);

      return(loc);
   }

   public final void draw(final Graphics2D g2)
   {
      final int iX = getLocation().x + m_iPadding + m_iStrokeThickness + m_iArrowSize;
      final int iY = getLocation().y + m_iPadding;
      final int iWidth = getWidth() - m_iArrowSize - (m_iStrokeThickness * 2);
      final int iHeight = getHeight() - m_iStrokeThickness;
      g2.setPaint(getFillColor());
      g2.fillRect(iX, iY, iWidth, iHeight);
      g2.setRenderingHints(m_RenderingHints);
      g2.setStroke(m_Stroke);
      m_Rect.setRoundRect(iX, iY, iWidth, iHeight, m_iRadius, m_iRadius);
      m_Arrow.reset();
      setArrow(m_Arrow);
      final Area area = new Area(m_Rect);
      area.add(new Area(m_Arrow));
      g2.draw(area);
   }

   protected abstract void setArrow(final Polygon arrow);
}