/*
 * $Id$
 *
 * File:   RecentCollection.java
 * Author: Werner Jaeger
 *
 * Created on Feb 28, 2015, 10:33:44 PM
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * A collection to store a list of recently used elements e.g. a recently
 * opened files list.
 *
 * <p>
 *     If elements are added into this collection, the ones least recently
 *     used are removed if MAXIMUM size is exceed.
 * </p>
 *
 * <p>
 *    If an element is added that is there already, then it becomes the most
 *    recently used element.
 * </p>
 *
 * <p>
 *     Returns elements in the most recently used order.
 * </p>
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
class RecentCollection<E extends Serializable> implements Iterable<E>
{
   private static final Logger LOGGER = Logger.getLogger(RecentCollection.class.getName());
   private static final int PIECELENGTH = (int)Math.floor(Preferences.MAX_VALUE_LENGTH * 0.75);

   private static final int MAXIMUM = 10;

   private final Collection<E>      m_Recent;
   private final Collection<String> m_Keys;
   private final int                m_iMaximumElements;
   private final Preferences        m_prefNode;
   private final String             m_strKeyPrefix;

   /**
    * Constructs a new {@code RecentCollection} with a maximum of 10 elements.
    *
    * @param prefNode the backing store. If {@code null} this collection will
    *                 not be persisted in a backing store.
    *
    * @param strKey the key this collection is associated with in the backing
    *               store. Must not be {@code null}.
    */
   public RecentCollection(final Preferences prefNode, final String strKey)
   {
      this(MAXIMUM, prefNode, strKey);
   }

   /**
    * Constructs a new {@code RecentCollection}.
    *
    * @param iMaximumElements the maximum number of element to be stored.
    * @param prefNode the backing store. Must not be {@code null}.
    * @param strKey the key this collection is associated with in the backing
    *               store. Must not be {@code null}.
    */
   public RecentCollection(final int iMaximumElements, final Preferences prefNode, final String strKey)
   {
      // Instead of ordering elements by insertion order, we can also order
      // them by access order We do this by passing in the parameter
      // {@code true} to the {@code LinkedHashMap} constructor
      m_Recent = Collections.newSetFromMap(new LinkedHashMap<E, Boolean>(32, 0.7f, true)
      {
         private static final long serialVersionUID = -7730262475692921424L;

         /**
          * Remove the eldest entry each time a new one is added.
          *
          * <p>
          *    Method returns {@code true} if this map should remove its eldest
          *    entry.
          * </p>
          *
          * <p>
          *    This method is invoked by put and putAll after inserting a new
          *    entry into the map.
          * </p>
          *
          * @param eldest the least recently accessed entry.
          * @return
          */
         @Override
         protected boolean removeEldestEntry(final Map.Entry<E, Boolean> eldest)
         {
            return(size() > iMaximumElements);
         }
      });

      m_Keys = new ArrayList<>();
      m_iMaximumElements = iMaximumElements;
      m_prefNode = prefNode;
      m_strKeyPrefix = strKey + ".";
   }

   /**
    * Adds an element to the collection.
    *
    * @param element the element to add. Must not be {@code null}.
    *
    * @return {@code true} if and only if the element was not yet in this
    *         collection, {@code false} otherwise.
    */
   public boolean add(final E element)
   {
      final boolean fAlreadyThere = m_Recent.contains(element);

      m_Recent.add(element);
      sync();
      return(!fAlreadyThere);
   }

   /**
    * Removes an element from the collection.
    *
    * @param element the element to remove. if {@code null} nothing happens.
    */
   public void remove(final E element)
   {
      if (null != element)
      {
         m_Recent.remove(element);
         sync();
      }
   }

   /**
    * Removes all elements form this collection.
    *
    * <p>
    *    The collection will be empty after this call returns.
    * </p>
    */
   public void clear()
   {
       final ArrayList<String> keys = new ArrayList<>(m_Keys);

      for (final String strKey : keys)
         removeFromPreferences(strKey);

       m_Recent.clear();
       m_Keys.clear();
   }

   /** {@inheritDoc */
   @Override
   public Iterator<E> iterator()
   {
      // and maybe read the property list in case it changed ... ?
      return(Collections.unmodifiableCollection(new ReverseCollection<>(m_Recent)).iterator());
   }

   /**
    * Fill this collection with the elements from the backing store.
    *
    * @return {@code true} if and only if this collection is successfully
    *         filled from elements of the backing store.
    */

   public boolean loadFromPreferences()
   {
      clear();

      boolean fRet = true;

      if (null != m_prefNode)
      {
         // load recent elements from properties
         for (int i = 0; i < m_iMaximumElements; i++)
         {
            try
            {
               final Object o = getObject(m_prefNode, m_strKeyPrefix + i);

               if (null != o)
               {
                  @SuppressWarnings("unchecked")
                  final E element = (E)o;
                  m_Recent.add(element);
                  m_Keys.add(m_strKeyPrefix + i);
               }
            }
            catch (final IOException | BackingStoreException | ClassNotFoundException ex)
            {
               fRet = false;
               LOGGER.log(Level.SEVERE, ex.toString(), ex);
            }
         }
      }
      else
         fRet = false;

      return(fRet);
   }

   private void sync()
   {
       final ArrayList<E> values = new ArrayList<>(m_Recent);

       clear();

       int i = 0;
       for (final E eValue : values)
       {
          m_Recent.add(eValue);
          add2Preferences(eValue, m_strKeyPrefix + i++);
       }
   }

   private void removeFromPreferences(final String strKey)
   {
      if (null != m_prefNode && null != strKey)
      {
         try
         {
            // remove element with given key from properties
            for (final String strSubKey : getSubKeys(m_prefNode, strKey))
               m_prefNode.remove(strSubKey);

            m_Keys.remove(strKey);
         }
         catch (final BackingStoreException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   private void add2Preferences(final E e, final String strKey)
   {
      if (null != m_prefNode)
      {
         try
         {
            putObject(m_prefNode, strKey, e);
            m_Keys.add(strKey);
         }
         catch (final IOException | BackingStoreException | ClassNotFoundException ex)
         {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
      }
      }
   }

   private static void putObject(final Preferences prefs, final String strKey, final Object o) throws IOException, BackingStoreException, ClassNotFoundException
   {
      final byte abRaw[] = object2Bytes(o);
      final byte abPieces[][] = breakIntoPieces(abRaw);

      writePieces(prefs, strKey + ".", abPieces);
   }

   static Object getObject(final Preferences prefs, final String strKey) throws IOException, BackingStoreException, ClassNotFoundException
   {
      final byte abPieces[][] = readPieces(prefs, strKey + ".");
      final byte abRaw[] = combinePieces(abPieces);

      return(bytes2Object(abRaw));
   }

   private static byte[][] breakIntoPieces(final byte abRaw[])
   {
      final int iNumPieces = (abRaw.length + PIECELENGTH - 1) / PIECELENGTH;
      final byte abPieces[][] = new byte[iNumPieces][];
      for (int i = 0; i < iNumPieces; ++i)
      {
         final int iStartByte = i * PIECELENGTH;

         int iEndByte = iStartByte + PIECELENGTH;
         if (iEndByte > abRaw.length)
               iEndByte = abRaw.length;

         final int iLength = iEndByte - iStartByte;
         abPieces[i] = new byte[iLength];
         System.arraycopy(abRaw, iStartByte, abPieces[i], 0, iLength);
      }
      return(abPieces);
   }

   private static byte[] combinePieces(final byte abPieces[][])
   {
      int iLength = 0;
      for (int i = 0; i < abPieces.length; ++i)
         iLength += abPieces[i].length;

      final byte abRaw[] = new byte[iLength];
      int iCursor = 0;
      for (int i = 0; i < abPieces.length; ++i)
      {
         System.arraycopy(abPieces[i], 0, abRaw, iCursor, abPieces[i].length);
         iCursor += abPieces[i].length;
      }
      return(abRaw);
   }

   private static byte[] object2Bytes(final Object o) throws IOException
   {
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      final ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(o);
      return(baos.toByteArray());
   }

   private static Object bytes2Object(final byte abRaw[]) throws IOException, ClassNotFoundException
   {
      final Object o;

      if (0 < abRaw.length)
      {
         final ByteArrayInputStream bais = new ByteArrayInputStream(abRaw);
         final ObjectInputStream ois = new ObjectInputStream(bais);
         o = ois.readObject();
      }
      else
         o = null;

      return(o);
   }

   private static byte[][] readPieces(final Preferences prefs, final String strKey) throws BackingStoreException
   {
      final ArrayList<byte[]> pieces = new ArrayList<>();

      final Collection<String> astrSubKeys = getSubKeys(prefs, strKey);
      for (final String strPrefsKey : astrSubKeys)
      {
         final byte[] abPiece = prefs.getByteArray(strPrefsKey, null);

         if (null != abPiece)
         {
            final String strIndex = strPrefsKey.substring(strKey.length());
            pieces.add(Integer.parseInt(strIndex), abPiece);
         }
      }

      return(pieces.toArray(new byte[pieces.size()][]));
   }

   private static void writePieces(final Preferences prefs, final String strKey, final byte[][] abPieces)
   {
      for (int i = 0; i < abPieces.length; i++)
         prefs.putByteArray(strKey + i, abPieces[i]);
   }

   private static Collection<String> getSubKeys(final Preferences prefs, final String strKey) throws BackingStoreException
   {
      final ArrayList<String> astrSubkeys = new ArrayList<>();

      final String[] astrKeys = prefs.keys();
      for (final String strPefsKey : astrKeys)
      {
         if (strPefsKey.startsWith(strKey))
            astrSubkeys.add(strPefsKey);
      }
      return(astrSubkeys);
   }
}
