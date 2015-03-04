/*
 * $Id$
 *
 * File:   ConcurrentList.java
 * Author: Werner Jaeger
 *
 * Created on Mar 3, 2015, 11:32:25 AM
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A concurrent list implementation.
 *
 * <p>
 *    Implemented simply by maintaining object level {@code ReadWriteLock}
 *    instance, and gaining the read lock in the read operations and gaining
 *    the write lock in the write operations
 * </>
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
class ConcurrentList<T> implements List< T>, Serializable
{
   private static final long serialVersionUID = -7731035618650790744L;
   private final ReadWriteLock m_ReadWriteLock = new ReentrantReadWriteLock();
   private final List< T> m_List;

   /**
    * Constructs a new {@code ConcurrentList}.
    *
    * @param list the list to make thread safe. Must not be {@code null}.
    */
   public ConcurrentList(final List<T> list)
   {
      m_List = list;
   }

   @Override
   public boolean remove(final Object o)
   {
      m_ReadWriteLock.writeLock().lock();
      boolean iRet;
      try
      {
         iRet = m_List.remove(o);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(iRet);
   }

   @Override
   public boolean add(final T t)
   {
      m_ReadWriteLock.writeLock().lock();
      boolean iRet;
      try
      {
         iRet = m_List.add(t);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(iRet);
   }

   @Override
   public void clear()
   {
      m_ReadWriteLock.writeLock().lock();
      try
      {
         m_List.clear();
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
   }

   @Override
   public int size()
   {
      m_ReadWriteLock.readLock().lock();
      try
      {
         return(m_List.size());
      }
      finally
      {
         m_ReadWriteLock.readLock().unlock();
      }
   }

   @Override
   public boolean contains(final Object o)
   {
      m_ReadWriteLock.readLock().lock();
      try
      {
         return(m_List.contains(o));
      }
      finally
      {
         m_ReadWriteLock.readLock().unlock();
      }
   }

   @Override
   public T get(final int iIndex)
   {
      m_ReadWriteLock.readLock().lock();
      try
      {
         return(m_List.get(iIndex));
      }
      finally
      {
         m_ReadWriteLock.readLock().unlock();
      }
   }

   @Override
   public boolean isEmpty()
   {
      m_ReadWriteLock.readLock().lock();
      try
      {
         return(m_List.isEmpty());
      }
      finally
      {
         m_ReadWriteLock.readLock().unlock();
      }
   }

   @Override
   public Iterator<T> iterator()
   {
      m_ReadWriteLock.readLock().lock();
      try
      {
          return(new ArrayList<>(m_List).iterator());
      }
      finally
      {
          m_ReadWriteLock.readLock().unlock();
      }
   }

   @Override
   public Object[] toArray()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public <T> T[] toArray(final T[] a)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean containsAll(final Collection<?> c)
   {
      m_ReadWriteLock.readLock().lock();
      try
      {
          return(m_List.containsAll(c));
      }
      finally
      {
          m_ReadWriteLock.readLock().unlock();
      }
   }

   @Override
   public boolean addAll(final Collection<? extends T> c)
   {
      m_ReadWriteLock.writeLock().lock();
      boolean iRet;
      try
      {
         iRet = m_List.addAll(c);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(iRet);
   }

   @Override
   public boolean addAll(final int arg0, final Collection<? extends T> arg1)
   {
      m_ReadWriteLock.writeLock().lock();
      boolean iRet;
      try
      {
         iRet = m_List.addAll(arg0, arg1);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(iRet);
   }

   @Override
   public boolean removeAll(final Collection<?> c)
   {
      m_ReadWriteLock.writeLock().lock();
      boolean iRet;
      try
      {
         iRet = m_List.removeAll(c);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(iRet);
   }

   @Override
   public boolean retainAll(final Collection<?> c)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public T set(final int arg0, final T arg1)
   {
      m_ReadWriteLock.writeLock().lock();
      T ret;
      try
      {
         ret = m_List.set(arg0, arg1);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(ret);
   }

   @Override
   public void add(final int arg0, final T arg1)
   {
      m_ReadWriteLock.writeLock().lock();
      try
      {
         m_List.add(arg0, arg1);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
   }

   @Override
   public T remove(final int iIndex)
   {
      m_ReadWriteLock.writeLock().lock();
      T ret;
      try
      {
         ret = m_List.remove(iIndex);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(ret);
   }

   @Override
   public int indexOf(final Object o)
   {
      m_ReadWriteLock.readLock().lock();
      try
      {
          return(m_List.indexOf(o));
      }
      finally
      {
          m_ReadWriteLock.readLock().unlock();
      }
   }

   @Override
   public int lastIndexOf(final Object o)
   {
      m_ReadWriteLock.readLock().lock();
      try
      {
          return(m_List.lastIndexOf(o));
      }
      finally
      {
          m_ReadWriteLock.readLock().unlock();
      }
   }

   @Override
   public ListIterator<T> listIterator()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public ListIterator<T> listIterator(final int iIndex)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public List<T> subList(final int arg0, final int arg1)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
