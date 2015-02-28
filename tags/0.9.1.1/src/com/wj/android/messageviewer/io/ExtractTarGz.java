/*
 * $Id$
 *
 * File:   ExtractTarGz.java
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
package com.wj.android.messageviewer.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

/**
 * This utility class encapsulates static methods to facilitate extraction of
 * the contents of a {@code tar.gz} file to a destination directory.
 *
 * @author Werner Jaeger
 */
public class ExtractTarGz
{
   private static final int DEFAULT_BUFFER_SIZE = 8192;

   /**
    * Creates new {@code ExtractTarGz}.
    *
    * <p>
    *    Restrict instantiation of this utility class.
    * </p>
    */
   private ExtractTarGz()
   {
      // Exists only to defeat instantiation.
   }

   /**
    * Extract the given input tar file to given output directory.
    *
    * <p>
    *    Returns the first file with the given filename found in the output
    *    directory. File name comparison is case sensitive.
    * </p>
    *
    * <p>
    *    Output directory including any necessary but nonexistent parent
    *    directories are created.
    * </p>
    *
    * <p>
    *    If input file can have {@code tar.gz}  or {@code .tar} extension.
    * </p
    *
    * @param inputFile the {@code tar} or {@code tar.gz} archive to extract.
    * @param outputDir the directory to extract the archive to.
    * @param strFileNameToFind file name (without path) to look for.
    * @return if a file with with the name given in {@code strFileNameToFind}
    *         was extracted to the output directory the full path of the first
    *         found file is returned, otherwise {@code null}.
    */
   public static File extractTarGzAndFindFirst(final File inputFile, final File outputDir, final String strFileNameToFind)
   {
      File returnFile = null;

      final List<File> untaredFiles = extractTarGz(inputFile, outputDir);

      if (null != untaredFiles)
      {
         for (File file : untaredFiles)
         {
            if (file.isFile() && file.getName().equals(strFileNameToFind))
            {
               returnFile = file;
               break;
            }
         }
      }

      return(returnFile);
   }

   /**
    * Extract the given input tar file to given output directory.
    *
    * <p>
    *    Output directory including any necessary but nonexistent parent
    *    directories are created.
    * </p>
    *
    * <p>
    *    If input file can have {@code tar.gz}  or {@code .tar} extension.
    * </p
    *
    * @param inputFile the tar or tar.gz archive to extract.
    * @param outputDir the directory to extract the archive to.
    *
    * @return The {@link List} of {@link File}s with the untared content or
    *         {@code null} if an error occurred.
    */
   public static List<File> extractTarGz(final File inputFile, final File outputDir)
   {
      List<File> untaredFiles = null;

      outputDir.mkdirs();

      if (outputDir.exists())
      {
         try
         {
            final InputStream in;
            if (inputFile.getName().endsWith(".gz"))
               in = new GZIPInputStream(new FileInputStream(inputFile));
            else
               in = new FileInputStream(inputFile);

            untaredFiles = unTar(in, outputDir);
         }
         catch (final IOException | ArchiveException e)
         {
            untaredFiles = null;
         }
      }

      return(untaredFiles);
   }

   /**
    * {@code Untar} an input file into an output file.
    *
    * @param inputStream the input stream of the .tar file
    * @param outputDir the output directory file.
    * @throws IOException
    * @throws FileNotFoundException
    *
    * @return The {@link List} of {@link File}s with the untared content.
    * @throws ArchiveException
    */
   private static List<File> unTar(final InputStream inputStream, final File outputDir) throws FileNotFoundException, IOException, ArchiveException
   {
      final List<File> untaredFiles = new LinkedList<>();

      try (TarArchiveInputStream debInputStream = (TarArchiveInputStream)new ArchiveStreamFactory().createArchiveInputStream("tar", inputStream))
      {
         TarArchiveEntry entry;
         while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null)
         {
            final File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory())
            {
               if (!outputFile.exists())
               {
                  if (!outputFile.mkdirs())
                     throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
               }
            }
            else
            {
               try (OutputStream outputFileStream = new FileOutputStream(outputFile))
               {
                  io(debInputStream, outputFileStream);
               }
            }
            untaredFiles.add(outputFile);
         }
      }

      return(untaredFiles);
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
   private static void io(final InputStream in, final OutputStream out) throws IOException
   {
      io(in, out, DEFAULT_BUFFER_SIZE);
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
   private static void io(final InputStream in, final OutputStream out, final int iBufferSize) throws IOException
   {
      final byte[] abBuffer = new byte[iBufferSize];
      int iAmount;

      while ((iAmount = in.read(abBuffer)) >= 0)
         out.write(abBuffer, 0, iAmount);
   }
}
