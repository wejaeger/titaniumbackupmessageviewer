/*
 * $Id$
 *
 * File:   SQLLiteContactsReader.java
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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads a Titanium Backup created SQLLite contact database.
 *
 * <p>
 *  Contacts database is stored as {@code contacts2.db} in a archive file with
 *  name pattern {@code com.android.providers.contacts-YYYYMMDD-HHMMSS.tar.gz}
 *  in the {@code TitaniumBackup} folder on the mobile phone.
 * </p>
 *
  * @author Werner Jaeger
 */
public class SQLLiteContactsReader
{
   private static final Logger LOGGER = Logger.getLogger(SQLLiteContactsReader.class.getName());

   private static final String DISPLAYNAME = "display_name";
   private static final String CONTACTNAMEQUERY1 = "select c." + DISPLAYNAME + " from raw_contacts c, phone_lookup p where p.normalized_number like ? and c.\"_id\" = p.raw_contact_id";
   private static final String CONTACTNAMEQUERY2 = "select c." + DISPLAYNAME + " from raw_contacts c, phone_lookup p where p.min_match like ? and c.\"_id\" = p.raw_contact_id";

   private final File m_ContactsDB;

   private Connection m_Connection;

   /**
    * Creates new {@code SQLLiteContactsReader}.
    *
    * @param contactsDB the contacts DB file.
    */
   public SQLLiteContactsReader(final File contactsDB)
   {
      m_ContactsDB = contactsDB;
      connect();
   }

   /**
    * Retrieve the contact name for a given address.
    *
    * @param strAddress the address to look up.
    *
    * @return the contact name for the given address or {@code null} if
    *         {@code strAddress} was {@code null}, empty or not found.
    */
   public String getContactNameForAddress(final String strAddress)
   {
      String strContactName = null;

      if (null != m_Connection && null != strAddress)
      {
         try (final PreparedStatement statement1 = m_Connection.prepareStatement(CONTACTNAMEQUERY1))
         {
            statement1.setQueryTimeout(30);  // set timeout to 30 sec.
            statement1.setString(1, strAddress);

            try (final ResultSet rs1 = statement1.executeQuery())
            {
               if (!rs1.next())
               {
                  try (final PreparedStatement statement2 = m_Connection.prepareStatement(CONTACTNAMEQUERY2))
                  {
                     statement2.setQueryTimeout(30);  // set timeout to 30 sec.
                     statement2.setString(1, toCallerIDMinMatch(strAddress));

                     try (final ResultSet rs2 = statement2.executeQuery())
                     {
                        if (rs2.next())
                        {
                           strContactName = rs2.getString(DISPLAYNAME);
                           if (rs2.next()) // only if is unique
                              strContactName = null;
                        }
                     }
                  }
               }
               else
                  strContactName = rs1.getString(DISPLAYNAME);
            }
         }
         catch (final SQLException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            strContactName = null;
         }
      }
      else
         strContactName = null;

      return(strContactName);
   }

   private boolean connect()
   {
      boolean fRet;

      try
      {
         Class.forName("org.sqlite.JDBC");
         m_Connection = DriverManager.getConnection("jdbc:sqlite:" + m_ContactsDB.getCanonicalPath());
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

   /**
    * Returns the rightmost 7 characters in the network portion in *reversed*
    * order.
    * @param strAddress
    * @return the reversed and truncated string.
    */
   private static String toCallerIDMinMatch(final String strAddress)
   {
      return(new StringBuilder(strAddress).reverse().substring(0, Math.min(7, strAddress.length())));
   }
}
