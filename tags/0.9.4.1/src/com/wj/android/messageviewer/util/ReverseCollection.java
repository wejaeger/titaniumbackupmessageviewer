/*
 * $Id$
 *
 * File:   ReverseCollection.java
 * Author: Werner Jaeger
 *
 * Created on Feb 28, 2015, 10:52:02 PM
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
package com.wj.android.messageviewer.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Provides an unmodifiable collection that returns the elements in the reverse
 * order of iteration.
 *
 * <p>
 * If the original collection implements the RandomAccess interface, we point to
 * that, otherwise we wrap it with an {@code ArrayList}.
 * </p>
 *
 * @param <E> the type of element maintained by this collection
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
class ReverseCollection<E> extends AbstractCollection<E>
{
   private final List<E> m_Elements;

   /**
    * Constructs a new {@code ReverseCollection}; from a given collection.
    *
    * @param original the original collection. Must not be {@code null}.
    */
   public ReverseCollection(final Collection<E> original)
   {
      if (original instanceof RandomAccess)
         m_Elements = (List<E>)original;
      else
         m_Elements = new ArrayList<>(original);
   }

   /** {@inheritDoc  */
   @Override
   public Iterator<E> iterator()
   {
      return new Iterator<E>()
      {
         private int m_iIndex = m_Elements.size();

         @Override
         public boolean hasNext()
         {
            return(m_iIndex > 0);
         }

         @Override
         public E next()
         {
            if (!hasNext())
               throw new NoSuchElementException();

            return((E)m_Elements.get(--m_iIndex));
         }

         @Override
         public void remove()
         {
            throw new UnsupportedOperationException();
         }
      };
   }

   /** {@inheritDoc  */
   @Override
   public int size()
   {
      return(m_Elements.size());
   }
}
