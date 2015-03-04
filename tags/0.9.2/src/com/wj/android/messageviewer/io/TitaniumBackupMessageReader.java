/*
 * $Id$
 *
 * File:   AndroidMesssageReader.java
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
package com.wj.android.messageviewer.io;

import com.wj.android.messageviewer.message.IMMSMessagePart;
import com.wj.android.messageviewer.message.IMessage;
import com.wj.android.messageviewer.message.MMSMessage;
import com.wj.android.messageviewer.message.MessageThread;
import com.wj.android.messageviewer.message.SMILMessage;
import com.wj.android.messageviewer.message.SMSMessage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Loads and exports message {@code xml} files from Titanium Backup folder.
 *
 * <p>
 *  Message files appear as file
 *  {@code com.keramidas.virtual.XML_MESSAGES-YYYYMMDD-HHMMSS.xml.gz} in
 *    the {@code TitaniumBackup} folder on the mobile phone.
 * </p>
 *
 * <p>
 *    Optionally, if a SQLLite contacts database is specified, a
 *    {@link SQLLiteContactsReader} is instantiated and used to query the
 *    contact name from thread address.
 * </p>
 *
 * @author Werner Jaeger
 */
public class TitaniumBackupMessageReader
{
   private static final Logger LOGGER = Logger.getLogger(TitaniumBackupMessageReader.class.getName());

   private final static String DEFAULTCHARSET = Charset.defaultCharset().name();

   final private SimpleDateFormat m_DateFmt;
   final private List<MessageThread> m_ThreadList;
   private SQLLiteContactsReader m_ContactReader;
   private int m_iNumberOfSMS;

   /**
    * Creates new {@code TitaniumBackupMessageReader}.
    */
   public TitaniumBackupMessageReader()
   {
      m_DateFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // e.g. 2015-01-12T08:43:30.830Z
      m_ThreadList = new ArrayList<>();
      m_ContactReader = null;
      m_iNumberOfSMS = 0;
      m_DateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
   }

   /**
    * Reads a message file as stored from the Titanium Backup application.
    *
    * @param is the message file input stream to read from.
    *        Must not be {@code null}.
    * @param contactsDB the SQLLite contact database file or {@code null}.
    *
    * @return the error code 0 meaning success, 1 {@code is} == {@code null}, 2
    *          invalid XML and 3 other reading problems.
    */
   public int loadMessages(final InputStream is, final File contactsDB)
   {
      int iErorror = 1;

      if (is != null)
      {
         m_ThreadList.clear();
         m_iNumberOfSMS = 0;

         if (null == m_ContactReader)
            instantiateContactReader(contactsDB);

         try
         {
            final Reader reader = new InputStreamReader(is, DEFAULTCHARSET);

            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(new InputSource(reader));

            doc.getDocumentElement().normalize();

            final Element docEle = doc.getDocumentElement();

            final NodeList threads = docEle.getElementsByTagName("thread");

            if ((threads != null) && (threads.getLength() > 0))
            {
               for (int i = 0; i < threads.getLength(); i++)
               {
                  if (Node.ELEMENT_NODE == threads.item(i).getNodeType())
                  {
                     final Element threadElement = (Element)threads.item(i);
                     final String strAddress = threadElement.getAttribute("address");

                     final String strContactName = m_ContactReader == null ? null : m_ContactReader.getContactNameForAddress(strAddress.split(";", 2)[0]);
                     final MessageThread thread = new MessageThread(strContactName, strAddress);
                     final NodeList messages = threadElement.getChildNodes();

                     m_iNumberOfSMS += messages.getLength();

                     for (int j = 0; j < messages.getLength(); j++)
                     {
                        if (Node.ELEMENT_NODE == messages.item(j).getNodeType())
                        {
                           final Element             messageElement   = (Element)messages.item(j);
                           final String              strTagName       = messageElement.getTagName();
                           final String              strServiceCenter = messageElement.getAttribute("serviceCenter");
                           final String              strMsgBox        = messageElement.getAttribute("msgBox");
                           final IMessage.MessageBox msgBox           = IMessage.MessageBox.fromString(strMsgBox);

                           if (null != msgBox)
                           {
                              final IMessage message;
                              switch(strTagName)
                              {
                                 case "sms":
                                    message = elementToMessage(msgBox, strServiceCenter, strAddress, messageElement);
                                    break;

                                 case "mms":
                                    message = elementToMMSMessage(msgBox, strServiceCenter, messageElement);
                                    break;

                                 default:
                                    LOGGER.log(Level.WARNING, "Unknown message type: ''{0}''", strTagName);
                                    message = null;
                              }

                              if (null != message)
                                 thread.addMessage(message);
                           }
                           else
                              LOGGER.log(Level.WARNING, "Unknown message box type: ''{0}''", strMsgBox);
                        }
//                        else
//                           LOGGER.log(Level.WARNING, "Unexpected non message ELEMENT_NODE {0}", messages.item(j).getNodeName());

                     }
                     m_ThreadList.add(thread);
                  }
//                  else
//                     LOGGER.log(Level.WARNING, "Unexpected non thread ELEMENT_NODE {0}", threads.item(i).getNodeName());
               }

               iErorror = 0;
            }
         }
         catch (SAXException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            iErorror = 2;
         }
         catch (ParserConfigurationException | ParseException | IOException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            iErorror = 3;
         }
      }
      return(iErorror);
   }

