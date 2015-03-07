/*
 * $Id$
 *
 * File:   TitaniumBackupMessageViewer.java
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
package com.wj.android.messageviewer.gui;

import com.wj.android.messageviewer.io.TitaniumBackupMessageReader;
import com.wj.android.messageviewer.message.MessageThread;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Main class and window for this application.
 *
 * <p>
 *    {@code TitaniumBackupMessageViewer} implements a threaded messaging
 *    viewer. that means it displays both sides of a messaging (SMS/MMS)
 *    conversation on one right screen side , in chronological order. On the
 *    left side a list box of message threads is displayed. In this
 *     way it groups messages from the same person and phone number together.
 * <p/>
 *
 * @author >Werner Jaeger
 */
public final class TitaniumBackupMessageViewer
{
   private static class AbstractListModelImpl extends AbstractListModel<MessageThread>
   {
      private static final long serialVersionUID = -3879053127396522633L;
      final MessageThread m_Thread;

      public AbstractListModelImpl()
      {
         m_Thread = new MessageThread("Threads load here...", "");
      }

      @Override
      public int getSize()
      {
         return(1);
      }

      @Override
      public MessageThread getElementAt(int iIndex)
      {
         return(m_Thread);
      }
   }

   private static class HyperlinkListenerImpl implements HyperlinkListener
   {
      public HyperlinkListenerImpl()
      {
      }

      @Override
      public void hyperlinkUpdate(final HyperlinkEvent evt)
      {
         if (HyperlinkEvent.EventType.ACTIVATED == evt.getEventType())
         {
            if(Desktop.isDesktopSupported())
            {
               try
               {
                  Desktop.getDesktop().browse(evt.getURL().toURI());
               }
               catch (final IOException | URISyntaxException e)
               {
                  LOGGER.log(Level.SEVERE, e.toString(), e);
               }
            }
         }
      }
   }

   private static final Logger LOGGER = Logger.getLogger(TitaniumBackupMessageViewer.class.getName());

   // various keys for application.properties
   private static final String VERSION = "version.num";
   private static final String DATE = "version.dat";
   private static final String TITLE = "project.name";

   private static final String RESOURCEPATH = "/com/wj/android/messageviewer/resources/";
   private static final Preferences PREFERENCES = Preferences.userNodeForPackage(TitaniumBackupMessageViewer.class);
   private static final String FILELOCATIONSEPARATOR = ":";

   private static final String recentFilesKey = "recentfile";
   private static final String windowXKey = "window.x";
   private static final String windowYKey = "window.y";
   private static final String windowWidthKey = "window.width";
   private static final String windowHeightKey = "window.height";
   private static final String windowStateKey = "window.state";
   private static final String windowVisibleKey = "window.visible";

   private final RecentCollection<Pair<String, String>> m_RecentCollection;
   private final Pair<String, String> m_FileLocations;

   private JFrame m_ReaderFrame;
   private JTextField m_NumberSMSField;
   private JFileChooser m_MessageFileChooser;
   private JFileChooser m_ContactsDatabaseChooser;
   private JFileChooser m_SaveChooser;
   private JScrollPane m_ScrollPaneThread;
   private JScrollPane m_ScrollPaneMessages;
   private JMenu m_mnRecentFiles;
   private JList<MessageThread> m_ThreadListBox;
   private MessageViewer m_MessageViewer;
   private TitaniumBackupMessageReader m_Reader;

