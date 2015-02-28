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

import com.wj.android.messageviewer.message.IMessage;
import com.wj.android.messageviewer.io.AndroidMessageReader;
import com.wj.android.messageviewer.message.MessageThread;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
public class TitaniumBackupMessageViewer
{
   final private static String RESOURCEPATH = "/com/wj/android/messageviewer/resources/";

   // various keys for application.properties
   private static final String VERSION = "version.num";
   private static final String DATE = "version.dat";
   private static final String TITLE = "project.name";

   private JFrame m_ReaderFrame;
   private JTextField m_NumberSMSField;
   private JFileChooser m_MessageFileChooser;
   private JFileChooser m_ContactsDatabaseChooser;
   private JFileChooser m_SaveChooser;
   private JScrollPane m_ScrollPaneThread;
   private JScrollPane m_ScrollPaneMessages;
   private JList<MessageThread> m_ThreadListBox;
   private MessageViewer m_MessageViewer;
   private AndroidMessageReader m_Reader;
   private String m_strMessageFileLocation;
   private String m_strContactsDBFileLocation;

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
      initialize(strMessageFileName, strContactsDBFileName);
   }

   /**
    * Launch the application and display the main window.
    *
    * @param asArgs the first optional argument is the full path to the message
    *               file to open at start up., the second optional argument is
    *               the full path to a contacts database file.
    */
   public static void main(final String[] asArgs)
   {
      EventQueue.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            if (asArgs.length == 1)
               new TitaniumBackupMessageViewer(asArgs[0], null).m_ReaderFrame.setVisible(true);
            else if (asArgs.length == 2)
               new TitaniumBackupMessageViewer(asArgs[0], asArgs[1]).m_ReaderFrame.setVisible(true);
            else if (asArgs.length == 0)
               new TitaniumBackupMessageViewer(null, null).m_ReaderFrame.setVisible(true);
         }
      });
   }

   /**
    * This method is called from within the constructor to initialize the form.
    */
   @SuppressWarnings("unchecked")
   private void initialize(final String strMessageFileName, final String strContactsDBFileName)
   {
      m_Reader = new AndroidMessageReader();

      m_ReaderFrame = new JFrame();
      m_ReaderFrame.setTitle(getApplicationProperty(TITLE));
      m_ReaderFrame.setBounds(100, 100, 700, 550);
      m_ReaderFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      final JMenuBar menuBar = new JMenuBar();
      m_ReaderFrame.setJMenuBar(menuBar);

      setFrameIcon();

      m_MessageFileChooser = new JFileChooser();
      m_MessageFileChooser.setDialogTitle("Open message file");
      m_MessageFileChooser.setFileFilter(new TitaniumBackupMessageFileNameFilter());
      m_MessageFileChooser.setApproveButtonToolTipText("You can open the commpressd (.gz) or plain (.xml) message file");


      m_ContactsDatabaseChooser = new JFileChooser();
      m_ContactsDatabaseChooser.setDialogTitle("Open contacts database");
      m_ContactsDatabaseChooser.setFileFilter(new TitaniumBackupContactsFileNameFilter());
      m_ContactsDatabaseChooser.setApproveButtonToolTipText("You can open the archived (.tar.gz) or the plain (.db) contacts database");

      m_SaveChooser = new JFileChooser();

      final JMenu mnFile = new JMenu("File");
      menuBar.add(mnFile);

      final JMenuItem mntmOpen = new JMenuItem("Open ...");
      mntmOpen.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            chooseMessageFileAction();
         }
      });
      mnFile.add(mntmOpen);

      final JMenuItem mntmExportSelected = new JMenuItem("Export selected messages ...");
      mntmExportSelected.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            exportSelectedAction();
         }
      });
      mnFile.add(mntmExportSelected);

      final JMenuItem mntmExportAll = new JMenuItem("Export all messages ...");
      mntmExportAll.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            exportAllAction();
         }
      });
      mnFile.add(mntmExportAll);


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

      final JMenuItem mntmAboutAndroidMessageReader = new JMenuItem("About Android Message Reader");
      mntmAboutAndroidMessageReader.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            aboutAction();
         }
      });
      mnHelp.add(mntmAboutAndroidMessageReader);

      if (null != strMessageFileName && !strMessageFileName.trim().isEmpty())
         m_strMessageFileLocation = strMessageFileName;
      else
         m_strMessageFileLocation = "...";

      if (null != strContactsDBFileName && !strContactsDBFileName.trim().isEmpty())
         m_strContactsDBFileLocation = strContactsDBFileName;
      else
         m_strContactsDBFileLocation = "...";

      final JLabel lblNumberOfSms = new JLabel("Number of SMS:");

      m_NumberSMSField = new JTextField();
      m_NumberSMSField.setEditable(false);
      m_NumberSMSField.setColumns(10);
      m_NumberSMSField.setBorder(BorderFactory.createEmptyBorder());

      m_MessageViewer = new MessageViewer();
      m_ScrollPaneMessages = new JScrollPane(m_MessageViewer);
      m_ScrollPaneMessages.getVerticalScrollBar().setUnitIncrement(10);

      m_ThreadListBox = new MessageThreadList(300);
      m_ThreadListBox.setFont(new Font("Arial Unicode MS", 0, 12));
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
      m_ThreadListBox.setModel(new AbstractListModel<MessageThread>()
      {
         final MessageThread thread = new MessageThread("Threads load here...", "");

         @Override
         public int getSize()
         {
            return(1);
         }

         @Override
         public MessageThread getElementAt(int iIndex)
         {
            return(thread);
         }
      });

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

   private void chooseMessageFileAction()
   {
      final int iRet = m_MessageFileChooser.showOpenDialog(m_ReaderFrame);

      if (JFileChooser.APPROVE_OPTION == iRet)
      {
         m_strMessageFileLocation = m_MessageFileChooser.getSelectedFile().getAbsolutePath();
         chooseContactsDBAction();
      }
   }

   private void chooseContactsDBAction()
   {
      m_ContactsDatabaseChooser.setCurrentDirectory(m_MessageFileChooser.getCurrentDirectory());

      final int iRet = m_ContactsDatabaseChooser.showOpenDialog(m_ReaderFrame);

      if (JFileChooser.APPROVE_OPTION == iRet)
      {
         m_strContactsDBFileLocation = m_ContactsDatabaseChooser.getSelectedFile().getAbsolutePath();
         loadAction();
      }
   }

   private void loadAction()
   {
      if (!m_strMessageFileLocation.equals("..."))
      {
         InputStream is = null;
         int iRet = 0;
         String strErrorMessage = "Failed to load messages!\n";

         try
         {
            final File messageFile = new File(m_strMessageFileLocation);

            if (m_strMessageFileLocation.endsWith(".gz"))
               is = new GZIPInputStream(new FileInputStream(messageFile));
            else
               is = new FileInputStream(messageFile);

            final File contactsDBFile;
            final String strContactsDBFileName = m_strContactsDBFileLocation;
            if (null != strContactsDBFileName && !strContactsDBFileName.trim().isEmpty() && !strContactsDBFileName.equals("..."))
               contactsDBFile= new File(strContactsDBFileName);
            else
               contactsDBFile = null;

            iRet = m_Reader.loadMessages(is, contactsDBFile);

            if (iRet != 0)
            {
               switch (iRet)
               {
                  case 1:
                     strErrorMessage = strErrorMessage + "Error Code " + iRet + ": Time offset invalid!\n";
                     break;
                  case 2:
                     strErrorMessage = strErrorMessage + "Error Code " + iRet + ": Invalid Android Message file!\n";
                     break;
                  case 3:
                     strErrorMessage = strErrorMessage + "Error Code " + iRet + ": Problem reading the file!\n";
                     break;
                  default:
                     strErrorMessage = strErrorMessage + "Error Code " + iRet + ": Unknown error!\n";
               }
            }
            else
            {
               m_ThreadListBox.clearSelection();
               m_ThreadListBox.setListData(m_Reader.getThreadArray());
               m_ThreadListBox.setEnabled(true);
               m_NumberSMSField.setText(Integer.toString(m_Reader.getNumberOfSMS()));
               m_MessageViewer.clear();

               if (0 < m_Reader.getNumberOfSMS())
                  m_ThreadListBox.setSelectedIndex(0);
            }
            m_ScrollPaneThread.setPreferredSize(m_ThreadListBox.getPreferredSize());
       }
         catch (FileNotFoundException ex)
         {
            iRet = -1;
            strErrorMessage = strErrorMessage + "Error Code " + iRet + ": Problem file not found!\n";
         }
         catch (IOException ex)
         {

            strErrorMessage = strErrorMessage + "Error Code " + iRet + ": Problem reading file!\n";
         }
         finally
         {
            try
            {
               if (is != null)
                  is.close();
            }
            catch (IOException ex)
            {
              strErrorMessage = strErrorMessage + "Error Code " + -2 + ": Problem closing the file!\n";
            }
         }

         if (iRet != 0)
            JOptionPane.showMessageDialog(m_ReaderFrame, strErrorMessage);
      }
      else
         JOptionPane.showMessageDialog(m_ReaderFrame, "Choose Android message file first!");
   }

   private void threadListValueChanged(final ListSelectionEvent evt)
   {
      final MessageThread selectedThread = m_ThreadListBox.getSelectedValue();

      if (selectedThread != null && !evt.getValueIsAdjusting())
      {
         final Collection<IMessage> selectedMessages = selectedThread.getMessages();
         m_MessageViewer.clear();
         m_MessageViewer.setScroll(false);

         for (IMessage selectedMessage : selectedMessages)
            m_MessageViewer.appendMessage(selectedMessage);

         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               m_MessageViewer.setScroll(true);
               m_MessageViewer.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
            }
         });
      }
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
      textPane.addHyperlinkListener(new HyperlinkListener()
      {
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
                     ;
                  }
               }
            }
         }
      });

      JOptionPane.showMessageDialog(m_ReaderFrame, textPane, "About", JOptionPane.INFORMATION_MESSAGE, getDialogIcon());
   }

   private void exportSelectedAction()
   {
      if ((!m_strMessageFileLocation.equals("...")) && (m_ThreadListBox.isEnabled()))
      {
         if (m_ThreadListBox.getSelectedIndex() == -1)
         {
            JOptionPane.showMessageDialog(m_ReaderFrame, "Select a thread first!");
         }
         else
         {
            m_SaveChooser.setDialogTitle("Choose a file to save as...");
            m_SaveChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            m_SaveChooser.setSelectedFile(new File("AndroidSelectedMessagesExport.txt"));

            final int iRet = m_SaveChooser.showSaveDialog(m_ReaderFrame);
            if (iRet == 0)
            {
               if (m_Reader.exportThreadMessages(m_SaveChooser.getSelectedFile(), m_ThreadListBox.getSelectedIndex()))
                  JOptionPane.showMessageDialog(m_ReaderFrame, "Messages with selected threads exported successfully!");
               else
                  JOptionPane.showMessageDialog(m_ReaderFrame, "Failed to export selected messages!");
            }
         }
      }
      else
         JOptionPane.showMessageDialog(m_ReaderFrame, "Load messages first!");
   }

   private void exportAllAction()
   {
      if ((!m_strMessageFileLocation.equals("...")) && (m_ThreadListBox.isEnabled()))
      {
         m_SaveChooser.setDialogTitle("Choose a file to save as...");
         m_SaveChooser.setDialogType(JFileChooser.SAVE_DIALOG);
         m_SaveChooser.setSelectedFile(new File("AndroidAllMessagesExport.txt"));

         final int iRet = m_SaveChooser.showSaveDialog(m_ReaderFrame);
         if (iRet == 0)
         {
            if (m_Reader.exportAllMessages(m_SaveChooser.getSelectedFile()))
               JOptionPane.showMessageDialog(m_ReaderFrame, "All messages exported successfully!");
            else
               JOptionPane.showMessageDialog(m_ReaderFrame, "Failed to export all messages!");
         }
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

   private static String getApplicationProperty(final String strKey)
   {
      final Properties appProps = new Properties();

      String strVal;

      try
      {
         appProps.load(TitaniumBackupMessageViewer.class.getResourceAsStream(RESOURCEPATH + "application.properties"));
         strVal = appProps.getProperty(strKey, "");
      }
      catch (final IOException e)
      {
         strVal = "";
      }

      return(strVal);
   }
}