/*
 * $Id$
 *
 * File:   MMSMessage.java
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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The {@code MMSMessage} class represents a Multimedia Message without a
 * presentation definition.
 *
 * <p>
 *    A device who receives this Message will decide how and in which order
 *    the {@code MMSMessagePart} are displayed. Use the subclass
 *    {@link SMILMessage} if you want to define the presentation with SMIL.
 * </p>
 *
 * @author Werner Jaeger
 */
public class MMSMessage extends SMSMessage
{
   private final List<IMMSMessagePart> m_Parts;

   private String m_strSubject;
   private String m_strMessageText;

   /**
    * Creates new {@code MMSMessage} with the given parts.
    *
    * @param parts the parts of this message.
    * @param strAddress the message address.
    * @param strServiceCenter the service center the message came from or
    *        {@code null} if not known
    * @param date the message date.
    * @param msgBox the message box of the message.
    */
   public MMSMessage(final List<IMMSMessagePart> parts, final String strServiceCenter, final String strAddress, final Date date, final MessageBox msgBox)
   {
      super(strServiceCenter, strAddress, date, null, msgBox);

      m_Parts = parts;
      m_strMessageText = null;
   }

   /**
    * Get the message subject.
    *
    * @return the subject.
    */
   public String getSubject()
   {
      return(m_strSubject);
   }

   /**
    * Set the subject of this message.
    *
    * @param strSubject the subject to set.
    */
   public void setSubject(final String strSubject)
   {
      m_strSubject = strSubject;
   }

   /**
    * Add a {@link IMMSMessagePart message part} to this message
    *
    * @param part the message part to be added.
    */
   public void addMessagePart(final IMMSMessagePart part)
   {
      m_Parts.add(part);
   }

   /**
    * Retrieve all message parts.
    *
    * @return the list om message parts. Never {@code null}
    */
   public Collection<IMMSMessagePart> getMessageParts()
   {
      return(Collections.unmodifiableList(m_Parts));
   }

   /**
    * Gets the text of this message.
    *
    * <p>
    *    If a not {@code null} text is explicitly set via
    *    {@link MMSMessage#setMessageText(java.lang.String)} this text is
    *    returned, otherwise it concatenates the subject plus texts of all
    *    parts, separating each part text with two newlines.
    * </p>
    *
    * @return the concatenated texts of subject and all parts.
    *         Never {@code null}.
    */
   @Override
   public String getMessageText()
   {
      final StringBuffer strRet;

      if (null == m_strMessageText)
      {
         strRet = new StringBuffer(getSubject() != null && !getSubject().trim().isEmpty() ? getSubject() + "\n\n" : "");

         for (IMMSMessagePart part : getMessageParts())
         {
            strRet.append(part.getText());
            strRet.append("\n\n");
         }
      }
      else
         strRet = new StringBuffer(m_strMessageText);

      return(strRet.toString());
   }

   /**
    * Sets the text for this message.
    *
    * @param strText the text to set.
    */
   @Override
   public void setMessageText(final String strText)
   {
      m_strMessageText = strText;
   }
}
