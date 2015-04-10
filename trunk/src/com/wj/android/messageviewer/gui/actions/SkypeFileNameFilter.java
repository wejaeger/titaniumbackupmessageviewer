/*
 * $Id$
 *
 * File:   SkypeFileNameFilter.java
 * Author: Werner Jaeger
 *
 * Created on Apr 3, 2015, 11:48:17 AM
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
package com.wj.android.messageviewer.gui.actions;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileFilter;

/**
 * An implementation of {@code FileFilter} that filters Skype
 * conversation database files.
 *
 * <p>
 *    Accepts files with this
 *    {@code com.skype.raider-YYMMDD-HHMMSS.tar.gz}
 *    name pattern and all files named {@code main.db}.
 * </p>
 *
 * @author Werner Jaeger
 */
class SkypeFileNameFilter extends FileFilter
{
   private static final Pattern NAMEPATTERN = Pattern.compile("main\\.db|com\\.skype\\.raider\\-[0-9]{8}\\-[0-9]{6}\\.tar\\.gz", Pattern.CASE_INSENSITIVE);

   private final Matcher m_Matcher;

   /**
    * Creates new {@code SkypeFileNameFilter}.
    */
   public SkypeFileNameFilter()
   {
      super();

      m_Matcher = NAMEPATTERN.matcher("");
   }

   /**
    * Tests the specified file.
    *
    * <p>
    *    Accepts files with this
    *    {@code com.skype.raider-YYMMDD-HHMMSS.tar.gz}
    *    name pattern and all files named {@code main.db}.
    * </p>
    *
    * @param f the File to test, if {@code null} {@code false} is returned.
    *
    * @return {@code true} if and only if specified file is a directory or
    *         matches the above described name pattern.
    */
   @Override
   public boolean accept(final File f)
   {
      final boolean fRet;

      if (null != f)
      {
         m_Matcher.reset(f.getName());

         fRet = f.isDirectory() || m_Matcher.matches();
      }
      else
         fRet = false;

      return(fRet);
   }

   /**
    * The description of this filter.
    *
    * @return {@code Skype conversation database and archived conversation database files}.
    */
   @Override
   public String getDescription()
   {
      return("Skype conversation database and archived conversation database files");
   }
}
