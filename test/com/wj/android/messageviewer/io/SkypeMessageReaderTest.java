/*
 * $Id$
 *
 * File:   SkypeMessageReaderTest.java
 * Author: Werner Jaeger
 *
 * Created on Apr 2, 2015, 6:40:10 PM
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
package com.wj.android.messageviewer.io;

import com.wj.android.messageviewer.message.MessageThread;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;

/**
 *Various test for class {@link SkypeMessageReader}.
 *
 * @author Werner Jaeger>
 */
public final class SkypeMessageReaderTest
{
   private static MessageThread[] m_aCheckThreads;

   private File m_MainDB;
   private IMessageReader m_MessageReader;

   /**
    * Constructs a new {@code SkypeMessageReaderTest} object.
    */
   public SkypeMessageReaderTest()
   {
   }

   /**
    * Creates check messages
    *
    * <p>
    *    Run once before any of the test methods.
    * </p>
    *
    * @throws ParseException if en error occurs parsing a check date string.
    */
   @BeforeClass
   public static void createCheckMesages() throws ParseException
   {
      final MessageThread[] aThreads = {
          new MessageThread("Test 2", "test2")
      };

      m_aCheckThreads = aThreads;
   }

   /**
    * Acquire an {@code InputStream} and a {@code TitaniumBackupMessageReader}
    * instance.
    *
    * <p>
    *     Run once before any of the test methods.
    * </p>
    * @throws URISyntaxException if test resource URL can not be converted to
    *         a URI.
    */
   @Before
   public void setUp() throws URISyntaxException
   {
      final URL url = getClass().getResource("testdata/main.db");
      Assert.assertNotNull("Resource testdata/main.db not found.", url);
      m_MainDB = new File(url.toURI());

      m_MessageReader = new SkypeMessageReader();
   }

   /**
    * Test of {@code loadMessages} method, of class
    * {@code SkypeMessageReader}.
    */
   @Test
   public void testLoadMessages()
   {
      final int iResult = m_MessageReader.loadMessages(null, m_MainDB);
      assertEquals(0, iResult);
   }

   /**
    * Test of {@code getThreadArray} method, of class
    * {@code SkypeMessageReader}.
    */
   @Test
   public void testGetThreadArray()
   {
      final int iResult = m_MessageReader.loadMessages(null, m_MainDB);
      assertEquals(0, iResult);

      final MessageThread[] aThreads = m_MessageReader.getThreadArray();
      assertArrayEquals(m_aCheckThreads, aThreads);
   }

   /**
    * Test of {@code getNumberOfMessages} method, of class
    * {@code SkypeMessageReader}.
    */
   @Test
   public void testGetNumberOfMessages()
   {
      final int iResult = m_MessageReader.loadMessages(null, m_MainDB);
      assertEquals(0, iResult);

      final int iNoOfMessages = m_MessageReader.getNumberOfMessages();
      assertEquals(2, iNoOfMessages);
   }

   /**
    * Test the messages returned by {@code getThreadArray} method, of class
    * {@code SkypeMessageReader}.
    */
   @Test
   public void testGetThreadArrayMessages()
   {
      final int iResult = m_MessageReader.loadMessages(null, m_MainDB);
      assertEquals(0, iResult);

      final MessageThread[] aThreads = m_MessageReader.getThreadArray();
      final MessageThread thread = aThreads[0];
      assertEquals(2, thread.getMessages().size());
   }
}
