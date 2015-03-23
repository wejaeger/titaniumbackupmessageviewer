/*
 * $Id$
 *
 * File:   IOUtils.java
 * Author: Werner Jaeger
 *
 * Created on Mar 9, 2015, 7:39:43 PM
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Various IO Helper methods.
 *
 * @author Werner Jaeger
 */
public class IOUtils
{
   private static final int DEFAULT_BUFFER_SIZE = 8192;

   // prevent instantiation
   private IOUtils()
   {

   }

   /**
    * Copies the specified input stream to the specified output stream using
    * the default buffer size of 8192.
    *
    * @param in contains the data that are written to {@code out}.
    * @param out the output stream that receives the data from the specified input stream.
    *
    * @throws IOException if the underlying read or write methods fails.
    */
   public static void io(final InputStream in, final OutputStream out) throws IOException
   {
      copy(in, out, DEFAULT_BUFFER_SIZE);
   }

   /**
    * Copies the specified input stream to the specified output stream using
    * the specified buffer size.
    *
    * @param in contains the data that are written to {@code out}.
    * @param out the output stream that receives the data from the specified input stream.
    * @param iBufferSize the size of the buffer to use in bytes.
    *
    * @throws IOException if the underlying read or write methods fails.
    */
   public static void copy(final InputStream in, final OutputStream out, final int iBufferSize) throws IOException
   {
      final byte[] abBuffer = new byte[iBufferSize];
      int iAmount;

      while ((iAmount = in.read(abBuffer)) >= 0)
         out.write(abBuffer, 0, iAmount);
   }

   /**
    * Copy bytes from an <code>InputStream</code> to chars on a
    * <code>Writer</code> using the default character encoding of the platform.
    * <p>
    * This method buffers the input internally, so there is no need to use a
    * <code>BufferedInputStream</code>.
    * <p>
    * This method uses {@link InputStreamReader}.
    *
    * @param input  the <code>InputStream</code> to read from
    * @param output  the <code>Writer</code> to write to
    * @param strEncoding  the name of supported encoding to use. Never {@code null}
    * @throws NullPointerException if the input or output is {@code null}
    * @throws IOException if an I/O error occurs
    * @since Commons IO 1.1
    */
   public static void copy(final InputStream input, final Writer output, final String strEncoding) throws IOException
   {
      final InputStreamReader in = new InputStreamReader(input, strEncoding);
      copy(in, output);
   }

   /**
    * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
    * <p>
    * This method buffers the input internally, so there is no need to use a
    * <code>BufferedReader</code>.
    * <p>
    * Large streams (over 2GB) will return a chars copied value of
    * <code>-1</code> after the copy has completed since the correct
    * number of chars cannot be returned as an int. For large streams
    * use the <code>copyLarge(Reader, Writer)</code> method.
    *
    * @param input  the <code>Reader</code> to read from
    * @param output  the <code>Writer</code> to write to
    * @return the number of characters copied
    * @throws NullPointerException if the input or output is {@code null}
    * @throws IOException if an I/O error occurs
    * @throws ArithmeticException if the character count is too large
    * @since Commons IO 1.1
    */
   public static int copy(final Reader input, final Writer output) throws IOException
   {
      final int iRet;

      final long lCount = copyLarge(input, output);

      if (lCount > Integer.MAX_VALUE)
           iRet = -1;
      else
         iRet = (int)lCount;

      return(iRet);
   }

   /**
    * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
    * <p>
    * This method buffers the input internally, so there is no need to use a
    * <code>BufferedReader</code>.
    *
    * @param input  the <code>Reader</code> to read from
    * @param output  the <code>Writer</code> to write to
    * @return the number of characters copied
    * @throws NullPointerException if the input or output is {@code null}
    * @throws IOException if an I/O error occurs
    * @since Commons IO 1.3
    */
   public static long copyLarge(final Reader input, final Writer output) throws IOException
   {
      final char[] acBuffer = new char[DEFAULT_BUFFER_SIZE];
      long lCount = 0;
      int iN = 0;
      while (-1 != (iN = input.read(acBuffer)))
      {
         output.write(acBuffer, 0, iN);
         lCount += iN;
      }

      return(lCount);
   }
}
