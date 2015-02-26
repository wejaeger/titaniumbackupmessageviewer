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
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
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
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
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
   private JFrame m_ReaderFrame;
   private JTextField m_MessageFileLocationField;
   private JTextField m_ContactsDBFileLocationField;
   private JTextField m_NumberSMSField;
   private JTextField m_ExportFileField;
   private MessageViewer m_MessageViewer;
   private JList<MessageThread> m_ThreadListBox;
   private JFileChooser m_MessageFileChooser;
   private JFileChooser m_ContactsDatabaseChooser;
   private JFileChooser m_SaveChooser;
   private AndroidMessageReader m_Reader;

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
    *               file to open at start up.
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
      m_ReaderFrame.setTitle("Android Message Reader");
      m_ReaderFrame.setBounds(100, 100, 700, 550);
      m_ReaderFrame.setDefaultCloseOperation(3);

      m_MessageFileChooser = new JFileChooser();
      m_MessageFileChooser.setFileFilter(new TitaniumBackupMessageFileNameFilter());

      m_ContactsDatabaseChooser = new JFileChooser();
      m_ContactsDatabaseChooser.setFileFilter(new TitaniumBackupContactsFileNameFilter());

      m_SaveChooser = new JFileChooser();

      final JMenuBar menuBar = new JMenuBar();
      m_ReaderFrame.setJMenuBar(menuBar);

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

      m_MessageFileLocationField = new JTextField();
      m_MessageFileLocationField.setEditable(false);

      if (null != strMessageFileName && !strMessageFileName.trim().isEmpty())
         m_MessageFileLocationField.setText(strMessageFileName);
      else
         m_MessageFileLocationField.setText("...");

      m_MessageFileLocationField.setColumns(10);

      m_ContactsDBFileLocationField = new JTextField();
      m_ContactsDBFileLocationField.setEditable(false);

      if (null != strContactsDBFileName && !strContactsDBFileName.trim().isEmpty())
         m_ContactsDBFileLocationField.setText(strContactsDBFileName);
      else
         m_ContactsDBFileLocationField.setText("...");

      m_ContactsDBFileLocationField.setColumns(10);

      final JButton chooseMessageFileButton = new JButton("Choose Message File");
      chooseMessageFileButton.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            chooseMessageFileAction();
         }
      });
     chooseMessageFileButton.setToolTipText("You can open the commpressd (.gz) or plain (.xml) message file");

      final JButton chooseContactsDBButton = new JButton("Choose Contacts Database file");
      chooseContactsDBButton.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            chooseContactsDBAction();
         }
      });
      chooseContactsDBButton.setToolTipText("You can open the archived (.tar.gz) or the plain (.db) contacts database");

      final JLabel lblNumberOfSms = new JLabel("Number of SMS:");

      m_NumberSMSField = new JTextField();
      m_NumberSMSField.setEditable(false);
      m_NumberSMSField.setColumns(10);

      final JButton loadButton = new JButton("Load!");
      loadButton.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            loadAction();
         }
      });
      loadButton.setToolTipText("Hit this button to actually load the choosen message file and the optionally choosen contacts database");

      final JScrollPane scrollPane_1 = new JScrollPane();
      final JScrollPane scrollPane = new JScrollPane();
      scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

      final JLabel lblNewLabel = new JLabel("Use the options below to export the SMS to a text file. You can either export the selected thread, or all:");
      lblNewLabel.setFont(new Font("Tahoma", 0, 12));

      m_ExportFileField = new JTextField();
      m_ExportFileField.setText("...");
      m_ExportFileField.setEditable(false);
      m_ExportFileField.setColumns(10);

      final JButton exportSelectedButton = new JButton("Export Selected");
      exportSelectedButton.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            exportSelectedAction();
         }
      });

      final JButton exportButton = new JButton("Export All");
      exportButton.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(final ActionEvent evt)
         {
            exportAllAction();
         }
      });

      final GroupLayout groupLayout = new GroupLayout(m_ReaderFrame.getContentPane());
      groupLayout.setHorizontalGroup(
              groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
              .addGroup(groupLayout.createSequentialGroup()
                      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                              .addGroup(groupLayout.createSequentialGroup()
                                      .addGap(7)
                                      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                              .addGroup(GroupLayout.Alignment.TRAILING, groupLayout.createSequentialGroup()
                                                      .addComponent(m_MessageFileLocationField, GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                      .addComponent(chooseMessageFileButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                                              .addGroup(GroupLayout.Alignment.TRAILING, groupLayout.createSequentialGroup()
                                                      .addComponent(m_ContactsDBFileLocationField, GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                      .addComponent(chooseContactsDBButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                                              .addGroup(groupLayout.createSequentialGroup()
                                                      .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                      .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))))
                              .addGroup(groupLayout.createSequentialGroup()
                                      .addContainerGap()
                                      .addComponent(lblNumberOfSms)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(m_NumberSMSField, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 444, Short.MAX_VALUE)
                                      .addComponent(loadButton, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                      .addGap(5))
              .addGroup(groupLayout.createSequentialGroup()
                      .addContainerGap()
                      .addComponent(m_ExportFileField, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                      .addComponent(exportSelectedButton)
                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                      .addComponent(exportButton)
                      .addContainerGap())
              .addGroup(groupLayout.createSequentialGroup()
                      .addContainerGap()
                      .addComponent(lblNewLabel)
                      .addContainerGap(129, Short.MAX_VALUE)));

      groupLayout.setVerticalGroup(
              groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
              .addGroup(groupLayout.createSequentialGroup()
                      .addGap(10)
                      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(m_MessageFileLocationField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                              .addComponent(chooseMessageFileButton))
                      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(m_ContactsDBFileLocationField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                              .addComponent(chooseContactsDBButton))
                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(lblNumberOfSms)
                              .addComponent(m_NumberSMSField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                              .addComponent(loadButton))
                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                              .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                              .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                      .addComponent(lblNewLabel)
                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                      .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                              .addComponent(m_ExportFileField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                              .addComponent(exportButton)
                              .addComponent(exportSelectedButton))
                      .addContainerGap()));

      m_MessageViewer = new MessageViewer();
      scrollPane.setViewportView(m_MessageViewer);
      scrollPane.getVerticalScrollBar().setUnitIncrement(10);

      m_ThreadListBox = new JList<>();
      m_ThreadListBox.setFont(new Font("Arial Unicode MS", 0, 12));
      m_ThreadListBox.addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(final ListSelectionEvent evt)
         {
            threadListValueChanged(evt);
         }
      });

      scrollPane_1.setViewportView(m_ThreadListBox);
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
      m_ReaderFrame.getContentPane().setLayout(groupLayout);

      if (null != strMessageFileName)
         loadAction();
   }

   private void chooseMessageFileAction()
   {
      final int iRet = m_MessageFileChooser.showOpenDialog(m_ReaderFrame);

      if (iRet == 0)
         m_MessageFileLocationField.setText(m_MessageFileChooser.getSelectedFile().getAbsolutePath());
   }

   private void chooseContactsDBAction()
   {
      final int iRet = m_ContactsDatabaseChooser.showOpenDialog(m_ReaderFrame);

      if (iRet == 0)
         m_ContactsDBFileLocationField.setText(m_ContactsDatabaseChooser.getSelectedFile().getAbsolutePath());
   }

   private void loadAction()
   {
      if (!m_MessageFileLocationField.getText().equals("..."))
      {
         InputStream is = null;
         int iRet = 0;
         String strErrorMessage = "Failed to load messages!\n";

         try
         {
            final String strMessageFilePath = m_MessageFileLocationField.getText();
            final File messageFile = new File(strMessageFilePath);

            if (strMessageFilePath.endsWith(".gz"))
               is = new GZIPInputStream(new FileInputStream(messageFile));
            else
               is = new FileInputStream(messageFile);

            final File contactsDBFile;
            final String strContactsDBFileName = m_ContactsDBFileLocationField.getText();
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
            }
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
//               m_MessageViewer.invalidate();;
            }
         });
      }
   }

   private void helpAction()
   {
   }

   private void aboutAction()
   {
      JOptionPane.showMessageDialog(m_ReaderFrame, "Android Message Reader\nv0.9.1 - 2015-02-26\nBy Werner Jäger");
   }

   private void exportSelectedAction()
   {
      if ((!m_MessageFileLocationField.getText().equals("...")) && (m_ThreadListBox.isEnabled()))
      {
         if (m_ThreadListBox.getSelectedIndex() == -1)
         {
            JOptionPane.showMessageDialog(m_ReaderFrame, "Select a thread first!");
         }
         else
         {
            m_SaveChooser.setDialogTitle("Choose a file to save as...");
            m_SaveChooser.setDialogType(1);
            m_SaveChooser.setSelectedFile(new File("AndroidSelectedMessagesExport.txt"));

            final int iRet = m_SaveChooser.showSaveDialog(m_ReaderFrame);
            if (iRet == 0)
            {
               final File saveFile = m_SaveChooser.getSelectedFile();
               m_ExportFileField.setText(saveFile.getAbsolutePath());

               if (m_Reader.exportThreadMessages(saveFile, m_ThreadListBox.getSelectedIndex()))
               {
                  JOptionPane.showMessageDialog(m_ReaderFrame, "Messages with selected threads exported successfully!");
               }
               else
               {
                  JOptionPane.showMessageDialog(m_ReaderFrame, "Failed to export selected messages!");
               }
            }
         }
      }
      else
         JOptionPane.showMessageDialog(m_ReaderFrame, "Load messages first!");
   }

   private void exportAllAction()
   {
      if ((!m_MessageFileLocationField.getText().equals("...")) && (m_ThreadListBox.isEnabled()))
      {
         m_SaveChooser.setDialogTitle("Choose a file to save as...");
         m_SaveChooser.setDialogType(1);
         m_SaveChooser.setSelectedFile(new File("AndroidAllMessagesExport.txt"));

         final int iRet = m_SaveChooser.showSaveDialog(m_ReaderFrame);
         if (iRet == 0)
         {
            final File saveFile = m_SaveChooser.getSelectedFile();
            m_ExportFileField.setText(saveFile.getAbsolutePath());

            if (m_Reader.exportAllMessages(saveFile))
            {
               JOptionPane.showMessageDialog(m_ReaderFrame, "All messages exported successfully!");
            }
            else
               JOptionPane.showMessageDialog(m_ReaderFrame, "Failed to export all messages!");
         }
      }
      else
      {
         JOptionPane.showMessageDialog(m_ReaderFrame, "Load messages first!");
      }
   }
}