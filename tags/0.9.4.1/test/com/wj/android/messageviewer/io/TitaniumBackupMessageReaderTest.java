/*
 * $Id$
 *
 * File:   TitaniumBackupMessageReaderTest.java
 * Author: Werner Jaeger
 *
 * Created on Mar 5, 2015, 8:00:30 AM
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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.BeforeClass;

/**
 *Various test for class {@link TitaniumBackupMessageReader}.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public final class TitaniumBackupMessageReaderTest
{
   private static IMessage[] m_aCheckMessages;

   private InputStream m_InputStram;
   private IMessageReader m_MessageReader;

   /**
    * Constructs a new {@code TitaniumBackupMessageReaderTest} object.
    */
   public TitaniumBackupMessageReaderTest()
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
      final IMessage.MessageBox draft = IMessage.MessageBox.fromString("draft");

      final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // e.g. 2015-01-12T08:43:30.830Z
      dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
      final Date date1 = dateFmt.parse("2015-01-10T20:01:00.00Z");
      final Date date2 = dateFmt.parse("2015-01-10T20:02:10.00Z");
      final Date date3 = dateFmt.parse("2015-01-10T20:03:10.00Z");
      final Date date4 = dateFmt.parse("2015-01-10T20:04:00.00Z");
      final Date date5 = dateFmt.parse("2015-01-10T20:05:00.00Z");

      final IMessage[] aMsg = {
          new SMSMessage("", "Test", date1, "I have sent a test message. Only for test purposes to demonstrate the look and feel of the message viewer", sent),
          new SMSMessage("Test", "Test", date2, "This is a message I have received", inbox),
          new SMSMessage("Test", "Test", date3, "Yet another received text message", inbox),
          new SMSMessage("", "Test", date4, "I have replied to a test message", sent),
          new SMSMessage("", "Test", date5, "Not yet sent. Just a draft", draft)
      };

      m_aCheckMessages = aMsg;
   }

   /**
    * Acquire an {@code InputStream} and a {@code TitaniumBackupMessageReader}
    * instance.
    *
    * <p>
    *     Run once before any of the test methods.
    * </p>
    */
   @Before
   public void setUp()
   {
      m_InputStram = getClass().getResourceAsStream("testdata/com.keramidas.virtual.XML_MESSAGES-00000000-000000.xml");
      Assert.assertNotNull("Resource testdata/com.keramidas.virtual.XML_MESSAGES-00000000-000000.xml not found.", m_InputStram);

      m_MessageReader = new TitaniumBackupMessageReader();
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
    * {@code TitaniumBackupMessageReader}.
    */
   @Test
   public void testLoadMessages()
   {
      final int iResult = m_MessageReader.loadMessages(m_InputStram, null);
      assertEquals(0, iResult);
   }

   /**
    * Test of {@code getThreadArray} method, of class
    * {@code TitaniumBackupMessageReader}.
    */
   @Test
   public void testGetThreadArray()
   {
      final int iResult = m_MessageReader.loadMessages(m_InputStram, null);
      assertEquals(0, iResult);

      final MessageThread[] aExpThread = {new MessageThread(null, "Test")};
      final MessageThread[] aThreads = m_MessageReader.getThreadArray();
      assertArrayEquals(aExpThread, aThreads);
   }

   /**
    * Test of {@code getNumberOfMessages} method, of class
    * {@code TitaniumBackupMessageReader}.
    */
   @Test
   public void testGetNumberOfMessages()
   {
      final int iResult = m_MessageReader.loadMessages(m_InputStram, null);
      assertEquals(0, iResult);

      final int iNoOfMessages = m_MessageReader.getNumberOfMessages();
      assertEquals(5, iNoOfMessages);
   }

   /**
    * Test the messages returned by {@code getThreadArray} method, of class
    * {@code TitaniumBackupMessageReader}.
    */
   @Test
   public void testGetThreadArrayMessages()
   {
      final int iResult = m_MessageReader.loadMessages(m_InputStram, null);
      assertEquals(0, iResult);

      final MessageThread[] aThreads = m_MessageReader.getThreadArray();
      final MessageThread thread = aThreads[0];
      assertEquals(5, thread.getMessages().size());
      final Collection<IMessage> messages = thread.getMessages();

      int i = 0;
      for (final IMessage msg : messages)
         assertThat(msg, is(m_aCheckMessages[i++]));
   }
}
