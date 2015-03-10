/*
 * $Id$
 *
 * File:   SMSBackupAndRestoreMessageReader.java
 * Author: Werner Jaeger
 *
 * Created on Mar 9, 2015, 12:55:09 PM
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This program is designed to read the XML backup files produced by the
 * Android app SMS Backup & Restore by Ritesh.
 *
 * <p>
 *    SMS Backup and Restore Android App:
 *    <a href="https://market.android.com/details?id=com.riteshsahu.SMSBackupRestore">market</a>
 * </p>
 *
 * <p>
 *    You find more info about SMS Backup and Restore
 *    <a href="http://android.riteshsahu.com/apps/sms-backup-restore">here</a>
 * </p>
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class SMSBackupAndRestoreMessageReader implements IMessageReader
{
   private static final Logger LOGGER = Logger.getLogger(SMSBackupAndRestoreMessageReader.class.getName());

   private final static String DEFAULTCHARSET = Charset.defaultCharset().name();

   final private Set<MessageThread> m_ThreadList;
   private int m_iNumberOfSMS;

   /**
    * Constructs a new {@code SMSBackupAndRestoreMessageReader}.
    */
   public SMSBackupAndRestoreMessageReader()
   {
      m_ThreadList = new TreeSet<>();
      m_iNumberOfSMS = 0;
   }

   /**
    * Get the number of all messages in all threads.
    *
    * @return the number of messages
    */
   @Override
   public int getNumberOfMessages()
   {
      return(m_iNumberOfSMS);
   }

   /**
    * Get an array of all message threads.
    *
    * @return array of all threads. Never {@code null}.
    */
   @Override
   public MessageThread[] getThreadArray()
   {
      return(m_ThreadList.toArray(new MessageThread[m_ThreadList.size()]));
   }

   /**
    * Reads a message file as stored from the SMS Backup & Restore application.
    *
    * @param is the message file input stream to read from.
    *        Must not be {@code null}.
    * @param contactsDB always {@code null}, not used with this reader.
    *
    * @return the error code 0 meaning success, 1 {@code is} == {@code null}, 2
    *         invalid XML and 3 other reading problems.
    */
   @Override
   public int loadMessages(final InputStream is, final File contactsDB)
   {
      int iError = 0;

      if (is != null)
      {
         m_ThreadList.clear();
         m_iNumberOfSMS = 0;

         try
         {
            final Reader reader = new InputStreamReader(is, DEFAULTCHARSET);

            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(new InputSource(reader));

            doc.getDocumentElement().normalize();

            final Element docEle = doc.getDocumentElement();

            // Get a nodelist of elements in the XML file
            final NodeList nl = docEle.getElementsByTagName("sms");
            if(nl != null && nl.getLength() > 0)
            {
               final Map<String, MessageThread> contactsMap = new LinkedHashMap<>(32, 0.7f, true);

               for(int i = 0 ; i < nl.getLength(); i++)
               {
                  final Element element = (Element)nl.item(i);
                  final IMessage msg = elementToMessage(element);
                  m_iNumberOfSMS ++;
                  if (!contactsMap.containsKey(msg.getMessageAddress()))
                  {
                     final MessageThread thread = new MessageThread(element.getAttribute("contact_name"), msg.getMessageAddress());
                     thread.addMessage(msg);
                     contactsMap.put(msg.getMessageAddress(), thread);
                  }
                  else
                     contactsMap.get(msg.getMessageAddress()).addMessage(msg);
               }
               m_ThreadList.addAll(contactsMap.values());
               iError = 0;
            }
         }
         catch (SAXException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            iError = 2;
         }
         catch (ParserConfigurationException | IOException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            iError = 3;
         }
      }
      return(iError);
   }

   private IMessage elementToMessage(final Element element)
   {
      final String strServiceCenter = element.getAttribute("service_center");
      final String strAddress = element.getAttribute("address");
      final long lTime = Long.parseLong(element.getAttribute("date"));
      final String strBody = element.getAttribute("body");
      final IMessage.MessageBox msgBox = IMessage.MessageBox.fromString(element.getAttribute("type"));

      return(new SMSMessage(strServiceCenter.equals("null") ? "" : strServiceCenter, strAddress, new Date(lTime), strBody, msgBox));
   }
}
