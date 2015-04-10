/*
 * $Id$
 *
 * File:   HtmlEditorKit.java
 * Author: Werner Jaeger
 *
 * Created on Apr 9, 2015, 1:34:50 PM
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

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

/**
 * {@code EditorKit} plug-in to render images stored as resources in a
 * {@code onjar} bundled application.
 *
 * <p>
 *    Overrides {@link #getViewFactory()} to return a view factory that returns
 *    an instance of class {@link OneJarImageView} to  handle {@code URL}s with
 *    {@code onjar} syntax, that is {@code URL}s starting with
 *    {@code jar:file:}.
 * </p>
 *
 * @author Werner Jaeger
 */
class HtmlEditorKit extends HTMLEditorKit
{
   /**
    * A factory to build views for HTML.
    *
    * @author Werner Jaeger
    */
   static class HtmlFactory extends HTMLEditorKit.HTMLFactory
   {
      /**
       * Creates a view from an element.
       *
       * <p>
       *    If {@code elem} equals {@code HTML.Tag.IMG} then an instance of
       *    class {@link OneJarImageView} is returned, otherwise super class
       *    implementation is called.
       * </p>
       *
       * @param elem the element
       * @return the view
       */
      @Override
      public View create(final Element elem)
      {
         final View view;

         final Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
         if (o instanceof HTML.Tag)
         {
            final HTML.Tag kind = (HTML.Tag)o;
            if (kind == HTML.Tag.IMG)
               view = new OneJarImageView(elem);
            else
               view = super.create(elem);
         }
         else
            view = super.create(elem);

         return(view);
      }
   }

   private static final long serialVersionUID = -492401333767652506L;

   /** Shared factory for creating HTML Views. */
   private static final ViewFactory HTMLFACTORY = new HtmlFactory();

   /**
    * Fetch a factory that is suitable for producing views of any models that
    * are produced by this kit.
    *
    * <p>
    *    Overridden to return a view factory that returns an instance of class
    *    {@link OneJarImageView} to  handle {@code URL}s with {@code onjar}
    *    syntax, that is {@code URL}s starting with {@code jar:file:}.
    * </p>
    *
    * @return the factory
    */
   @Override
   public ViewFactory getViewFactory()
   {
      return(HTMLFACTORY);
   }
}
