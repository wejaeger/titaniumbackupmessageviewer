/*
 * $Id$
 *
 * File:   IMessageReader.java
 * Author: Werner Jaeger
 *
 * Created on Mar 8, 2015, 1:35:45 PM
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

import com.wj.android.messageviewer.message.MessageThread;
import java.io.File;
import java.io.InputStream;

/**
 * Interface definition for message readers.
 * 
 * @author <a href="mailto:werner.jaeger@t-systems.com">Werner Jaeger</a>
 */
public interface IMessageReader
{

   /**
    * Get the number of all messages in all threads.
    *
    * @return the number of messages
    */
   int getNumberOfMessages();

   /**
    * Get an array of all message threads.
    *
    * @return array of all threads. Never {@code null}.
    */
   MessageThread[] getThreadArray();

   /**
    * Reads a message file as stored from the Titanium Backup application.
    *
    * @param is the message file input stream to read from.
    *        Must not be {@code null}.
    * @param contactsDB the SQLLite contact database file or {@code null}.
    *
    * @return the error code -3 meaning contact database file not found,
    *         0 meaning success, 1 {@code is} == {@code null}, 2
    *         invalid XML and 3 other reading problems.
    */
   int loadMessages(final InputStream is, final File contactsDB);
}
