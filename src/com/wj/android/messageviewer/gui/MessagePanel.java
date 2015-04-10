/*
 * $Id$
 *
 * File:   MessagePanel.java
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

import com.wj.android.messageviewer.message.IMMSMessagePart;
import com.wj.android.messageviewer.message.IMessage;
import com.wj.android.messageviewer.message.ImagePart;
import com.wj.android.messageviewer.message.MMSMessage;
import com.wj.android.messageviewer.message.TextPart;
import com.wj.android.messageviewer.message.VCardPart;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Modified JPanel for easy drawing of
 * {@link com.wj.android.messageviewer.message.IMessage messages}.
 *
 * @author Werner Jaeger
 */
public class MessagePanel extends JPanel
{
   private static final int VGAP = 20;
   private static final long serialVersionUID = 5182781147775125388L;

   private final IMessage m_Message;

   /**
    * Creates new {@code MessagePanel}.
    *
    * @param message the message to display. Never {@code null}.
    */
   public MessagePanel(final IMessage message)
   {
      super(null, true);

      m_Message = message;

      initComponents();
   }

   /**
    * Sets the background color of this component.
    *
    * @param bg the desired background Color
    */
   @Override
   public void setBackground(final Color bg)
   {
      super.setBackground(bg);

      final int iCount = getComponentCount();
      for (int i = 0; i < iCount; i++)
         getComponent(i).setBackground(bg);
   }

   /**
    * Resizes this component so that it has width d.width and height d.height.
    *
    * <p>
    *    This method changes layout-related information, and therefore,
    *    invalidates the component hierarchy.
    * </p>
    *
    * @param d the dimension specifying the new size of this component
    */
   @Override
   public void setSize(final Dimension d)
   {
      setSize(d.width, d.height);
   }

   /**
    * Resizes this component so that it has width {@code iWidth} and height
    * {@code iHeight}.
    *
    * <p>
    *    This method changes layout-related information, and therefore,
    *    invalidates the component hierarchy.
    * </p>
    *
    * @param iWidth the new width of this component in pixels
    * @param iHeight the new height of this component in pixels
    */
   @Override
   public void setSize(final int iWidth, final int iHeight)
   {
      final int iCount = getComponentCount();
      for (int i = 0; i < iCount; i++)
      {
         final Component comp = getComponent(i);
         final Dimension preferredSize = comp.getPreferredSize();
         comp.setSize(iWidth, preferredSize.height);
      }

      super.setSize(iWidth, iHeight);
   }

   /**
    * Returns the preferred size.
    *
    * @return the preferred size.
    */
   @Override
   public Dimension getPreferredSize()
   {
      int iWidth = 0;

      final Point loc = new Point(0, 0);
      final int iCount = getComponentCount();
      for (int i = 0; i < iCount; i++)
      {
         final Component comp = getComponent(i);
         final Dimension preferredSize = comp.getPreferredSize();
         iWidth = Math.max(iWidth, preferredSize.width);
         comp.setLocation(loc);
         loc.y += preferredSize.height + VGAP;
      }
      loc.y -= VGAP;
      return(new Dimension(iWidth, loc.y));
   }

   private static Image resize(final Image image, final int iWdith, final int iHeight)
   {
      final BufferedImage bi = new BufferedImage(iWdith, iHeight, BufferedImage.TRANSLUCENT);

      final Graphics2D g2d = bi.createGraphics();
      g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
      g2d.drawImage(image, 0, 0, iWdith, iHeight, null);
      g2d.dispose();

      return(bi);
   }

   /**
    * Initialize all components.
    */
   private void initComponents()
   {
      if (m_Message instanceof MMSMessage)
      {
         final MMSMessage mmsMessage = (MMSMessage)m_Message;

         final Point loc = new Point(0, 0);

         final String strSubject = mmsMessage.getSubject().trim().isEmpty() ? "(no subject)" : mmsMessage.getSubject();
         final JLabel subject =  new JLabel(strSubject, JLabel.CENTER);
         subject.setVerticalAlignment(JLabel.TOP);
         final Font defFont = subject.getFont();
         subject.setFont(new Font(defFont.getFamily(), defFont.getStyle(), 24));
         subject.setLocation(loc);
         add(subject);
         loc.y += subject.getPreferredSize().height + VGAP;

         for (IMMSMessagePart part : mmsMessage.getMessageParts())
         {
            if (part instanceof TextPart)
            {
               final TextPart textPart = (TextPart)part;
               final BubbleText text = new BubbleText(textPart.getText());
               text.setEditable(false);
               text.setLocation(loc);
               add(text);
               loc.y += text.getPreferredSize().height + VGAP;
            }
            else if (part instanceof VCardPart)
            {
               final VCardPart textPart = (VCardPart)part;
               final BubbleText text = new BubbleText(textPart.getText());
               text.setEditable(false);
               text.setLocation(loc);
               add(text);
               loc.y += text.getPreferredSize().height + VGAP;
            }
            else if (part instanceof ImagePart)
            {
               final ImagePart imagePart = (ImagePart)part;
               final Image image = imagePart.getImage();

               if (null != image)
               {
                  final ImageIcon icon;
                  final int iImageWidth = image.getWidth(null);
                  final int iImageHeight = image.getHeight(null);
                  if (iImageWidth > 300 || iImageHeight > 400)
                  {
                     final float fltMul;

                     if (iImageHeight > 400)
                        fltMul = 400F / iImageHeight;
                     else
                        fltMul =  300F / iImageWidth;

                     icon = new ImageIcon(resize(image, Math.round(iImageWidth * fltMul), Math.round(iImageHeight * fltMul)));
                  }
                  else
                     icon = new ImageIcon(image);

                  final JLabel label = new JLabel(icon, JLabel.CENTER);
                  label.setVerticalAlignment(JLabel.TOP);
                  label.setLocation(loc.x, loc.y);
                  add(label);
                  loc.y += label.getPreferredSize().height + VGAP;
               }
            }
         }
      }
      else
      {
         final BubbleText text = new BubbleText(m_Message.getMessageText());
         text.setEditable(false);
         add(text);
      }
   }
}
