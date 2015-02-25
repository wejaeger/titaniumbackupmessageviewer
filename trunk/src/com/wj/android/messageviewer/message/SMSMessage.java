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

import java.util.Date;

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
   private String m_strMessageText;
   private MessageBox m_msgBox;
   private Date m_MessageDate;
   private String m_strMessageAddress;

   /**
    * Creates new {@code SMSMessage}.
    */
   public SMSMessage()
   {
      m_msgBox = null;
      m_MessageDate = new Date();
      m_strMessageText = "";
      m_strMessageAddress = "";
   }

   /**
    * Creates new {@code SMSMessage}.
    *
    * @param strAddress the message address.
    * @param date the message date.
    * @param strBody the message text.
    * @param msgBox the message box of the message.
    * @param iTimeOffset the time offset.
    */
   public SMSMessage(final String strAddress, final Date date, final String strBody, final MessageBox msgBox, final int iTimeOffset)
   {
      m_strMessageAddress = strAddress;
      m_MessageDate = new Date(date.getTime() + iTimeOffset); // date.add(offset);
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
      m_MessageDate = msgDate;
   }

   @Override
   final public Date getMessageDate()
   {
      return(m_MessageDate);
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
   public String toString()
   {
      return (getMessageBox() + ": " + getMessageDate().toString() + ":  " + getMessageText());
   }

   @Override
   public int compareTo(final IMessage msg)
   {
      return(getMessageDate().compareTo(msg.getMessageDate()));
   }

}