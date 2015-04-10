/*
 * $Id$
 *
 * File:   SkypeMessageReader.java
 * Author: Werner Jaeger
 *
 * Created on Apr 2, 2015, 5:14:36 PM
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads {@code Skype} conversations from a {@code Skype} {@code SQLite}
 * main database.
 *
 * <p>
 *  Skype conversations are stored in {@code main.db} in a archive file with
 *  name pattern {@code com.skype.raider-YYMMDD-HHMMSS.tar.gz}
 *  in the {@code TitaniumBackup} folder on the mobile phone.
 * </p>
 *
 * @author Werner Jaeger
 */
public class SkypeMessageReader implements IMessageReader
{
   private static final Logger LOGGER = Logger.getLogger(SkypeMessageReader.class.getName());

   private static final String ID = "id";
   private static final String CONVID = "convo_id";
   private static final String IDENTITY = "identity";
   private static final String DISPLAYNAME = "displayname";
   private static final String AUTHOR = "author";
   private static final String BODYXML = "body_xml";
   private static final String TIMESTAMP = "timestamp";
   private static final String TYPE = "type";
   private static final String CONVERSATIONSQUERY = "select c." + ID + ", c." + IDENTITY + ", c." + DISPLAYNAME + " from Conversations c, Messages m where m."
           + CONVID + " == c." + ID + " and m." + TYPE + " == 61 group by c." + ID;
   private static final String MESSAGESQUERY = "select m." + CONVID + ", m." + AUTHOR + ", m." + BODYXML + ", m." + TIMESTAMP
           + " from Messages m where m." + TYPE + " == 61 and LENGTH(" + BODYXML + ") > 0";

   private final Map<Integer, MessageThread> m_Conversations;

   private int m_iNumberOfMessages;

   private File m_MainDB;

   private Connection m_Connection;

   /**
    * Creates new {@code SQLLiteContactsReader}.
    */
   public SkypeMessageReader()
   {
      m_Conversations = new LinkedHashMap<>(32, 0.7f, false);
      m_iNumberOfMessages = 0;
   }

   /**
    * Reads Skype conversations from file
    * {@code com.skype.raider-YYMMDD-HHMMSS.tar.gz} stored in the
    * {@code TitaniumBackup} folder or from Skype {@code main.db}.
    *
    * @param is Always {@code null}.
    * @param mainDB {@code com.skype.raider-YYMMDD-HHMMSS.tar.gz} or
    *        {@code SQLLite} {@code Skype} main database file. Must not be
    *        {@code null}.
    *
    * @return the error code -3 meaning Skype database file not found,
    *         0 meaning success and 3 other reading problems.
    */
   @Override
   public int loadMessages(final InputStream is, final File mainDB)
   {
      int iRet = -3;

      m_Conversations.clear();
      m_iNumberOfMessages = 0;

      if (instantiateMainDb(mainDB))
      {
         if (connect())
         {
            if (queryConversations())
            {
               if (addMessages())
                  iRet = 0;
               else
                  iRet = 3;
            }
            else
               iRet = 3;
         }
         else
            iRet = 3;
      }

      return(iRet);
   }

   /**
    * Get an array of all message threads.
    *
    * @return array of all threads. Never {@code null}.
    */
   @Override
   public MessageThread[] getThreadArray()
   {
      return(m_Conversations.values().toArray(new MessageThread[m_Conversations.size()]));
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

   private boolean instantiateMainDb(final File mainDB)
   {
      if (null != mainDB)
      {
         if (mainDB.getName().endsWith(".tar.gz") || mainDB.getName().endsWith("tar"))
         {
            final String strTempDir = System.getProperty("java.io.tmpdir");
            final File skypeConversationTempDir = new File(strTempDir, "titaniumBackupSkypeConversations");
            m_MainDB = ExtractTarGz.extractTarGzAndFindFirst(mainDB, skypeConversationTempDir, "main.db");
         }
         else if (mainDB.getName().endsWith(".db"))
            m_MainDB = mainDB;
      }

      return(null != m_MainDB);
   }


   private boolean connect()
   {
      boolean fRet;

      try
      {
         Class.forName("org.sqlite.JDBC");
         m_Connection = DriverManager.getConnection("jdbc:sqlite:" + m_MainDB.getCanonicalPath());
         fRet = true;
      }
      catch (final ClassNotFoundException | SQLException | IOException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
         m_Connection = null;
         fRet = false;
      }
      return(fRet);
   }

   private boolean queryConversations()
   {
      boolean fRet = false;

      try (final PreparedStatement statement = m_Connection.prepareStatement(CONVERSATIONSQUERY))
      {
         statement.setQueryTimeout(30);  // set timeout to 30 sec.

         try (final ResultSet rs = statement.executeQuery())
         {
            while (rs.next())
            {
               final int iId = rs.getInt(ID);
               final String strIdentity = rs.getString(IDENTITY);
               final String strDisplayName = rs.getString(DISPLAYNAME);
               m_Conversations.put(iId, new MessageThread(strDisplayName, strIdentity));
            }

            fRet = true;
         }
      }
      catch (final SQLException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
      }

      return(fRet);
   }

   private boolean addMessages()
   {
      boolean fRet = true;

      try (final PreparedStatement statement = m_Connection.prepareStatement(MESSAGESQUERY))
      {
         statement.setQueryTimeout(30);  // set timeout to 30 sec.

         try (final ResultSet rs = statement.executeQuery())
         {
            while (rs.next())
            {
               final int iId = rs.getInt(CONVID);
               final String strAuthor = rs.getString(AUTHOR);
               final String strBody = rs.getString(BODYXML);
               final long lTimestamp = rs.getLong(TIMESTAMP);

               final MessageThread thread = m_Conversations.get(iId);
               if (null != thread)
               {
                  final IMessage.MessageBox msgBox;
                  if (thread.getAddress().equals(strAuthor))
                     msgBox = IMessage.MessageBox.INBOX;
                  else
                     msgBox = IMessage.MessageBox.SENT;

                  final IMessage msg = new SMSMessage("", strAuthor, new Date(lTimestamp * 1000), strBody, msgBox);
                  thread.addMessage(msg);
                  m_iNumberOfMessages++;
               }
            }
         }
      }
      catch (final SQLException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
         fRet = false;
      }

      return(fRet);
   }
}