   /**
    * Export the messages as plain text of the thread given by the contact
    * index to the specified file.
    *
    * @param saveFile the file to save the messages to.
    * @param iContactIndex index in message tread.
    *
    * @return {@code true} if and only if messages are exported successfully.
    */
   public boolean exportThreadMessages(final File saveFile, final int iContactIndex)
   {
      boolean fRet = true;

      try
      {
         try (final BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), DEFAULTCHARSET)))
         {
            final Collection<IMessage> selectedMessages = m_ThreadList.get(iContactIndex).getMessages();

            outputWriter.write(m_ThreadList.get(iContactIndex).toString());
            outputWriter.newLine();
            outputWriter.newLine();

            for (IMessage selectedMessage : selectedMessages)
            {
               outputWriter.write(selectedMessage.toString());
               outputWriter.newLine();
            }

            outputWriter.flush();
         }
      }
      catch (IOException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
         fRet = false;
      }

      return(fRet);
   }

   /**
    * Export all messages as plain text to the specified file.
    *
    * @param saveFile the file to save the messages to.
    * @return {@code true} if and only if messages are exported successfully.
    */
   public boolean exportAllMessages(final File saveFile)
   {
      boolean fRet = true;

      try (final BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), DEFAULTCHARSET)))
      {
         for (final MessageThread selectedContact : m_ThreadList)
         {
            final Collection<IMessage> selectedMessages = selectedContact.getMessages();

            outputWriter.write(selectedContact.toString());
            outputWriter.newLine();
            outputWriter.newLine();

            for (final IMessage selectedMessage : selectedMessages)
            {
               outputWriter.write(selectedMessage.toString());
               outputWriter.newLine();
            }

            outputWriter.write("++++++++++++++++++++++++++++++++++++++++++++++++++");
            outputWriter.newLine();
         }
      }
      catch (final IOException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
         fRet = false;
      }

      return(fRet);
   }

   /**
    * Get an array of all message threads.
    *
    * @return array of all threads. Never {@code null}.
    */
   public MessageThread[] getThreadArray()
   {
      return(m_ThreadList.toArray(new MessageThread[m_ThreadList.size()]));
   }

   /**
    * Get the number of all messages in all threads.
    *
    * @return the number of messages
    */
   public int getNumberOfSMS()
   {
      return(m_iNumberOfSMS);
   }

   private IMessage elementToMessage(final IMessage.MessageBox msgBox, final String strServiceCenter, final String strAddress, final Element element) throws ParseException, UnsupportedEncodingException
   {
      final IMessage msg;

      if (element.hasChildNodes())
      {
         if (element.getFirstChild().getNodeType() == Node.TEXT_NODE)
         {
            final Date date = m_DateFmt.parse(element.getAttribute("date"));

            final IMessage.Encoding encoding = IMessage.Encoding.fromString(element.getAttribute("encoding"));

            final String strBody;
            if (encoding == IMessage.Encoding.BAS64)
               strBody = new String(DatatypeConverter.parseBase64Binary(element.getFirstChild().getNodeValue()), DEFAULTCHARSET);
            else
               strBody = element.getFirstChild().getNodeValue();

            msg = new SMSMessage(strServiceCenter, strAddress, date, strBody, msgBox);
         }
         else
            msg = null;
      }
      else
         msg = null;

      return(msg);
   }

   private IMessage elementToMMSMessage(final IMessage.MessageBox msgBox, final String strServiceCenter, final Element element) throws ParseException, UnsupportedEncodingException
   {
      final MMSMessage msg;

      final String strAddress = retrieveAddress(msgBox, element);
      if (null != strAddress)
      {
         final List<IMMSMessagePart> parts = retrieveParts(element);

         if (hasSMILPart(parts))
         {
            msg = new SMILMessage(parts, strServiceCenter, strAddress, m_DateFmt.parse(element.getAttribute("date")), msgBox);
            msg.setSubject(element.getAttribute("subject"));
         }
         else
         {
            msg = new MMSMessage(parts, strServiceCenter, strAddress, m_DateFmt.parse(element.getAttribute("date")), msgBox);
            msg.setSubject(element.getAttribute("subject"));
         }
      }
      else
         msg = null;

      return(msg);
   }

   private boolean instantiateContactReader(final File contactsDB)
   {
      if (null != contactsDB)
      {
         if (contactsDB.getName().endsWith(".tar.gz") || contactsDB.getName().endsWith("tar"))
         {
            final String strTempDir = System.getProperty("java.io.tmpdir");
            final File contactsTempDir = new File(strTempDir, "titaniumBackupMessageViewerContacts");
            final File extractedContactsDB = ExtractTarGz.extractTarGzAndFindFirst(contactsDB, contactsTempDir, "contacts2.db");

            if (null != extractedContactsDB)
               m_ContactReader = new SQLLiteContactsReader(extractedContactsDB);
         }
         else if (contactsDB.getName().endsWith(".db"))
            m_ContactReader = new SQLLiteContactsReader(contactsDB);
      }

      return(null != m_ContactReader);
   }

   private static String retrieveAddress(final IMessage.MessageBox msgBox, final Element element)
   {
      String strAddress = null;

      final NodeList address = element.getElementsByTagName("address");
      if (null != address && address.getLength()== 2)
      {
         for (int i = 0; i < address.getLength(); i++)
         {
            final Element addressElement = (Element)address.item(i);
            final String strAddressType = addressElement.getAttribute("type");

            if (strAddressType.equals("from") && msgBox == IMessage.MessageBox.INBOX)
               strAddress = addressElement.getFirstChild().getNodeValue();
            else if (strAddressType.equals("to") && msgBox == IMessage.MessageBox.SENT)
               strAddress = addressElement.getFirstChild().getNodeValue();
         }
      }

      return(strAddress);
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
            final String  strContentType     = partElement.getAttribute("contentType");
            final String  strContentId       = partElement.getAttribute("contentId");
            final String  strContentLocation = partElement.getAttribute("contentLocation");
            final String  strEncoding        = partElement.getAttribute("encoding");

            final IMessage.Encoding encoding = IMessage.Encoding.fromString(strEncoding);

            if (null != encoding)
            {
               final byte[] abContent;
               if (encoding == IMessage.Encoding.BAS64)
                  abContent = DatatypeConverter.parseBase64Binary(partElement.getTextContent());
               else
                  abContent = partElement.getTextContent().getBytes(DEFAULTCHARSET);

               final IMMSMessagePart.ContentType contentType = IMMSMessagePart.ContentType.fromString(strContentType);

               if (null != contentType)
               {
                  final IMMSMessagePart messagePart = contentType.newMessagePart(contentType, strContentId, strContentLocation, abContent, DEFAULTCHARSET);
                  if (null != messagePart)
                     partsRet.add(messagePart);
                  else
                     LOGGER.log(Level.WARNING, "Failed to instatiate message üart for content typ: ''{0}''", strContentType);
               }
               else
                  LOGGER.log(Level.WARNING, "Unknown content typ: ''{0}''", strContentType);
            }
            else
               LOGGER.log(Level.WARNING, "Unknown character encoding: ''{0}''", strEncoding);
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

      return(fRet);
   }
}