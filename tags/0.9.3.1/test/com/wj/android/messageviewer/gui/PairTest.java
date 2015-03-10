/*
 * $Id$
 *
 * File:   PairTest.java
 * Author: Werner Jaeger
 *
 * Created on Mar 1, 2015, 12:46:15 PM
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

import com.wj.android.messageviewer.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Various test for the {link Pair} class.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class PairTest
{

   /**
    * Constructs a new {@code PairTest}.
    */
   public PairTest()
   {
   }

   /**
    * Test of equals method, of class Pair.
    */
   @Test
   public void testEquals2Null()
   {
      final Pair<String, String> pair1 = new Pair<>(null, null);
      final Pair<String, String> pair2 = new Pair<>(null, null);

      assertTrue(pair1.equals(pair2));
   }

   /**
    * Test of equals method, of class Pair.
    */
   @Test
   public void testEqual12Null()
   {
      final Pair<String, String> pair1 = new Pair<>(null, "second");
      final Pair<String, String> pair2 = new Pair<>(null, "second");

      assertTrue(pair1.equals(pair2));
   }

   /**
    * Test of equals method, of class Pair.
    */
   @Test
   public void testEqual1NoNull()
   {
      final Pair<String, String> pair1 = new Pair<>("first", "second");
      final Pair<String, String> pair2 = new Pair<>("first", "second");

      assertTrue(pair1.equals(pair2));
   }

   /**
    * Test of equals method, of class Pair.
    */
   @Test
   public void testFirstUnEqual()
   {
      final Pair<String, String> pair1 = new Pair<>("first1", "second1");
      final Pair<String, String> pair2 = new Pair<>("first2", "second1");

      assertFalse(pair1.equals(pair2));
   }

   /**
    * Test of equals method, of class Pair.
    */
   @Test
   public void testSecondUnEqual()
   {
      final Pair<String, String> pair1 = new Pair<>("first1", "second1");
      final Pair<String, String> pair2 = new Pair<>("first1", "second2");

      assertFalse(pair1.equals(pair2));
   }
}
