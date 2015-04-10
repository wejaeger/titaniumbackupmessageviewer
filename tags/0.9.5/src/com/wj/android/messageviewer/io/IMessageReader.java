/*
 * $Id$
 *
 * File:   IMessageReader.java
 * Author: Werner Jaeger
 *
 * Created on Mar 8, 2015, 1:35:45 PM
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
import com.wj.android.messageviewer.resources.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Interface definition for message readers.
 *
 * @author Werner Jaeger
 */
public interface IMessageReader
{
    /**
    * Determine the message file type for a specified message file and
    * provide the appropriate reader implementation.
    */
   public enum MessageFileType
   {
      /** A Titanium Backup message file. */
      TITANIUM (new TitaniumBackupMessageReader()),
      /** a SMS Backup and Restore message file */
      SMSBACKUPANDRESTORE (new SMSBackupAndRestoreMessageReader()),
      /** a Skype message reader */
      SKYPE (new SkypeMessageReader());

      private final IMessageReader m_MessageReader;

      private MessageFileType(final IMessageReader messageReader)
      {
         m_MessageReader = messageReader;
      }

      /**
       * Get the appropriate reader for this type.
       *
       * @return an appropriate reader implementation. Never {@code null}.
       */
      public final IMessageReader reader()
      {
         return(m_MessageReader);
      }

      /**
       * Returns the type of the specified message file.
       *
       * @param strAbsoluteFileNamePath absolute path to the file to check. If
       *          {@code null} or file type unknown {@code null} is returned.
       * @return the type or {@code null} if not known.
       */
      public static MessageFileType getMessageFileType(final String strAbsoluteFileNamePath)
      {
         MessageFileType messageType = null;

         if (null == strAbsoluteFileNamePath || (!strAbsoluteFileNamePath.contains("com.skype.raider-") && !strAbsoluteFileNamePath.endsWith("main.db")))
         {
            final String strSMSSchema = Resources.getSMSBackaupSchema();
            final String strTitaniumSchema = Resources.getTitaniumBackaupSchema();
            if (null != strAbsoluteFileNamePath && null != strSMSSchema && null != strTitaniumSchema)
            {
               if (strAbsoluteFileNamePath.startsWith("sms-"))
               {
                  // try check for SMS Backup Reader and Restore first
                  if (isValid(strAbsoluteFileNamePath, strSMSSchema))
                     messageType = MessageFileType.SMSBACKUPANDRESTORE;
                  else if (isValid(strAbsoluteFileNamePath, strTitaniumSchema))
                     messageType = MessageFileType.TITANIUM;
               }
               else
               {
                  // try check for Titanium Backup Reader and Restore first
                  if (isValid(strAbsoluteFileNamePath, strTitaniumSchema))
                     messageType = MessageFileType.TITANIUM;
                  else if (isValid(strAbsoluteFileNamePath, strSMSSchema))
                     messageType = MessageFileType.SMSBACKUPANDRESTORE;
               }
            }
         }
         else
            messageType =  MessageFileType.SKYPE;

         return(messageType);
      }

      private static boolean isValid(final String strAbsoluteFileNamePath, final String strSchema)
      {
         boolean fRet = true;
         InputStream is = null;

         try
         {
            final File messageFile = new File(strAbsoluteFileNamePath);
            if (strAbsoluteFileNamePath.endsWith(".gz"))
               is = new GZIPInputStream(new FileInputStream(messageFile));
            else
               is = new FileInputStream(messageFile);

            // parse an XML document into a DOM tree
            final DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document document = parser.parse(is);

            // create a SchemaFactory capable of understanding WXS schemas
            final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // load a WXS schema, represented by a Schema instance
            final Schema schema = factory.newSchema(new StreamSource(new StringReader(strSchema)));

            // create a Validator instance, which can be used to validate an instance document
            final Validator validator = schema.newValidator();

            validator.validate(new DOMSource(document));
         }
         catch (final ParserConfigurationException | SAXException | IOException ex)
         {
            fRet = false;

            if (ex instanceof SAXException)
               Logger.getLogger(MessageFileType.class.getName()).log(Level.FINER, ex.toString(), ex);
            else
               Logger.getLogger(MessageFileType.class.getName()).log(Level.SEVERE, ex.toString(), ex);
         }
         finally
         {
            try
            {
               if (is != null)
                  is.close();
            }
            catch (IOException ex)
            {
               Logger.getLogger(MessageFileType.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            }
         }

         return(fRet);
      }
   }

   /**
    * Get the number of all messages in all threads.
    *
    * @return the number of messages
    */
   int getNumberOfMessages();

   /**
    * Get an array of all message threads.
    *
    * @return array of all threads. Never {@code null}.
    */
   MessageThread[] getThreadArray();

   /**
    * Reads a message file as stored from the Titanium Backup application.
    *
    * @param is the message file input stream to read from.
    *        Must not be {@code null}.
    * @param contactsDB the SQLLite contact database file or {@code null}.
    *
    * @return the error code -3 meaning contact database file not found,
    *         0 meaning success, 1 {@code is} == {@code null}, 2
    *         invalid XML and 3 other reading problems.
    */
   int loadMessages(final InputStream is, final File contactsDB);
}
