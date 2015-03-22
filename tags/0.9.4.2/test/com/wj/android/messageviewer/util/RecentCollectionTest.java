/*
 * $Id$
 *
 * File:   RecentCollectionTest.java
 * Author: Werner Jaeger
 *
 * Created on Mar 1, 2015, 10:39:59 AM
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
package com.wj.android.messageviewer.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Various test for class {@link RecentCollection}.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class RecentCollectionTest
{
   private static Preferences m_Preferences;

   private final RecentCollection<String> m_Recent;

   /**
    * Constructs a new {@code RecentCollectionTest} object.
    */
   public RecentCollectionTest()
   {
      m_Recent = new RecentCollection<>(m_Preferences, "recentTestFiles");
   }

   /**
    * Acquire a {@code Preferences} node.
    *
    * <p>
    *     Run once before any of the test methods.
    * </p>
    */
   @BeforeClass
   public static void setUPPreferences()
   {
      m_Preferences = Preferences.userNodeForPackage(RecentCollectionTest.class);
   }

   /**
    * Force any changes in the contents of the {@code Preference} node.
    *
    * <p>
    *    Run after all the tests in the class have been run.
    * </p>
    *
    * @throws BackingStoreException if an error in the backing store occurs
    */
   @AfterClass
   public static void flushPreferences() throws BackingStoreException
   {
      m_Preferences.flush();
   }

   /**
    * Ensure recent collection is clear.
    *
    * <p>
    *    run before every test method.
    * </p>
    *
    * @throws BackingStoreException if an error in the backing store occurs
    */
   @Before
   public void setUp() throws BackingStoreException
   {
      m_Recent.clear();
   }

   /**
    * Ensure recent collection is clear.
    *
    * <p>
    *    run after every test method.
    * </p>
    *
    * @throws BackingStoreException if an error in the backing store occurs
    */
   @After
   public void tearDown() throws BackingStoreException
   {
      m_Recent.clear();
   }

   /**
    * Test if added more then ten elements the least recently used are removed.
    *
    * @throws BackingStoreException in case of an error in backing store.
    * @throws UnsupportedEncodingException if an unsupported encoding is used
    * @throws IOException if an IO error occurred
    * @throws ClassNotFoundException if a class could not be found
    */
   @Test
   public void testOverflow() throws BackingStoreException, UnsupportedEncodingException, IOException, ClassNotFoundException
   {
      // test overflow
      for (int i = 0; i < 20; i++)
         m_Recent.add("" + i);

      final Iterator<String> files = m_Recent.iterator();
      for (int i = 19; i >= 10; i--)
         assertEquals("" + i, files.next());

      assertFalse(files.hasNext());

      for (int i = 19; i >= 10; i--)
      {
         final String str = m_Recent.getObject(m_Preferences, "recentTestFiles." + (i - 10)).toString();
         assertEquals("" + i, str);
      }
   }

   /**
    * test file added that is there already.
    */
   @Test
   public void testAddingAlreadyExisting()
   {
      m_Recent.add("hello");
      m_Recent.add("hello");
      m_Recent.add("hello");
      m_Recent.add("goodbye");
      m_Recent.add("goodbye");
      m_Recent.add("goodbye");
      m_Recent.add("hello");

      final Iterator<String> files = m_Recent.iterator();
      assertEquals("hello", files.next());
      assertEquals("goodbye", files.next());
      assertFalse(files.hasNext());
   }
}
