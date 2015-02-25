/*
 * $Id$
 *
 * File:   VCrdPart.java
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

/**
 * This class represents a VCard part.
 *
 * @author Werner Jaeger
 */
public class VCardPart extends GenericMessagePart
{
   private final String m_strText;

   /**
    * Creates new {@code VCardPart}.
    *
    * @param contentType the type of the content (e.g. "text/x-vCard")
    * @param strContentId the content identifier. Must not be {@code null}.
    * @param strContentLocation the content location
    * @param abContent the content
    * @param strCharset the character set of text content
    *
    * @throws UnsupportedEncodingException if encoding of {@code content} to
    *         {@link IMMSMessagePart#getCharSet() getCharset()} fails.
    */
   protected VCardPart(final ContentType contentType, final String strContentId, final String strContentLocation, final byte[] abContent, final String strCharset) throws UnsupportedEncodingException
   {
      super(contentType, strContentId, strContentLocation, abContent, strCharset);

      m_strText = new String(abContent, getCharSet());
   }

   /**
    * Gets the content as text.
    *
    * @return the content as text.
    */
   @Override
   public String getText()
   {
      return(m_strText);
   }
}