   /**
    * Creates new {@code GUI}.
    *
    * @param strMessageFileName the full pathname to a message file that should
    *        be loaded or {@code null} if no message file should be loaded.
    * @param strContactsDBFileName the SQLLite contacts database that should be
    *        used to query contact names from message addresses or {@code null}
    *        if no contacts database should be used.
    */
   public TitaniumBackupMessageViewer(final String strMessageFileName, final String strContactsDBFileName)
   {
      m_RecentCollection = new RecentCollection<>(PREFERENCES, recentFilesKey);
      m_FileLocations = new Pair<>(null, null);

      initialize(strMessageFileName, strContactsDBFileName);
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
      EventQueue.invokeLater(new Runnable()
      {
         boolean fIsVisible = PREFERENCES.getBoolean(windowVisibleKey, true);

         @Override
         public void run()
         {
            if (asArgs.length == 1)
               new TitaniumBackupMessageViewer(asArgs[0], null).m_ReaderFrame.setVisible(fIsVisible);
            else if (asArgs.length == 2)
               new TitaniumBackupMessageViewer(asArgs[0], asArgs[1]).m_ReaderFrame.setVisible(fIsVisible);
            else if (asArgs.length == 0)
               new TitaniumBackupMessageViewer(null, null).m_ReaderFrame.setVisible(fIsVisible);
         }
      });
   }

