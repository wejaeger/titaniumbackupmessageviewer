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

import com.wj.android.messageviewer.message.IMMSMessagePart;
import com.wj.android.messageviewer.message.IMessage;
import com.wj.android.messageviewer.message.MMSMessage;
import com.wj.android.messageviewer.message.MessageThread;
import com.wj.android.messageviewer.message.SMILMessage;
import com.wj.android.messageviewer.message.SMSMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
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

   private static final String DEFAULTCHARSET = Charset.defaultCharset().name();

   private final Set<MessageThread> m_ThreadList;
   private int m_iNumberOfMessages;

   /**
    * Constructs a new {@code SMSBackupAndRestoreMessageReader}.
    */
   public SMSBackupAndRestoreMessageReader()
   {
      m_ThreadList = new TreeSet<>();
      m_iNumberOfMessages = 0;
   }

   /**
    * Get the number of all messages in all threads.
    *
    * @return the number of messages
    */
   @Override
   public int getNumberOfMessages()
   {
      return(m_iNumberOfMessages);
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
         m_iNumberOfMessages = 0;

         try
         {
            final Reader reader = new InputStreamReader(is, DEFAULTCHARSET);

            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(new InputSource(reader));

            doc.getDocumentElement().normalize();

            final Element docEle = doc.getDocumentElement();

            // Get a nodelist of elements in the XML file
            final NodeList nl = docEle.getElementsByTagName("*");
            if (nl != null && nl.getLength() > 0)
            {
               final Map<String, MessageThread> contactsMap = new LinkedHashMap<>(32, 0.7f, true);

               for (int i = 0; i < nl.getLength(); i++)
               {
                  final Element messageElement = (Element)nl.item(i);
                  final String  strTagName = messageElement.getTagName();

                  final String strAddress       = messageElement.getAttribute("address");
                  final String strServiceCenter = messageElement.getAttribute("service_center");

                  final IMessage message;
                  switch(strTagName)
                  {
                     case "sms":
                     {
                        final IMessage.MessageBox msgBox = IMessage.MessageBox.fromString(messageElement.getAttribute("type"));
                        if (null == msgBox)
                        {
                           message = null;
                           LOGGER.log(Level.WARNING, "Unknown message box type: ''{0}''", messageElement.getAttribute("type"));
                        }
                        else
                           message = elementToMessage(msgBox, strServiceCenter, strAddress, messageElement);
                     }
                        break;

                     case "mms":
                     {
                        final IMessage.MessageBox msgBox = IMessage.MessageBox.fromString(messageElement.getAttribute("msg_box"));
                        if (null == msgBox)
                        {
                           message = null;
                           LOGGER.log(Level.WARNING, "Unknown message box type: ''{0}''", messageElement.getAttribute("msg_box"));
                        }
                        else
                           message = elementToMMSMessage(msgBox, strServiceCenter, messageElement);
                     }
                        break;

                     case "part":
                     case "parts":
                        message = null;
                        break;

                     default:
                        LOGGER.log(Level.WARNING, "Unexpected tagname: ''{0}''", strTagName);
                        message = null;
                  }

                  if (null != message)
                  {
                     m_iNumberOfMessages++;
                     if (!contactsMap.containsKey(strAddress))
                     {
                        final MessageThread thread = new MessageThread(messageElement.getAttribute("contact_name"), strAddress);
                        thread.addMessage(message);
                        contactsMap.put(strAddress, thread);
                     }
                     else
                        contactsMap.get(strAddress).addMessage(message);
                  }
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


   private IMessage elementToMessage(final IMessage.MessageBox msgBox, final String strServiceCenter, final String strAddress, final Element element)
   {
      final long lTime = Long.parseLong(element.getAttribute("date"));
      final String strBody = element.getAttribute("body");

      return(new SMSMessage(strServiceCenter.equals("null") ? "" : strServiceCenter, strAddress, new Date(lTime), strBody, msgBox));
   }

   private IMessage elementToMMSMessage(final IMessage.MessageBox msgBox, final String strServiceCenter, final Element element) throws UnsupportedEncodingException
   {
      final MMSMessage msg;

      final String strAddress = element.getAttribute("address");
      final List<IMMSMessagePart> parts = retrieveParts(element);
      final long lTime = Long.parseLong(element.getAttribute("date"));
      final String strSubject = element.getAttribute("sub");

      if (hasSMILPart(parts))
      {
         msg = new SMILMessage(parts, strServiceCenter, strAddress, new Date(lTime), msgBox);
         msg.setSubject(strSubject.equals("null") ? "" : strSubject);
      }
      else
      {
         msg = new MMSMessage(parts, strServiceCenter, strAddress, new Date(lTime), msgBox);
         msg.setSubject(strSubject.equals("null") ? "" : strSubject);
      }

      return(msg);
   }

   private static List<IMMSMessagePart> retrieveParts(final Element element) throws UnsupportedEncodingException
   {
      final List<IMMSMessagePart> partsRet = new ArrayList<>();

      final NodeList parts = element.getElementsByTagName("part");

      if (null != parts)
      {
         for (int i = 0; i < parts.getLength(); i++)
         {
            final Element partElement        = (Element)parts.item(i);
            final String  strContentType     = partElement.getAttribute("ct");
            final String  strContentId       = partElement.getAttribute("cid");
            final String  strContentLocation = partElement.getAttribute("cl");

            final IMMSMessagePart.ContentType contentType = IMMSMessagePart.ContentType.fromString(strContentType);
            if (null != contentType)
            {
               final IMessage.Encoding encoding;
               if (contentType == IMMSMessagePart.ContentType.APPLICATIONSMIL || contentType == IMMSMessagePart.ContentType.TEXTPLAIN)
                  encoding = IMessage.Encoding.PLAIN;
               else
                  encoding = IMessage.Encoding.BAS64;

               final byte[] abContent;
               if (encoding == IMessage.Encoding.BAS64)
                  abContent = DatatypeConverter.parseBase64Binary(partElement.getAttribute("data"));
               else
                  abContent = partElement.getAttribute("text").getBytes(DEFAULTCHARSET);

               final IMMSMessagePart messagePart = contentType.newMessagePart(contentType, strContentId, strContentLocation, abContent, DEFAULTCHARSET);
               if (null != messagePart)
                  partsRet.add(messagePart);
               else
                  LOGGER.log(Level.WARNING, "Failed to instantiate message part for content typ: ''{0}''", strContentType);
            }
            else
               LOGGER.log(Level.WARNING, "Unknown content typ: ''{0}''", strContentType);
         }
      }

      return(partsRet);
   }

   private static boolean hasSMILPart(final List<IMMSMessagePart> parts)
   {
      boolean fRet = false;

      for (final IMMSMessagePart part : parts)
      {
         if (part.getContentType() == IMMSMessagePart.ContentType.APPLICATIONSMIL)
         {
            fRet = true;
            break;
         }
      }

      return(true);
   }
}
