/*
 * $Id$
 *
 * File:   BubbleTextTest.java
 * Author: Werner Jaeger
 *
 * Created on Mar 6, 2015, 9:46:02 AM
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for class {@link BubbleText}.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class BubbleTextTest
{
   private final BubbleText m_Text;

   /**
    * Constructs a new {@code BubbleTextTest}.
    */
   public BubbleTextTest()
   {
      m_Text = new BubbleText();
      m_Text.setEditable(false);
      m_Text.setLineWrap(true);
      m_Text.setWrapStyleWord(true);
   }

   /**
    * Test of getPreferredSize method, of class BubbleText.
    */
   @Test
   public void testGetPreferredSizeReturnsLessThanSetWidth()
   {
      m_Text.setText("First line\nSeocond Line");

      m_Text.setSize(500, 50);

      final int iPreferredWidth = m_Text.getPreferredSize().width;
      assertEquals(104, iPreferredWidth);
   }

   /**
    * Test of getPreferredSize method, of class BubbleText.
    */
   @Test
   public void testGetPreferredSizeReturnsnSetWidth()
   {
      m_Text.setText("First line\nSeocond Line");

      m_Text.setSize(30, 50);

      final int iPreferredWidth = m_Text.getPreferredSize().width;
      assertEquals(30, iPreferredWidth);
   }
}
