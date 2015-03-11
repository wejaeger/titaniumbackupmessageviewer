/*
 * $Id$
 *
 * File:   BackupMessageViewerApplication.java
 * Author: Werner Jaeger
 *
 * Created on Mar 9, 2015, 7:30:20 AM
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
package com.wj.android.messageviewer;

import com.wj.android.messageviewer.gui.BackupMessageViewerFrame;
import com.wj.android.messageviewer.gui.workers.LoadMessagesWorker;
import com.wj.android.messageviewer.io.IMessageReader;
import com.wj.android.messageviewer.util.Pair;
import com.wj.android.messageviewer.util.RecentCollection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * The message viewer application's main class.
 *
 * <p>
 *    This class is designed as singleton.
 * </p>
 *
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public class BackupMessageViewerApplication
{
   private static final Logger LOGGER = Logger.getLogger(BackupMessageViewerApplication.class.getName());

   private static final Preferences PREFERENCES = Preferences.userNodeForPackage(BackupMessageViewerApplication.class);

   private static final String RECENTFILEKEY = "recentfile";
   private static final String WINDOWXKEY = "window.x";
   private static final String WINDOWYKEY = "window.y";
   private static final String WINDOWWIDTHKEY = "window.width";
   private static final String WINDOWHEIGTHKEY = "window.height";
   private static final String WINDOWSTATEKEY = "window.state";
   private static final String WINDOVISIBLE = "window.visible";

   private static BackupMessageViewerApplication m_Instance;

   private final RecentCollection<Pair<String, String>> m_RecentCollection;

   private BackupMessageViewerFrame m_AppFrame;

   /**
    * Prevent instantiation
    */
   private BackupMessageViewerApplication()
   {
      m_RecentCollection = new RecentCollection<>(PREFERENCES, RECENTFILEKEY);
   }

   /**
    * Get the single instance of this class.
    *
    * @return the only instance of this class.
    */
   public static BackupMessageViewerApplication getInstance()
   {
      return(m_Instance);
   }

   /**
    * Launch the application and display the main window.
    *
    * @param asArgs the first optional argument is the full path to the message
    *               file to open at start up, the second optional argument is
    *               the full path to a contacts database file.
    */
   public static void main(final String[] asArgs)
   {
      m_Instance = new BackupMessageViewerApplication();
      m_Instance.init(asArgs);
   }

   /**
    * Save application preference to user preference node.
    */
   public void savePreferences()
   {
      if (JFrame.MAXIMIZED_BOTH != m_AppFrame.getExtendedState())
      {
         PREFERENCES.putInt(WINDOWXKEY, m_AppFrame.getLocation().x);
         PREFERENCES.putInt(WINDOWYKEY, m_AppFrame.getLocation().y);
         PREFERENCES.putInt(WINDOWWIDTHKEY, m_AppFrame.getWidth());
         PREFERENCES.putInt(WINDOWHEIGTHKEY, m_AppFrame.getHeight());
      }

      PREFERENCES.putInt(WINDOWSTATEKEY, m_AppFrame.getExtendedState());
      PREFERENCES.putBoolean(WINDOVISIBLE, m_AppFrame.isVisible());

      try
      {
         PREFERENCES.flush();
      }
      catch (final BackingStoreException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   /**
    * Adds specified files to the persistent recent file list maintained by
    * this application.
    *
    * @param files2load the tiles to add.
    */
   public void add2RecentFileList(final Pair<String, String> files2load)
   {
      m_RecentCollection.add(files2load);
   }

   /**
    * Removes specified files from the persistent recent file list maintained by
    * this application.
    *
    * @param files2load the tiles to remove.
    */
   public void removeFromRecentFileList(final Pair<String, String> files2load)
   {
      m_RecentCollection.remove(files2load);
   }

   /**
    * Return stored recent files from the persistent recent file list
    * maintained by this application.
    *
    * @return all files in the recent file list.
    */
   public Iterable<Pair<String, String>> getRecentFiles()
   {
      return(m_RecentCollection);
   }

   private void init(final String[] asArgs)
   {
      if (!m_RecentCollection.loadFromPreferences())
         LOGGER.log(Level.WARNING, "Faild to load recent files from preferences");

      m_AppFrame = new BackupMessageViewerFrame();

      initAppFrameFromPrefs();

      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            final boolean fIsVisible = PREFERENCES.getBoolean(WINDOVISIBLE, true);
            m_AppFrame.setVisible(fIsVisible);
         }
      });

      if (asArgs.length == 1)
      {
         final IMessageReader.MessageFileType messageReaderType = IMessageReader.MessageFileType.getMessageFileType(asArgs[0]);

         if (null != messageReaderType)
            new LoadMessagesWorker(m_AppFrame, new Pair<String, String>(asArgs[0], null), messageReaderType).execute();
         else
            JOptionPane.showMessageDialog(m_AppFrame, "Specified file is a unknown backup message file", "Error", JOptionPane.ERROR_MESSAGE);
      }
      else if (asArgs.length == 2)
      {
         final IMessageReader.MessageFileType messageReaderType = IMessageReader.MessageFileType.getMessageFileType(asArgs[0]);

         if (null != messageReaderType)
            new LoadMessagesWorker(m_AppFrame, new Pair<>(asArgs[0], asArgs[1]), messageReaderType).execute();
         else
            JOptionPane.showMessageDialog(m_AppFrame, "Specified file is a unknown backup message file", "Error", JOptionPane.ERROR_MESSAGE);
      }
   }

   private void initAppFrameFromPrefs()
   {
      final int ix = PREFERENCES.getInt(WINDOWXKEY, 100);
      final int iy = PREFERENCES.getInt(WINDOWYKEY, 100);
      final int iWidth = PREFERENCES.getInt(WINDOWWIDTHKEY, 700);
      final int iHeight = PREFERENCES.getInt(WINDOWHEIGTHKEY, 500);
      final int iState = PREFERENCES.getInt(WINDOWSTATEKEY, JFrame.NORMAL);
      m_AppFrame.setLocation(ix, iy);
      m_AppFrame.setSize(iWidth, iHeight);
      m_AppFrame.setExtendedState(iState);
   }
}
