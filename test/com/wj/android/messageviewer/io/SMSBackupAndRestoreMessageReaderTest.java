/*
 * $Id$
 *
 * File:   SMSBackupAndRestoreMessageReaderTest.java
 * Author: Werner Jaeger
 *
 * Created on Mar 9, 2015, 2:57:03 PM
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

import com.wj.android.messageviewer.message.IMessage;
import com.wj.android.messageviewer.message.MessageThread;
import com.wj.android.messageviewer.message.SMSMessage;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 *Various test for class {@link SMSBackupAndRestoreMessageReader}.
 *
 * @author Werner Jaeger
 */
public final class SMSBackupAndRestoreMessageReaderTest
{
   private static IMessage[] m_aCheckMessages;

   private InputStream m_InputStram;
   private IMessageReader m_MessageReader;

   /**
    * Constructs a new {@code SMSBackupAndRestoreMessageReaderTest} object.
    */
   public SMSBackupAndRestoreMessageReaderTest()
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
      final IMessage.MessageBox sent = IMessage.MessageBox.fromString("sent");
      final IMessage.MessageBox inbox = IMessage.MessageBox.fromString("inbox");

      final IMessage[] aMsg = {
          new SMSMessage("", "332", new Date(1285799668193L), "Sample Message Sent from the phone", sent),
          new SMSMessage("", "4433221123", new Date(1289643415810L), "Sample Message received by the phone", inbox),
      };

      m_aCheckMessages = aMsg;
   }

   /**
    * Acquire an {@code InputStream} and a {@code SMSBackupAndRestoreMessageReader}
    * instance.
    *
    * <p>
    *    Run once before any of the test methods.
    * </p>
    */
   @Before
   public void setUp()
   {
      m_InputStram = getClass().getResourceAsStream("testdata/sms-2015-03-09.xml");
      Assert.assertNotNull("Resource testdata/sms-2015-03-09.xml not found.", m_InputStram);

      m_MessageReader = new SMSBackupAndRestoreMessageReader();
   }

   /**
    * Close acquired input stream.
    *
    * <p>
    *    Run after all the tests in the class have been run.
    * </p>
    *
    * @throws IOException if closing of input stream fails.
    */
   @After
   public void tearDown() throws IOException
   {
      if (null != m_InputStram)
         m_InputStram.close();

      m_MessageReader = null;
      m_InputStram = null;
   }

   /**
    * Test of {@code loadMessages} method, of class
    * {@code SMSBackupAndRestoreMessageReader}.
    */
   @Test
   public void testLoadMessages()
   {
      final int iResult = m_MessageReader.loadMessages(m_InputStram, null);
      assertEquals(0, iResult);
   }

   /**
    * Test of {@code getThreadArray} method, of class
    * {@code SMSBackupAndRestoreMessageReader}.
    */
   @Test
   public void testGetThreadArray()
   {
      final int iResult = m_MessageReader.loadMessages(m_InputStram, null);
      assertEquals(0, iResult);

      final MessageThread[] aExpThread = {new MessageThread("(Unknown)", "332"), new MessageThread("(Unknown)", "4433221123")};
      final MessageThread[] aThreads = m_MessageReader.getThreadArray();
      assertArrayEquals(aExpThread, aThreads);
   }

   /**
    * Test of {@code getNumberOfMessages} method, of class
    * {@code SMSBackupAndRestoreMessageReader}.
    */
   @Test
   public void testGetNumberOfMessages()
   {
      final int iResult = m_MessageReader.loadMessages(m_InputStram, null);
      assertEquals(0, iResult);

      final int iNoOfMessages = m_MessageReader.getNumberOfMessages();
      assertEquals(2, iNoOfMessages);
   }

   /**
    * Test the messages returned by {@code getThreadArray} method, of class
    * {@code SMSBackupAndRestoreMessageReader}.
    */
   @Test
   public void testGetThreadArrayMessages()
   {
      final int iResult = m_MessageReader.loadMessages(m_InputStram, null);
      assertEquals(0, iResult);

      final MessageThread[] aThreads = m_MessageReader.getThreadArray();
      assertEquals(2, aThreads.length);

      int i = 0;
      for (final MessageThread thread : aThreads)
      {
         assertEquals(1, thread.getMessages().size());
         for (final IMessage msg : thread.getMessages())
            assertThat(msg, is(m_aCheckMessages[i++]));
      }
   }
}
