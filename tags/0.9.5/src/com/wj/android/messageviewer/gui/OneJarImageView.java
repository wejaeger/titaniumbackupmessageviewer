/*
 * $Id$
 *
 * File:   OneJarImageView.java
 * Author: Werner Jaeger
 *
 * Created on Apr 9, 2015, 3:15:48 PM
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
package com.wj.android.messageviewer.gui;

import com.wj.android.messageviewer.resources.Resources;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.ImageView;

/**
 * View of an Image, intended to support the HTML &lt;IMG&gt; tag.
 *
 * <p>
 *    Overrides {@link #getImageURL()} to handle {@code URL}s with
 *    {@code onjar} syntax, that is {@code URL}s starting with
 *    {@code jar:file:}.
 * </p>
 *
 * @author Werner Jaeger
 */
class OneJarImageView extends ImageView
{
   /**
    * Creates a new view that represents an IMG element.
    *
    * @param elem the element to create a view for
    */
   public OneJarImageView(final Element elem)
   {
      super(elem);
   }

   /**
    * Return a URL for the image source, or {@code null} if it could not
    * be determined.
    *
    * <p>
    *    Overridden to handle {@code URL}s with {@code onjar} syntax, that
    *    is {@code URL}s starting with {@code jar:file:}.
    * </p>
    */
   @Override
   public URL getImageURL()
   {
      URL url = null;

      final String strSrc = (String)getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
      if (null != strSrc)
      {
         if (!strSrc.startsWith("jar:file:"))
         {
            // no onejar syntax, default behavior
            final URL reference = ((HTMLDocument)getDocument()).getBase();

            try
            {
               url = new URL(reference, strSrc);
            }
            catch (final MalformedURLException ex)
            {
               url = null;
            }
         }
         else
            url = Resources.class.getResource(strSrc.substring(strSrc.lastIndexOf('!') + 1));
      }

      return(url);
   }
}