   /**
    * This method is called from within the constructor to initialize the form.
    */
   private void initialize(final String strMessageFileName, final String strContactsDBFileName)
   {
      m_Reader = new TitaniumBackupMessageReader();

      m_ReaderFrame = new JFrame();
      m_ReaderFrame.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(final WindowEvent windowEvent)
         {
            shutdown();
         }
      });

      m_ReaderFrame.setTitle(getApplicationProperty(TITLE));
      m_ReaderFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      initReaderFrameFromPrefs();

      setFrameIcon();

      UIManager.put("FileChooser.readOnly", Boolean.TRUE);
      m_MessageFileChooser = new JFileChooser();
      m_MessageFileChooser.setDialogTitle("Open message file");
      m_MessageFileChooser.setFileFilter(new TitaniumBackupMessageFileNameFilter());
      m_MessageFileChooser.setApproveButtonToolTipText("You can open the commpressd (.gz) or plain (.xml) message file");


      UIManager.put("FileChooser.readOnly", Boolean.TRUE);
      m_ContactsDatabaseChooser = new JFileChooser();
      m_ContactsDatabaseChooser.setDialogTitle("Open contacts database");
      m_ContactsDatabaseChooser.setFileFilter(new TitaniumBackupContactsFileNameFilter());
      m_ContactsDatabaseChooser.setApproveButtonToolTipText("You can open the archived (.tar.gz) or the plain (.db) contacts database");

      m_SaveChooser = new JFileChooser();

      createMenu();

      if (null != strMessageFileName && !strMessageFileName.trim().isEmpty())
         m_FileLocations.setFirst(strMessageFileName);
      else
         m_FileLocations.setFirst(null);

      if (null != strContactsDBFileName && !strContactsDBFileName.trim().isEmpty())
         m_FileLocations.setSecond(strContactsDBFileName);
      else
         m_FileLocations.setSecond(null);

      final JLabel lblNumberOfSms = new JLabel("Number of SMS:");

      m_NumberSMSField = new JTextField();
      m_NumberSMSField.setEditable(false);
      m_NumberSMSField.setColumns(10);
      m_NumberSMSField.setBorder(BorderFactory.createEmptyBorder());

      m_MessageViewer = new MessageViewer();
      m_MessageViewer.setBackground(m_ReaderFrame.getContentPane().getBackground());
      m_ScrollPaneMessages = new JScrollPane(m_MessageViewer);
      m_ScrollPaneMessages.getVerticalScrollBar().setUnitIncrement(10);


      m_ThreadListBox = new MessageThreadList(300);
      m_ThreadListBox.setBackground(m_ReaderFrame.getContentPane().getBackground());
      m_ThreadListBox.addListSelectionListener(new ListSelectionListener()
      {
         /** @inherited */
         @Override
         public void valueChanged(final ListSelectionEvent evt)
         {
            threadListValueChanged(evt);
         }
      });

      m_ScrollPaneThread = new JScrollPane(m_ThreadListBox);
      m_ThreadListBox.setModel(new AbstractListModelImpl());

      m_ThreadListBox.setEnabled(false);
      m_ThreadListBox.setBorder(new EtchedBorder(1, null, null));

      final Box h1Box = Box.createHorizontalBox();
      h1Box.add(Box.createVerticalStrut(lblNumberOfSms.getPreferredSize().height * 4));
      h1Box.add(lblNumberOfSms);
      h1Box.add(Box.createHorizontalStrut(10));
      h1Box.add(m_NumberSMSField);
      h1Box.setMaximumSize(h1Box.getPreferredSize());
      h1Box.setAlignmentX(Component.LEFT_ALIGNMENT);

      final Box h2Box = Box.createHorizontalBox();
      h2Box.add(m_ScrollPaneThread);
      h2Box.add(Box.createHorizontalStrut(10));
      h2Box.add(m_ScrollPaneMessages);
      h2Box.setAlignmentX(Component.LEFT_ALIGNMENT);

      m_ReaderFrame.add(h1Box, BorderLayout.PAGE_START);
      m_ReaderFrame.add(h2Box, BorderLayout.CENTER);

      m_ScrollPaneThread.setMinimumSize(new Dimension(100,  100));
      m_ScrollPaneThread.setMaximumSize(new Dimension(m_ThreadListBox.getPreferredSize().width, Short.MAX_VALUE));
      m_ScrollPaneMessages.setMinimumSize(new Dimension(300, 100));

      if (null != strMessageFileName)
         loadAction();
   }

   private void createMenu()
   {
      final JMenuBar menuBar = new JMenuBar();
      m_ReaderFrame.setJMenuBar(menuBar);

      final JMenu mnFile = new JMenu("File");
      menuBar.add(mnFile);

      final JMenuItem mntmOpen = new JMenuItem("Open ...");
      mntmOpen.setMnemonic(KeyEvent.VK_O);
      mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
      mntmOpen.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            chooseMessageFileAction();
         }
      });
      mnFile.add(mntmOpen);

      createRecentFileMenu(mnFile);

      mnFile.add(new JSeparator()); // SEPARATOR

      final JMenuItem mntmExportSelected = new JMenuItem("Export selected messages ...");
      mntmExportSelected.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            exportAction(false);
         }
      });
      mnFile.add(mntmExportSelected);

      final JMenuItem mntmExportAll = new JMenuItem("Export all messages ...");
      mntmExportAll.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            exportAction(true);
         }
      });
      mnFile.add(mntmExportAll);


      mnFile.add(new JSeparator()); // SEPARATOR

      final JMenuItem mntmClose = new JMenuItem("Quit");
      mntmClose.setMnemonic(KeyEvent.VK_Q);
      mntmClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
      mntmClose.setToolTipText("Quit application");
      mntmClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent event)
         {
            shutdown();
         }
      });
      mnFile.add(mntmClose);

      final JMenu mnHelp = new JMenu("Help");
      menuBar.add(mnHelp);

      final JMenuItem mntmHelp = new JMenuItem("Help");
      mntmHelp.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            helpAction();
         }
      });
      mnHelp.add(mntmHelp);

      final JMenuItem mntmAboutAndroidMessageReader = new JMenuItem("About " + getApplicationProperty(TITLE));
      mntmAboutAndroidMessageReader.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            aboutAction();
         }
      });
      mnHelp.add(mntmAboutAndroidMessageReader);
   }

   private void createRecentFileMenu(final JMenu mnFile)
   {
      if (m_RecentCollection.loadFromPreferences())
      {
         m_mnRecentFiles = new JMenu("Open Recent File");

         syncRecentFiles();

         mnFile.add(m_mnRecentFiles);
      }
   }

   void syncRecentFiles()
   {
      if (null != m_mnRecentFiles)
      {
         m_mnRecentFiles.removeAll();

         for (final Pair<String, String> pair : m_RecentCollection)
            addRecentFile2RecentFileMenu(pair);
      }
   }

   private void addRecentFile2RecentFileMenu(final Pair<String, String> pair)
   {
      final JMenuItem mnItem = new JMenuItem(pair.getFirst() + (pair.getSecond() == null ? "" :  FILELOCATIONSEPARATOR) + (pair.getSecond() == null ? "" :  pair.getSecond()));
      mnItem.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            final String[] strFiles = evt.getActionCommand().split("\\" + FILELOCATIONSEPARATOR, 2);
            if (1 <= strFiles.length)
               m_FileLocations.setFirst(strFiles[0]);

            if (2 <= strFiles.length)
               m_FileLocations.setSecond(strFiles[1]);

            loadAction();
         }
      });
      m_mnRecentFiles.add(mnItem);
    }

   private void chooseMessageFileAction()
   {
      m_MessageFileChooser.setSelectedFile(new File(""));
      final int iRet = m_MessageFileChooser.showOpenDialog(m_ReaderFrame);

      if (JFileChooser.APPROVE_OPTION == iRet)
      {
         m_FileLocations.setFirst(m_MessageFileChooser.getSelectedFile().getAbsolutePath());
         chooseContactsDBAction();
      }
   }

   private void chooseContactsDBAction()
   {
      m_ContactsDatabaseChooser.setCurrentDirectory(m_MessageFileChooser.getCurrentDirectory());

      m_ContactsDatabaseChooser.setSelectedFile(new File(""));
      final int iRet = m_ContactsDatabaseChooser.showOpenDialog(m_ReaderFrame);

      if (JFileChooser.APPROVE_OPTION == iRet)
         m_FileLocations.setSecond(m_ContactsDatabaseChooser.getSelectedFile().getAbsolutePath());

      loadAction();
  }

   private void loadAction()
   {
      if (null != m_FileLocations.getFirst())
         new LoadMessagesWorker(this, m_ReaderFrame, m_FileLocations, m_RecentCollection, m_Reader, m_ThreadListBox, m_NumberSMSField, m_MessageViewer, m_ScrollPaneThread).execute();
      else
         JOptionPane.showMessageDialog(m_ReaderFrame, "Choose message file first!");
   }

   private void threadListValueChanged(final ListSelectionEvent evt)
   {
      if (!evt.getValueIsAdjusting())
         new DisplayThreadMessageWorker(m_ReaderFrame, m_ThreadListBox.getSelectedValue(), m_MessageViewer).execute();
   }

   private void helpAction()
   {
   }

   private void aboutAction()
   {
      final StringBuffer strText = new StringBuffer("<html><body><center><h4>");
      strText.append(getApplicationProperty(TITLE));
      strText.append("</h4></center>");
      strText.append("<center><h5>");
      strText.append(getApplicationProperty(VERSION));
      strText.append(" (");
      strText.append(getApplicationProperty(DATE));
      strText.append(")</h5></center>");
      strText.append("<p><center>View messages from your Android device in a conversation like style.</center></p>");
      strText.append("<p><center>Copyright &copy; ");
      strText.append(getApplicationProperty(DATE).substring(0, 4));
      strText.append(" Werner Jaeger</center></p>");
      strText.append("<p><center>For more information please visit</center>");
      strText.append("<center><a href=\"https://sourceforge.net/projects/titaniumbackupmessageviewer/\">sourceforge.net/projects/titaniumbackupmessageviewer</a></center></p>");
      strText.append("</body></html>");

      final JEditorPane textPane = new JEditorPane();
      textPane.setContentType("text/html");
      textPane.setEditable(false);
      textPane.setText(strText.toString());
      textPane.setBackground((Color)UIManager.get("OptionPane.background"));
      textPane.addHyperlinkListener(new HyperlinkListenerImpl());

      JOptionPane.showMessageDialog(m_ReaderFrame, textPane, "About", JOptionPane.INFORMATION_MESSAGE, getDialogIcon());
   }

   private void exportAction(final boolean fAll)
   {
      if (null != m_FileLocations.getFirst() && m_ThreadListBox.isEnabled())
      {
         if (fAll || m_ThreadListBox.getSelectedIndex() != -1)
         {
            m_SaveChooser.setDialogTitle("Choose a file to save as...");
            m_SaveChooser.setDialogType(JFileChooser.SAVE_DIALOG);

            if (fAll)
                  m_SaveChooser.setSelectedFile(new File("AndroidAllMessagesExport.txt"));
            else
               m_SaveChooser.setSelectedFile(new File("AndroidSelectedMessagesExport.txt"));

            final int iRet = m_SaveChooser.showSaveDialog(m_ReaderFrame);
            if (iRet == 0)
               new ExportWorker(m_ReaderFrame, fAll, m_SaveChooser.getSelectedFile(), m_ThreadListBox.getSelectedIndex(), m_Reader).execute();
         }
         else
            JOptionPane.showMessageDialog(m_ReaderFrame, "Select a thread first!");
      }
      else
         JOptionPane.showMessageDialog(m_ReaderFrame, "Load messages first!");
   }

   private void setFrameIcon()
   {
      final String[] astrIcons = {"app-icon32.png", "app-icon48.png", "app-icon64.png"};
      final List<Image> icons = new ArrayList<>(5);

      for (final String astrIcon : astrIcons)
      {
         final ImageIcon icon = new ImageIcon(getClass().getResource(RESOURCEPATH + astrIcon));
         icons.add(icon.getImage());
      }
      m_ReaderFrame.setIconImages(icons);
   }

   private ImageIcon getDialogIcon()
   {
      return(new ImageIcon(getClass().getResource(RESOURCEPATH + "app-icon32.png")));
   }

   private void shutdown()
   {
      savePreferences();

      // this will make sure WindowListener.windowClosing() et al. will be called.
      final WindowEvent wev = new WindowEvent(m_ReaderFrame, WindowEvent.WINDOW_CLOSING);
      Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
   }

   private static String getApplicationProperty(final String strKey)
   {
      final Properties appProps = new Properties();

      String strVal;

      try (final InputStream is = TitaniumBackupMessageViewer.class.getResourceAsStream(RESOURCEPATH + "application.properties"))
      {
         appProps.load(is);
         strVal = appProps.getProperty(strKey, "");
      }
      catch (final IOException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
         strVal = "";
      }

      return(strVal);
   }

   private void initReaderFrameFromPrefs()
   {
      final int ix = PREFERENCES.getInt(windowXKey, 100);
      final int iy = PREFERENCES.getInt(windowYKey, 100);
      final int iWidth = PREFERENCES.getInt(windowWidthKey, 700);
      final int iHeight = PREFERENCES.getInt(windowHeightKey, 500);
      final int iState = PREFERENCES.getInt(windowStateKey, JFrame.NORMAL);
      m_ReaderFrame.setLocation(ix, iy);
      m_ReaderFrame.setSize(iWidth, iHeight);
      m_ReaderFrame.setExtendedState(iState);
   }

   private void savePreferences()
   {
      if (JFrame.MAXIMIZED_BOTH != m_ReaderFrame.getExtendedState())
      {
         PREFERENCES.putInt(windowXKey, m_ReaderFrame.getLocation().x);
         PREFERENCES.putInt(windowYKey, m_ReaderFrame.getLocation().y);
         PREFERENCES.putInt(windowWidthKey, m_ReaderFrame.getWidth());
         PREFERENCES.putInt(windowHeightKey, m_ReaderFrame.getHeight());
      }

      PREFERENCES.putInt(windowStateKey, m_ReaderFrame.getExtendedState());
      PREFERENCES.putBoolean(windowVisibleKey, m_ReaderFrame.isVisible());

      try
      {
         PREFERENCES.flush();
      }
      catch (final BackingStoreException ex)
      {
          LOGGER.log(Level.SEVERE, ex.toString(), ex);
      }
   }
}