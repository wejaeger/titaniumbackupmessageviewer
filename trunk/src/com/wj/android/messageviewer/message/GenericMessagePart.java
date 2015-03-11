/*
 * $Id$
 *
 * File:   GenericMessagePart.java
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

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Simple implementation of the {@link IMMSMessagePart} interface.
 *
 * <p>
 *    Instances of this class can be added to a {@link MMSMessage}.
 * </p>
 *
 * @author Werner Jaeger
 */
public abstract class GenericMessagePart implements IMMSMessagePart
{
   private static final long serialVersionUID = 5929878763930779397L;

   private final ContentType m_ContentType;
   private final String m_StrContentId;
   private final String m_StrContentLocation;
   private final String m_strCharset;

   /** Contains the content bytes. */
   protected final byte[] m_abContent;

   /**
    * Creates new {@code GenericMessagePart}.
    *
    * @param contentType the type of the content (e.g. "text/plain").
    *        Must not be {@code null}.
    * @param strContentId the content identifier. Must not be {@code null}.
    * @param strContentLocation the content location . Must not be {@code null}.
    * @param abContent the content . Must not be {@code null}.
    * @param strCharset the character set of text content.
    *        Must not be {@code null}.
    */
   protected GenericMessagePart(final ContentType contentType, final String strContentId, final String strContentLocation, final byte[] abContent, final String strCharset)
   {
      m_ContentType = contentType;
      m_StrContentId = strContentId.replaceAll("<|>", "");
      m_StrContentLocation = strContentLocation;
      m_abContent = Arrays.copyOf(abContent, abContent.length);
      m_strCharset = strCharset;
   }

   /** {@inheritDoc} */
   @Override
   public final String getContentLocation()
   {
      return(m_StrContentLocation);
   }

   /** {@inheritDoc} */
   @Override
   public final String getContentId()
   {
      return(m_StrContentId);
   }

   /** {@inheritDoc} */
   @Override
   public final ContentType getContentType()
   {
      return(m_ContentType);
   }

   /** {@inheritDoc} */
   @Override
   public String getCharSet()
   {
      return(m_strCharset != null ? m_strCharset : Charset.defaultCharset().name());
   }
}
