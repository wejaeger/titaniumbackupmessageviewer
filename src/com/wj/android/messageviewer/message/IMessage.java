/*
 * $Id$
 *
 * File:   IMessage.java
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
package com.wj.android.messageviewer.message;

import java.util.Date;

/**
 * This Interface represents a Message.
 *
 * @author Werner Jaeger
 */
public interface IMessage extends Comparable<IMessage>
{
   public enum Encoding
   {
      PLAIN ("plain"),
      BAS64 ("base64");

      private final String m_strEncoding;

      private Encoding(final String strEncoding)
      {
         m_strEncoding = strEncoding;
      }

      @Override
      public String toString()
      {
         return(m_strEncoding);
      }

      public static Encoding fromString(final String strEncoding)
      {
         Encoding eRet = null;

         if (null != strEncoding)
         {
            for (Encoding e : Encoding.values())
            {
               if (strEncoding.equalsIgnoreCase(e.m_strEncoding))
               {
                  eRet = e;
                  break;
               }
            }
         }
         return(eRet);
      }
   }

   public enum MessageBox
   {
      SENT ("sent"),
      INBOX ("inbox");

      private final String m_strMsgBox;

      private MessageBox(final String strMsgBox)
      {
         m_strMsgBox = strMsgBox;
      }

      @Override
      public String toString()
      {
         return(m_strMsgBox);
      }

      public static MessageBox fromString(final String strMsgBox)
      {
         MessageBox msgBoxRet = null;

         if (null != strMsgBox)
         {
            for (MessageBox msgBox : MessageBox.values())
            {
               if (strMsgBox.equalsIgnoreCase(msgBox.m_strMsgBox))
               {
                  msgBoxRet = msgBox;
                  break;
               }
            }
         }
         return(msgBoxRet);
      }
   }

   /**
    *  Set the message text.
    *
    * @param strTxt the text to set.
    */
   void setMessageText(final String strTxt);

   /**
    * Get the text of this message.
    *
    * @return the message text.
    */
   String getMessageText();

   /**
    * Set the message box.
    *
    * @param msgBox the message box to set.
    */
   void setMessageBox(final MessageBox msgBox);

   /**
    * Get the Message Box.
    *
    * @return the message box.
    */
   MessageBox getMessageBox();

   /**
    * Set the message date.
    *
    * @param msgDate the date of the message.
    */
   void setMessageDate(final Date msgDate);

   /**
    * Get the date of the message.
    *
    * @return the date.
    */
   Date getMessageDate();

   /**
    * Set the message address.
    *
    * @param strAddress the address of the message.
    */
   void setMessageAddress(final String strAddress);

   /**
    * Get the Address of the message.
    *
    * @return the address.
    */
   String getMessageAddress();
}
