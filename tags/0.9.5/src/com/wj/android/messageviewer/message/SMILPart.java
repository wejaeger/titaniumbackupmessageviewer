/*
 * $Id$
 *
 * File:   SMILPart.java
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
package com.wj.android.messageviewer.message;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class represents a "application/smil" part.
 *
 * @author Werner Jaeger
 */
public class SMILPart extends GenericMessagePart
{
   private static final long serialVersionUID = -4062949269796069080L;
   private static final Logger LOGGER = Logger.getLogger(SMILPart.class.getName());
   private static final String PARTAGNAME = "par";
   private static final String SRCATTRNAME = "src";

   private transient Document m_SMILDocument;
   private final List<String> m_ContentReferences;

   /**
    * Creates new {@code SMILPart}.
    *
    * @param contentType the type of the content (e.g. "text/plain")
    * @param strContentId the content identifier. Must not be {@code null}.
    * @param strContentLocation the content location
    * @param abContent the content
    * @param strCharset the character set of text content
    */
   protected SMILPart(final ContentType contentType, final String strContentId, final String strContentLocation, final byte[] abContent, final String strCharset)
   {
      super(contentType, strContentId, strContentLocation, abContent, strCharset);

      m_SMILDocument = null;
      m_ContentReferences = new ArrayList<>();
   }

   /**
    * Gets the content location.
    *
    * @return the content location.
    */
   @Override
   public String getText()
   {
      return(getContentLocation());
   }

   /**
    * Retrieve content references in the order defined by SMIL document.
    *
    * <p>
    *    Content is referenced by the {@code src} attribute of {@code par}
    *    child elements. If the reference is prefixed by {@code cid;} the
    *    reference referred to is the content identifier, otherwise it is the
    *    content location.
    * </p>
    *
    * @return collection of ordered content identifiers. Never {@code null}.
    */
   public Collection<String> contentReferences()
   {
      if (null == m_SMILDocument)
         m_SMILDocument = readSmilDocument();

      if (null != m_SMILDocument && m_ContentReferences.isEmpty())
      {
         final NodeList pars = m_SMILDocument.getElementsByTagName(PARTAGNAME);
         for (int i = 0; i < pars.getLength(); i++)
         {
            if (Node.ELEMENT_NODE == pars.item(i).getNodeType())
            {
               final Element parElement = (Element)pars.item(i);
               if (null != parElement)
               {
                  final NodeList childs = parElement.getChildNodes();
                  for (int j = 0; j < childs.getLength(); j++)
                  {
                     if (Node.ELEMENT_NODE == childs.item(j).getNodeType())
                     {
                        final Element childElement = (Element)childs.item(j);
                        if (null != childElement)
                        {
                           final String strSrc = childElement.getAttribute(SRCATTRNAME);
                           if (!strSrc.trim().isEmpty())
                              m_ContentReferences.add(strSrc);
                        }
                     }
//                     else
//                        LOGGER.log(Level.WARNING, "Unexpected non par child ELEMENT_NODE {0}", childs.item(j).getNodeName());
                  }
               }
            }
            else
               LOGGER.log(Level.WARNING, "Unexpected non par ELEMENT_NODE {0}", pars.item(i).getNodeName());
         }
      }

      return(m_ContentReferences);
   }

   /**
    * Read the content bytes into a dom document.
    *
    * @return a dom document representation of the content bytes.
    */
   private Document readSmilDocument()
   {
      Document doc;

      try
      {
         final Reader reader = new StringReader(new String(m_abContent, getCharSet()));
         final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         doc = dBuilder.parse(new InputSource(reader));
         doc.getDocumentElement().normalize();
      }
      catch (final SAXException | IOException | ParserConfigurationException ex)
      {
         LOGGER.log(Level.SEVERE, ex.toString(), ex);
         doc = null;
      }
      return(doc);
   }
}
