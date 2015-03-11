/*
 * $Id$
 *
 * File:   IMMSMessagePart.java
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
import java.io.UnsupportedEncodingException;

/**
 * This Interface represents a content part of a {@code MMS message}.
 *
 * A {@link MMSMessage} may contain one or multiple content parts.
 *
 * @author Werner Jaeger
 */
public interface IMMSMessagePart extends Serializable
{
   /**
    * Represents the content type of a message part.
    */
   public enum ContentType
   {
      /** A JPEG image */
      IMAMGEJPEG ("image/jpeg"),
     /** A PNG image */
      IMAMGEPNG ("image/png"),
     /** A GIF image */
      IMAMGEGIF ("image/gif"),
      /** Plain text */
      TEXTPLAIN ("text/plain"),
      /** SMIL document */
      APPLICATIONSMIL ("application/smil"),
      /** A vCard */
      TEXTVCARD ("text/x-vCard");

      private final String m_strContentType;

      private ContentType(final String strContentType)
      {
         m_strContentType = strContentType;
      }

      /**
       * Returns a string representation.
       *
       * @return the content type string e.g. {@code text/plain}.
       */
      @Override
      public String toString()
      {
         return(m_strContentType);
      }

      /**
       * Factory method to construct the appropriate message part for this
       * content type.
       *
       * @param contentType the type of the content (e.g. "text/plain")
       * @param strContentId the content identifier. Must not be {@code null}.
       * @param strContentLocation the content location
       * @param abContent the content
       * @param strCharset the character set of text content
       *
       * @return a newly created instance of {@link IMMSMessagePart}.
       *         or {@code null} if no appropriate implementation exists.
       *
       * @throws UnsupportedEncodingException if encoding of text content to
       *         {@link IMMSMessagePart#getCharSet() getCharset()} fails.
       */
      public final IMMSMessagePart newMessagePart(final ContentType contentType, final String strContentId, final String strContentLocation, final byte[] abContent, final String strCharset) throws UnsupportedEncodingException
      {
         final IMMSMessagePart mmsMessagePart;

         switch(this)
         {
            case IMAMGEJPEG:
            case IMAMGEPNG:
            case IMAMGEGIF:
               mmsMessagePart = new ImagePart(contentType, strContentId, strContentLocation, abContent, strCharset);
               break;

            case TEXTPLAIN:
               mmsMessagePart = new TextPart(contentType, strContentId, strContentLocation, abContent, strCharset);
               break;

            case APPLICATIONSMIL:
               mmsMessagePart = new SMILPart(contentType, strContentId, strContentLocation, abContent, strCharset);
               break;

            case TEXTVCARD:
               mmsMessagePart = new VCardPart(contentType, strContentId, strContentLocation, abContent, strCharset);
               break;

            default:
               mmsMessagePart = null;
               break;
         }

         return(mmsMessagePart);
      }

      /**
       * Factory method to construct the appropriate enumeration type for this
       * content type
       *
       * @param strContentType the content type string
       *
       * @return the appropriate content type or {@code null} if no content
       *         type is defined for the given {@code strContentType}.
       */
      public static ContentType fromString(final String strContentType)
      {
         ContentType contentTypeRet = null;

         if (null != strContentType)
         {
            for (ContentType contentType : ContentType.values())
            {
               if (strContentType.equalsIgnoreCase(contentType.m_strContentType))
               {
                  contentTypeRet = contentType;
                  break;
               }
            }
         }
         return(contentTypeRet);
      }
   }

   /**
    * Get the content location of this message part.
    *
    * @return the location of the content.
    */
   String getContentLocation();

   /**
    * Get the content identifier of this message part.
    *
    * @return the identifier of the content.
    */
   String getContentId();

   /**
    * Get the content-type of this message part.
    *
    * @return the content-type.
    */
   ContentType getContentType();

   /**
    * Get the text of the message part.
    *
    * <p>
    *    For media type parts like image the content location is returned.
    * </p>
    *
    * @return the text.
    */
   String getText();

   /**
    * Get the character set of the content.
    *
    * <p>
    *    The character set is ignored for binary content.
    * </p>
    *
    * @return the character set. Never {@code null}
    */
   String getCharSet();
}
