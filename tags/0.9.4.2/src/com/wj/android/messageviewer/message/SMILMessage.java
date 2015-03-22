/*
 * $Id$
 *
 * File:   SMILMessage.java
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code SMILMessage} is a {@link MMSMessage} extended by a SMIL document
 * to organize the presentation.
 *
 * <p>
 *    (SMIL = Synchronized Multimedia Integration Language) The concept of the
 *    {@code SMILMessage} means the ordering, layout, sequencing and timing of
 *    {@link IMMSMessagePart} on the terminal screen.
 * </p>
 *
 * <p>
 *     The {@link IMMSMessagePart} of the {@code SMILMessage} must be referenced
 *     by the SMIL document, otherwise they might be ignored at the target
 *     terminal.
 * </p>
 *
 * @author Werner Jaeger
 */
public class SMILMessage extends MMSMessage
{
   private static final long serialVersionUID = -9168554817199468278L;
   private static final Logger LOGGER = Logger.getLogger(ImagePart.class.getName());
   private static final String CIDPREFIX = "cid:";

   private final Map<String, IMMSMessagePart> m_PartsMap;
   private final SMILPart m_SMILPart;
   private final Collection<IMMSMessagePart> m_OrderedParts;

   /**
    * Creates new {@code SMILMessage}.
    *
    * @param parts the parts of this message.
    * @param strAddress the message address.
    * @param strServiceCenter the service center the message came from or
    *        {@code null} if not known
    * @param date the message date.
    * @param msgBox the message box of the message.
    */
   public SMILMessage(final List<IMMSMessagePart> parts, final String strServiceCenter, final String strAddress, final Date date, final IMessage.MessageBox msgBox)
   {
      super(parts, strServiceCenter, strAddress, date, msgBox);

      m_OrderedParts = new ArrayList<>();
      m_PartsMap = new HashMap<>(parts.size());

      SMILPart smilPart = null;

      for (final IMMSMessagePart part : parts)
      {
         if (part.getContentType() != IMMSMessagePart.ContentType.APPLICATIONSMIL)
         {
            m_PartsMap.put(CIDPREFIX + part.getContentId(), part);
            m_PartsMap.put(part.getContentLocation(), part);
         }
         else
            smilPart = (SMILPart)part;
      }

      m_SMILPart = smilPart;
   }

   /**
    * Retrieve all message parts in the order defined by SMIL document.
    *
    * @return the list of message ordered parts. Never {@code null}
    */
   @Override
   public Collection<IMMSMessagePart> getMessageParts()
   {

      if (m_OrderedParts.isEmpty()) // already read ?
      {
         final Collection<String> aContentReferences = m_SMILPart.contentReferences();

         if (aContentReferences.isEmpty())
         {
            // SMIL document does not reference any part, so show user at least
            // the content type of all existing but not referenced parts.
            final Collection<IMMSMessagePart> parts = super.getMessageParts();

            for (final IMMSMessagePart part : parts)
            {
               if (part.getContentType() != IMMSMessagePart.ContentType.APPLICATIONSMIL)
               {
                  try
                  {
                     final String strCharSet = part.getCharSet();
                     final String strContentId = part.getContentId();
                     final String strContentLocation = part.getContentLocation();
                     final byte[] abContent = strContentId.getBytes(strCharSet);
                     final IMMSMessagePart.ContentType contenType = part.getContentType();
                     final IMMSMessagePart newPart = contenType.newMessagePart(contenType, strContentId, strContentLocation, abContent, strCharSet);
                     m_OrderedParts.add(newPart);
                  }
                  catch (final UnsupportedEncodingException ex)
                  {
                     LOGGER.log(Level.SEVERE, ex.toString(), ex);
                     m_OrderedParts.add(part);
                  }
               }
            }
         }
         else
         {
            for (final String strContenReference : aContentReferences)
            {
               final IMMSMessagePart part = m_PartsMap.get(strContenReference);
               if (null != part)
                  m_OrderedParts.add(part);
            }
         }
      }
      return(Collections.unmodifiableCollection(m_OrderedParts));
   }

   /**
    * Indicates whether some other object is "equal to" this one.
    *
    * @param o the reference object with which to compare. May be {@code null}.
    *
    * @return {@code true} if this message date  is the same as the {@code obj}
    *          date; {@code false} otherwise.
    */
   @Override
   public boolean equals(final Object o)
   {
      return(super.equals(o));
   }

   /**
    * Returns a hash code value for the object.
    *
    * @return a hash code value for this object.
    */
   @Override
   public int hashCode()
   {
      return(super.hashCode());
   }
}
