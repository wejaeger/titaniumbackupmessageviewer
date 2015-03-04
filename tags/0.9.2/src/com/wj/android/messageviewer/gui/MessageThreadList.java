/*
 * $Id$
 *
 * File:   MessageThreadList.java
 * Author: Werner Jaeger
 *
 * Created on Feb 28, 2015, 2:13:00 PM
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

import com.wj.android.messageviewer.message.MessageThread;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Customized list used to display
 * {@link com.wj.android.messageviewer.message.MessageThread message treads}.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
class MessageThreadList extends JList<MessageThread>
{
   final private static String RESOURCEPATH = "/com/wj/android/messageviewer/resources/";
   final private static ImageIcon ICON = new ImageIcon(MessageThreadListCellRenderer.class.getResource(RESOURCEPATH + "user16.png"));
   private static final long serialVersionUID = -1648776942721559318L;

   final class MessageThreadComponentAdapter extends ComponentAdapter
   {
      /** @inherited */
      @Override
      public void componentResized(final ComponentEvent e)
      {
         // force cache invalidation by temporarily setting fixed height
         setFixedCellHeight(10);
         setFixedCellHeight(-1);
      }
   };

   /**
    * Wraps the list item text.
    *
    * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
    */
   final class MessageThreadListCellRenderer extends DefaultListCellRenderer
   {
      private static final long serialVersionUID = -2684873318524485434L;
      final private JPanel m_Panel;
      final private JTextArea m_TextArea;

      /**
       * Construct a new {@code MessageThreadListCellRenderer}.
       *
       * @param iInitialTextWidth the initial width of the text in pixel.
       */
      MessageThreadListCellRenderer(final int iInitialTextWidth)
      {
         //create panel
         m_Panel = new JPanel(new BorderLayout(10, 0));

         //icon
         m_Panel.add(new JLabel(ICON), BorderLayout.LINE_START);

         //text
         m_TextArea = new JTextArea();
         m_TextArea.setLineWrap(true);
         m_TextArea.setWrapStyleWord(true);
         m_TextArea.setSize(iInitialTextWidth, 1);
         m_Panel.add(m_TextArea, BorderLayout.CENTER);
      }

      /**
       *
       */
      @Override
      public Component getListCellRendererComponent(final JList<?> list, final Object oValue, final int iIndex, final boolean fIsSelected, final boolean fCellHasFocus)
      {
         final String strText = oValue.toString();

         m_TextArea.setText(strText);
         m_TextArea.setFont(list.getFont());
         m_Panel.setEnabled(list.isEnabled());
         m_Panel.setOpaque(true);

         if (fIsSelected)
         {
            m_TextArea.setBackground(list.getSelectionBackground());
            m_TextArea.setForeground(list.getSelectionForeground());
         }
         else
         {
            m_TextArea.setBackground(list.getBackground());
            m_TextArea.setForeground(list.getForeground());
         }

         return(m_Panel);
      }
   }

   /**
    * Constructs a {@code MessageThreadList}.
    *
    * @param iInitialTextWidth the initial width of the text in pixel.
    */
   MessageThreadList(final int iInitialTextWidth)
   {
      super();

      setCellRenderer(new MessageThreadListCellRenderer(iInitialTextWidth));
   }

   /** @inherited */
   @Override
   public boolean getScrollableTracksViewportWidth()
   {
      return(true);
   }
}
