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

import java.io.Serializable;
import java.util.Date;

/**
 * This Interface represents a Message.
 *
 * @author Werner Jaeger
 */
public interface IMessage extends Comparable<IMessage>, Serializable
{
   /** Represent the contents encoding */
   public enum Encoding
   {
      /** No encoding */
      PLAIN ("plain"),
      /** Content is base 64 encoded */
      BAS64 ("base64");

      private final String m_strEncoding;

      private Encoding(final String strEncoding)
      {
         m_strEncoding = strEncoding;
      }

      /**
       * Returns a text representation of this encoding.
       *
       * @return the encoding string e.g. {@code plain} or {@code base64}.
       *         Never {@code null}.
       */
      @Override
      public String toString()
      {
         return(m_strEncoding);
      }

      /**
       * Creates the appropriate encoding instance for the given encoding
       * string.
       *
       * @param strEncoding
       * @return the appropriate encoding instance for the given encoding
       *         string or {@code null} if this if it is not defined in this
       *         enumeration.
       */
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

   /**
    * Represents the type of the messages message box.
    */
   public enum MessageBox
   {
      /** Message was sent. */
      SENT ("sent"),
      /** Message was received. */
      INBOX ("inbox"),
      /** Daft message box */
      DRAFT ("draft");

      private final String m_strMsgBox;

      private MessageBox(final String strMsgBox)
      {
         m_strMsgBox = strMsgBox;
      }

      /**
       * Returns a string representation.
       *
       * @return the message box type string e.g. {@code sent/inbox}.
       */
      @Override
      public String toString()
      {
         return(m_strMsgBox);
      }

      /**
       * Factory method to construct the appropriate enumeration type for this
       * message box type
       *
       * @param strMsgBox
       *
       * @return the appropriate message box  type or {@code null} if no message
       *         box type is defined for the given {@code strMsgBox}.
       */
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
    * Get the properly formatted date of the message.
    *
    * @return the formatted message date.
    */
   String getFormattedMessageDate();

   /**
    * Set the message address.
    *
    * @param strAddress the address of the message.
    */
   void setMessageAddress(final String strAddress);

   /**
    * Set the service center this message came from.
    *
    * @param strServiceCenter the service center or {@code null} or empty if
    *        not known.
    */
   void setServiceCenter(final String strServiceCenter);

   /**
    * Get the Address of the message.
    *
    * @return the address.
    */
   String getMessageAddress();

   /**
    * Get the service center this message came from.
    *
    * @return the service center or {@code null} or empty  if not known.
    */
   String getServiceCenter();
}
