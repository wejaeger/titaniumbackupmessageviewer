/*
 * $Id$
 *
 * File:   ImagePart.java
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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * This class represents a "image" part.
 *
 * @author Werner Jaeger
 */
public class ImagePart extends GenericMessagePart
{
   private Image m_Image;

   /**
    * Creates new {@code ImagePart}.
    *
    * @param contentType the type of the content (e.g. "text/plain")
    * @param strContentId the content identifier. Must not be {@code null}.
    * @param strContentLocation the content location
    * @param abContent the content
    * @param strCharset the character set of text content
    */
   protected ImagePart(final ContentType contentType, final String strContentId, final String strContentLocation, final byte[] abContent, final String strCharset)
   {
      super(contentType, strContentId, strContentLocation, abContent, strCharset);

      m_Image = null;
   }

   /**
    * Get the image of this part.
    *
    * @return the image or {@code null} if image failed to read.
    */
   public Image getImage()
   {
      if (null == m_Image)
         m_Image = byteArrayToImage(getContent());

      return(m_Image);
   }

   /**
    * Gets the image location.
    *
    * @return the image location.
    */
   @Override
   public String getText()
   {
      return(getContentLocation());
   }

   private static BufferedImage byteArrayToImage(byte[] abImage)
   {
      BufferedImage image;

      try
      {
         final InputStream inputStream = new ByteArrayInputStream(abImage);
         image = ImageIO.read(inputStream);
      }
      catch (IOException ex)
      {
         image = null;
      }

      return(image);
   }
}
