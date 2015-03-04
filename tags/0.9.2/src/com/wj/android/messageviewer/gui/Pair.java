/*
 * $Id$
 *
 * File:   Pair.java
 * Author: Werner Jaeger
 *
 * Created on Mar 1, 2015, 12:25:42 PM
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

import java.io.Serializable;

/**
 * A class to hold two generic data together.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
class Pair<First extends Serializable, Second extends Serializable> implements Serializable
{
   private static final long serialVersionUID = -8524273621472431244L;

   private First m_First;
   private Second m_Second;

   /**
    * Constructs a new {@code Pair}.
    *
    * @param first the first data item. May be {@code null}.
    * @param second the second data item. May be {@code null}.
    */
   public Pair(final First first, final Second second)
   {
      m_First = first;
      m_Second = second;
   }

   /**
    * Copy constructs for a {@code Pair}.
    *
    * @param first the pair to make a copy from. May not be {@code null}.
    */
   public Pair(final Pair<First, Second> pair)
   {
      m_First = pair.getFirst();
      m_Second = pair.getSecond();
   }

   /**
    * Set the first data item.
    *
    * @param first the data to set. May be {@code null}.
    */
   public void setFirst(final First first)
   {
      m_First = first;
   }

   /**
    * Set the second data item.
    *
    * @param second the data to set. May be {@code null}.
    */
   public void setSecond(final Second second)
   {
      m_Second = second;
   }

   /**
    * Gets the first data item.
    *
    * @return the first data item or {@code null}.
    */
   public First getFirst()
   {
      return(m_First);
   }

   /**
    * Gets the second data item.
    *
    * @return the second data item or {@code null}.
    */
   public Second getSecond()
   {
      return(m_Second);
   }

    /**
    * Set the first and the second data item.
    *
    * @param first the first data to set. May be {@code null}.
    * @param second the second data to set. May be {@code null}.
    */
  public void set(final First first, final Second second)
   {
      setFirst(first);
      setSecond(second);
   }

  /** {@inheritDoc */
   @Override
   public boolean equals(final Object o)
   {
      boolean fRet = false;

      if (this != o)
      {
         if (o != null && getClass() == o.getClass())
         {
            final Pair pair = (Pair)o;

            if (m_First != null ? m_First.equals(pair.m_First) : pair.m_First == null)
            {
               if (m_Second != null ? m_Second.equals(pair.m_Second) : pair.m_Second == null)
                  fRet = true;
            }
         }
      }
      else
         fRet = true;

      return(fRet);
   }

  /** {@inheritDoc */
   @Override
   public int hashCode()
   {
      int iResult = m_First != null ? m_First.hashCode() : 0;

      iResult = 31 * iResult + (m_Second != null ? m_Second.hashCode() : 0);

      return(iResult);
   }
}
