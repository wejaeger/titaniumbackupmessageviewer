/*
 * $Id$
 *
 * File:   Resources.java
 * Author: Werner Jaeger
 *
 * Created on Mar 8, 2015, 8:00:13 AM
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
package com.wj.android.messageviewer.resources;

import com.wj.android.messageviewer.util.IOUtils;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Singleton class to facilitate access to resources.
 *
 * @author Werner Jaeger
 */
public class Resources
{
   private static final String DEFAULTCHARSET = Charset.defaultCharset().name();

   /** 32 pixel application icon */
   public static final String APPICON32 = "app-icon32.png";
   /** 48 pixel application icon */
   public static final String APPICON48 = "app-icon48.png";
   /** 64 pixel application icon */
   public static final String APPICON64 = "app-icon64.png";
   /** 16 pixel user icon */
   public static final String USER16 = "user16.png";

   private static final String APPPROPFILENAME = "application.properties";
   private static final String SMSBACKUPXSD = "sms.xsd";
   private static final String TITANIUMBACKUPXSD = "titanium.xsd";
   private static final String APPTITLEKEY = "application.title";
   private static final String APPVERSIONKEY = "version.num";
   private static final String APPVENDORKEY = "application.vendor";
   private static final String APPHOMEPAGEKEY = "application.homepage";
   private static final String RELEASEDATEKEY = "version.dat";

   private static final Map<String, ImageIcon> ICONMAP = new HashMap<>();
   private static final Properties APPPROPS = new Properties();

   /**
    * Prevent instantiation.
    */
   private Resources()
   {
   }

   /**
    * Get an icon with specified name.
    *
    * @param strIconName the name of the icon. Use predefined constants.
    *
    * @return the icon or {@code null} if no icon with specified name exists.
    */
   public static ImageIcon getIcon(final String strIconName)
   {
      ImageIcon icon = ICONMAP.get(strIconName);

      synchronized (ICONMAP)
      {
         if (null == icon)
         {
            try
            {
               icon = new ImageIcon(Resources.class.getResource(strIconName));
               ICONMAP.put(strIconName, icon);
            }
            catch (Exception ex)
            {
               JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
         }
      }

      return(icon);
   }

   /**
    * Get a list of the frame icon of different dimensions.
    * <p>
    *    Depending on the platform capabilities one or several images of
    *    different dimensions will be used as the window's icon.
    * </p>
    *
    * @return the sequence of images to be displayed as the icon for
    *         frame windows
    */
   public static List<Image> getFrameIcons()
   {
      final String[] astrIcons = {Resources.APPICON32, Resources.APPICON48, Resources.APPICON64};
      final List<Image> icons = new ArrayList<>(3);

      for (final String astrIcon : astrIcons)
      {
         final ImageIcon icon = getIcon(astrIcon);
         icons.add(icon.getImage());
      }
      return(icons);
   }

   /**
    * Get the application name from the underlying application properties file.
    *
    * @return the application name or empty if could not be read.
    */
   public static String getApplicationTitle()
   {
      return(getProperty(APPTITLEKEY));
   }

   /**
    * Get the application version from the underlying application properties
    * file.
    *
    * @return the application version or empty if could not be read.
    */
   public static String getApplicationVersion()
   {
      return(getProperty(APPVERSIONKEY));
   }

   /**
    * Get the application vendor from the underlying application properties
    * file.
    *
    * @return the application vendor or empty if could not be read.
    */
   public static String getApplicationVendor()
   {
      return(getProperty(APPVENDORKEY));
   }

   /**
    * Get the application home page from the underlying application properties
    * file.
    *
    * @return the application home page or empty if could not be read.
    */
   public static String getApplicationHomePage()
   {
      return(getProperty(APPHOMEPAGEKEY));
   }

   /**
    * Get the release date from the underlying application properties file.
    *
    * @return the release date or empty if could not be read.
    */
   public static String getReleaseDate()
   {
      return(getProperty(RELEASEDATEKEY));
   }

   /**
    * Get the XSD for validating SMS Backup and Restore
    * message XML files.
    *
    * @return the XSD {@code null} if schema is not found.
    */
   public static String getSMSBackaupSchema()
   {
      return(getSchema(SMSBACKUPXSD));
   }

   /**
    * Get the XSD for validating Titanium Backup
    * message XML files.
    *
    * @return the XSD or {@code null} if schema is not found.
    */
   public static String getTitaniumBackaupSchema()
   {
      return(getSchema(TITANIUMBACKUPXSD));
   }

   private static String getProperty(final String strKey)
   {
      String strProp = APPPROPS.getProperty(strKey, "");

      if (strProp.isEmpty())
      {
         if (loadProperties())
             strProp = APPPROPS.getProperty(strKey, "");
      }

      return(strProp);
   }

   private static boolean loadProperties()
   {
      boolean fRet = false;

      try (final InputStream is = Resources.class.getResourceAsStream(APPPROPFILENAME))
      {
         APPPROPS.load(is);
         fRet = true;
      }
      catch (final IOException ex)
      {
         JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }

      return(fRet);
   }

   private static String getSchema(final String strSchemaName)
   {
      String strRet = null;

      try (final InputStream is = Resources.class.getResourceAsStream(strSchemaName))
      {
         final StringWriter writer = new StringWriter();
         IOUtils.copy(is, writer, DEFAULTCHARSET);
         strRet = writer.toString();

      }
      catch (final IOException ex)
      {
         JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }

      return(strRet);
   }

}
