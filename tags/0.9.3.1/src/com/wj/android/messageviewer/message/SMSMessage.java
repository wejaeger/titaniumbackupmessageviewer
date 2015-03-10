/*
 * $Id$
 *
 * File:   SMSMessage.java
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

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * The {@code SMSMessage} class represents a Short Message without a
 * presentation definition.
 *
 * <p>
 *    A device who receives this {@code SMSMessage} will decide how the
 *    message text is displayed.
 * </p>
 *
 * @author Werner Jaeger
 */
public class SMSMessage implements IMessage
{
   private static final long serialVersionUID = -3092043642357166011L;

   private static final class AddressInfo
   {
      /**
       * Constructs a {@code AddressInfo} object.
       *
       * Prevent instantiation of this class.
       */
      private AddressInfo()
      {
      }

      private static TimeZone toTimeZone(final String strAddress)
      {
         TimeZone tz = TimeZone.getDefault();

         if (null != strAddress)
         {
            if (strAddress.startsWith("+63"))
               tz = TimeZone.getTimeZone("Asia/Manila");
         }

         return(tz);
      }
   }

   private String m_strMessageText;
   private MessageBox m_msgBox;
   private Date m_MessageDate;
   private String m_strMessageAddress;
   private String m_strServiceCenter;

   /**
    * Creates new {@code SMSMessage}.
    */
   public SMSMessage()
   {
      m_msgBox = null;
      m_MessageDate = new Date();
      m_strMessageText = "";
      m_strMessageAddress = "";
      m_strServiceCenter = null;
    }

   /**
    * Creates new {@code SMSMessage}.
    *
    * @param strServiceCenter
    * @param strAddress the message address.
    * @param date the message date.
    * @param strBody the message text.
    * @param msgBox the message box of the message.
    */
   public SMSMessage(final String strServiceCenter, final String strAddress, final Date date, final String strBody, final MessageBox msgBox)
   {
      m_strMessageAddress = strAddress;
      m_strServiceCenter = strServiceCenter;
      m_MessageDate = date != null ? new Date(date.getTime()) : null;
      m_strMessageText = strBody;
      m_msgBox = msgBox;
   }

   @Override
   public void setMessageText(final String strTxt)
   {
      m_strMessageText = strTxt;
   }

   @Override
   public String getMessageText()
   {
      return(m_strMessageText);
   }

   @Override
   final public void setMessageBox(final MessageBox msgBox)
   {
      m_msgBox = msgBox;
   }

   @Override
   final public MessageBox getMessageBox()
   {
      return(m_msgBox);
   }

   @Override
   final public void setMessageDate(final Date msgDate)
   {
      m_MessageDate = msgDate != null ? new Date(msgDate.getTime()) : null;
   }

   @Override
   final public Date getMessageDate()
   {
      return(new Date(m_MessageDate.getTime()));
   }

   @Override
   public String getFormattedMessageDate()
   {
      final TimeZone tz = AddressInfo.toTimeZone(m_strServiceCenter == null || m_strServiceCenter.trim().isEmpty() ? m_strMessageAddress : m_strServiceCenter);

      if (null != tz)
         DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).setTimeZone(tz);
      else
         DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).setTimeZone(TimeZone.getDefault());

      return(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG).format(m_MessageDate));
   }

   @Override
   public void setMessageAddress(final String strAddress)
   {
      m_strMessageAddress = strAddress;
   }

   @Override
   final public String getMessageAddress()
   {
      return(m_strMessageAddress);
   }

   @Override
   final public void setServiceCenter(final String strServiceCenter)
   {
      m_strServiceCenter = strServiceCenter;
   }

   @Override
   final public String getServiceCenter()
   {
      return(m_strServiceCenter);
   }

   /**
    * A string representation.
    *
    * @return the message box concatenated with a colon, blank, message date,
    *         colon, blank and the message text. Never {@code null}.
    */
   @Override
   public String toString()
   {
      return(getMessageBox() + ": " + getFormattedMessageDate() + ":  " + getMessageText());
   }

   /**
    * Compares this message date with the specified message date.
    *
    * @param msg the message to be compared. Must not be {@code null}.
    *
    * @return a negative integer, zero, or a positive integer as this message
    *          date is less than, equal to, or greater than the specified
    *          message date.
    */
   @Override
   public int compareTo(final IMessage msg)
   {
      return(m_MessageDate.compareTo(msg.getMessageDate()));
   }

   /**
    * Indicates whether some other object is "equal to" this one.
    *
    * @param o the reference object with which to compare. May be {@code null}.
    *
    * @return {@code true} if this message date, address, text, service center
    *         and message box are is the same as the {@code obj} date, address,
    *         text, service center and message box; {@code false} otherwise.
    */
   @Override
   public boolean equals(final Object o)
   {
      boolean fRet = false;

      if (this != o)
      {
         if (o != null && getClass() == o.getClass())
         {
            final SMSMessage msg = (SMSMessage)o;

            if (m_MessageDate != null ? m_MessageDate.equals(msg.m_MessageDate) : msg.m_MessageDate == null)
            {
               if (m_strMessageAddress != null ? m_strMessageAddress.equals(msg.m_strMessageAddress) : msg.m_strMessageAddress == null)
               {
                  if (m_strMessageText != null ? m_strMessageText.equals(msg.m_strMessageText) : msg.m_strMessageText == null)
                  {
                     if (m_msgBox != null ? m_msgBox.equals(msg.m_msgBox) : msg.m_msgBox == null)
                     {
                        if (m_strServiceCenter != null ? m_strServiceCenter.equals(msg.m_strServiceCenter) : msg.m_strServiceCenter == null)
                           fRet = true;
                     }
                  }
               }
            }
         }
      }
      else
         fRet = true;

      return(fRet);
   }

   /**
    * Returns a hash code value for the object.
    *
    * @return a hash code value for this object.
    */
   @Override
   public int hashCode()
   {
      int iHash = 5;

      iHash = 73 * iHash + Objects.hashCode(m_MessageDate) + Objects.hashCode(m_strMessageAddress) + Objects.hashCode(m_strMessageText) + Objects.hashCode(m_msgBox) + Objects.hashCode(m_strServiceCenter);

      return(iHash);
   }
}