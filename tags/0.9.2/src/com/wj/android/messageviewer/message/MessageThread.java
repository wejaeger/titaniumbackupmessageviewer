/*
 * $Id$
 *
 * File:   MessageThread.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents threaded messaging.
 *
 * <p>
 *    A message thread groups messages to the same person  and phone number
 *    together.
 * </p>
 *
 * @author Werner Jaeger
 */
public class MessageThread
{
   final private String m_strName;
   final private String m_strAddress;
   final private ArrayList<IMessage> m_MessageList;

   /**
    * Creates new {@code MessageThread}.
    *
    * @param strName the name of the contact or {@code null}.
    * @param strAddress the address (mostly phone number) of the contact.
    *        Must not be {@code null}.
    */
   public MessageThread(final String strName, final String strAddress)
   {
      m_strName = strName;
      m_strAddress = strAddress;
      m_MessageList = new ArrayList<>();
   }

   /**
    * Get the contact name.
    *
    * @return the name of the contact or {@code null} if name is not known.
    */
   public String getName()
   {
      return(m_strName);
   }

   /**
    * Get the address of the contact.
    *
    * @return the address (e.g. phone number). Never {@code null}.
    */
   public String getAddress()
   {
      return(m_strAddress);
   }

   /**
    * Get all the messages of this thread.
    *
    * @return all messages of this thread. Never {@code null}.
    */
   public Collection<IMessage> getMessages()
   {
      return(Collections.unmodifiableCollection(m_MessageList));
   }

   /**
    * Add a message to this thread.
    *
    * @param msgToAdd the message to add. Must not be {@code null}.
    */
   public void addMessage(final IMessage msgToAdd)
   {
      m_MessageList.add(msgToAdd);
   }

   /**
    * Returns a string representation.
    *
    * @return the name (if not {@code null}) concatenated with a blank, hyphen,
    *          blank and then the address. Never {@code null}.
    */
   @Override
   public String toString()
   {
      final String strRet;

      if (null == m_strName || m_strName.isEmpty())
         strRet = m_strAddress;
      else
         strRet  = m_strName + (!m_strAddress.equals("") ? " - " + m_strAddress : "");

      return(strRet);
   }
}