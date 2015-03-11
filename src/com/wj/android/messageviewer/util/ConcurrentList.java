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
package com.wj.android.messageviewer.util;

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
 * </p>
 *
 * @param <T> the type of elements maintained by this list.
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class ConcurrentList<T> implements List< T>, Serializable
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

   /**
    * {@inheritDoc}
    *
    * @param o element to be removed from this list, if present
    *
    *  @return {@code true} if this list contained the specified element
    */
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

   /**
    * {@inheritDoc}
    *
    * @param t element to be appended to this list
    *
    * @return {@code true} if this collection changed as a result of the call
    */
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

   /**
    * {@inheritDoc}
    */
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

   /**
    * {@inheritDoc}
    *
    * @return the number of elements in this list
    */
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

   /**
    * {@inheritDoc}
    *
    * @param o element whose presence in this list is to be tested
    *
    * @return {@code true} if this list contains the specified element
    */
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

   /**
    * {@inheritDoc}
    *
    * @param iIndex index of the element to return
    *
    * @return the element at the specified position in this list
    */
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

   /**
    * {@inheritDoc}
    *
    * @return {@code true} if this list contains no elements
    */
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

   /**
    * {@inheritDoc}
    *
    * @return an iterator over the elements in this list in proper sequence
    */
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

   /**
    * {@inheritDoc}
    *
    * @return an array containing the elements of this list.
    *
    * @throws UnsupportedOperationException always
    */
   @Override
   public Object[] toArray() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   /**
    * {@inheritDoc}
    *
    * @param a the array into which the elements of this list are to be stored,
    *          if it is big enough; otherwise, a new array of the same runtime
    *          type is allocated for this purpose.
    *
    * @param <T> the type of elements maintained by in the array
    *
    * @return an array containing the elements of this list.
    *
    * @throws UnsupportedOperationException always
    */
   @Override
   public <T> T[] toArray(final T[] a) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   /**
    * {@inheritDoc}
    *
    * @param c collection to be checked for containment in this list
    *
    * @return {@code true} if this list contains all of the elements of the
    *         specified collection
    */
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

   /**
    * {@inheritDoc}
    *
    * @param c collection containing elements to be added to this list
    *
    * @return {@code true} if this list changed as a result of the call
    */
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

   /**
    * {@inheritDoc}
    *
    * @param iIndex index at which to insert the first element from the
    *        specified collection
    * @param c collection containing elements to be added to this list
    *
    * @return {@code true} if this list changed as a result of the call
    */
   @Override
   public boolean addAll(final int iIndex, final Collection<? extends T> c)
   {
      m_ReadWriteLock.writeLock().lock();
      boolean iRet;
      try
      {
         iRet = m_List.addAll(iIndex, c);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(iRet);
   }

   /**
    * {@inheritDoc}
    *
    * @param c collection containing elements to be removed from this list
    *
    * @return {@code true} if this list changed as a result of the call
    */
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

   /**
    * {@inheritDoc}
    *
    * @param c collection containing elements to be retained in this list
    *
    * @return true if this list changed as a result of the call
    *
    * @throws UnsupportedOperationException always
    */
   @Override
   public boolean retainAll(final Collection<?> c) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   /**
    * {@inheritDoc}
    *
    * @param iIndex  index of the element to replace
    * @param element element to be stored at the specified position
    *
    * @return the element previously at the specified position
    */
   @Override
   public T set(final int iIndex, final T element)
   {
      m_ReadWriteLock.writeLock().lock();
      T ret;
      try
      {
         ret = m_List.set(iIndex, element);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
      return(ret);
   }

   /**
    * {@inheritDoc}
    *
    * @param iIndex index at which the specified element is to be inserted
    * @param element element to be inserted
    */
   @Override
   public void add(final int iIndex, final T element)
   {
      m_ReadWriteLock.writeLock().lock();
      try
      {
         m_List.add(iIndex, element);
      }
      finally
      {
         m_ReadWriteLock.writeLock().unlock();
      }
   }

   /**
    * {@inheritDoc}
    *
    * @param iIndex the index of the element to be remove
    *
    * @return the element previously at the specified position
    */
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

   /**
    * {@inheritDoc}
    *
    * @param o  element to search for
    *
    * @return the index of the first occurrence of the specified element in
    *         this list, or -1 if this list does not contain the element
    */
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

   /**
    * {@inheritDoc}
    *
    * @param o element to search for
    *
    * @return the index of the last occurrence of the specified element in
    *         this list, or -1 if this list does not contain the element
    */
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

   /**
    * {@inheritDoc}
    *
    * @return a list iterator over the elements in this list
    *           (in proper sequence)
    *
    * @throws UnsupportedOperationException always
    */
   @Override
   public ListIterator<T> listIterator() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   /**
    * {@inheritDoc}
    *
    * @param iIndex index of first element to be returned from the list
    *               iterator (by a call to the {@code next} method)
    *
    * @return a list iterator of the elements in this list (in proper sequence),
    *           starting at the specified position in this list
    *
    * @throws UnsupportedOperationException always
    */
   @Override
   public ListIterator<T> listIterator(final int iIndex) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   /**
    * {@inheritDoc}
    *
    * @param iFromIndex low endpoint (inclusive) of the subList
    * @param iToIndex high endpoint (exclusive) of the subList
    *
    * @return a view of the specified range within this list
    *
    * @throws UnsupportedOperationException always
    */
   @Override
   public List<T> subList(final int iFromIndex, final int iToIndex) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
