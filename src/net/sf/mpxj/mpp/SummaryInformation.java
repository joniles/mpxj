/*
 * file:       SummaryInformation.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2004
 * date:       2004-12-02
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sf.mpxj.mpp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.common.NumberHelper;

import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.Section;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * This class encapsulates the functionality required to retrieve document
 * summary information from MPP files. This code is common to both the
 * MPP8 and MPP9 file formats.
 */
final class SummaryInformation
{
   /**
    * Constructor.
    *
    * @param rootDir root of the POI file system
    * @throws MPXJException
    */
   public SummaryInformation(DirectoryEntry rootDir)
      throws MPXJException
   {
      try
      {
         PropertySet ps = new PropertySet(new DocumentInputStream(((DocumentEntry) rootDir.getEntry("\005SummaryInformation"))));
         HashMap<Integer, Object> map = getPropertyMap(ps);
         m_projectTitle = (String) map.get(PROJECT_TITLE);
         m_subject = (String) map.get(SUBJECT);
         m_author = (String) map.get(AUTHOR);
         m_keywords = (String) map.get(KEYWORDS);
         m_comments = (String) map.get(COMMENTS);
         m_template = (String) map.get(TEMPLATE);
         m_projectUser = (String) map.get(PROJECT_USER);
         m_revision = NumberHelper.parseInteger((String) map.get(REVISION_NUMBER));
         m_creationDate = (Date) map.get(CREATION_DATE);
         m_lastSaved = (Date) map.get(LAST_SAVED);
         m_application = (String) map.get(APPLICATION);
         m_editingTime = (Integer) map.get(EDITING_TIME);
         m_lastPrinted = (Date) map.get(LAST_PRINTED);

         ps = new PropertySet(new DocumentInputStream(((DocumentEntry) rootDir.getEntry("\005DocumentSummaryInformation"))));
         map = getPropertyMap(ps);
         m_category = (String) map.get(CATEGORY);
         m_format = (String) map.get(FORMAT);
         m_manager = (String) map.get(MANAGER);
         m_company = (String) map.get(COMPANY);
         m_contentType = (String) map.get(CONTENT_TYPE);
         m_contentStatus = (String) map.get(CONTENT_STATUS);
         m_language = (String) map.get(LANGUAGE);
         m_documentVersion = (String) map.get(DOCUMENT_VERSION);

         m_customProperties = getCustomPropertyMap(ps);
      }

      catch (Exception ex)
      {
         throw new MPXJException(MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * This method reads the contents of a property set and returns
    * a map relating property IDs and values together.
    *
    * @param ps property set
    * @return map
    */
   private HashMap<Integer, Object> getPropertyMap(PropertySet ps)
   {
      HashMap<Integer, Object> map = new HashMap<Integer, Object>();
      Property[] properties;
      Property property;
      List<Section> sections = ps.getSections();
      int index = 100;

      for (Section section : sections)
      {
         properties = section.getProperties();
         for (int loop = 0; loop < properties.length; loop++)
         {
            property = properties[loop];
            // the following causes an "unnecessary cast" warning in JDK1.4
            // this is in place to ensure compatibility with JDK1.5
            map.put(Integer.valueOf(index + (int) property.getID()), property.getValue());
            //System.out.println ("id="+(index+property.getID())+" value="+property.getValue());
         }
         index += 100;
      }
      return (map);
   }

   /**
    * Retrieve a map of custom property names and values.
    * 
    * @param ps document summary property set
    * @return maps of custom property names and values
    */
   @SuppressWarnings("unchecked") private HashMap<String, Object> getCustomPropertyMap(PropertySet ps)
   {
      HashMap<String, Object> map = new HashMap<String, Object>();

      for (Section section : ps.getSections())
      {
         //
         // Extract the property names
         //
         Map<Long, String> names = null;
         for (Property property : section.getProperties())
         {
            //
            // If we have a key in this section, this is
            long id = property.getID();
            if (id == 0 && property.getValue() instanceof Map)
            {
               names = (Map<Long, String>) property.getValue();
               break;
            }
         }

         //
         // No key? no custom properties in this section
         //
         if (names == null)
         {
            continue;
         }

         //
         // Extract the property values
         //
         if (!names.isEmpty())
         {
            for (Property property : section.getProperties())
            {
               long id = property.getID();
               if ((id & KEY_FLAG) == 0)
               {
                  String name = names.get(Long.valueOf(id));
                  if (name != null)
                  {
                     map.put(name, property.getValue());
                  }
               }
            }
         }
      }
      return (map);
   }

   /**
    * Retrieve the author's name.
    *
    * @return author's name
    */
   public String getAuthor()
   {
      return (m_author);
   }

   /**
    * Retrieve comments.
    *
    * @return comments
    */
   public String getComments()
   {
      return (m_comments);
   }

   /**
    * Retrieve the company name.
    *
    * @return company name
    */
   public String getCompany()
   {
      return (m_company);
   }

   /**
    * Retrieve the keywords.
    *
    * @return keywords
    */
   public String getKeywords()
   {
      return (m_keywords);
   }

   /**
    * Retrieve the manager.
    *
    * @return manager
    */
   public String getManager()
   {
      return (m_manager);
   }

   /**
    * Retrieve the project title.
    *
    * @return project title
    */
   public String getProjectTitle()
   {
      return (m_projectTitle);
   }

   /**
    * Retrieve the subject.
    *
    * @return subject
    */
   public String getSubject()
   {
      return (m_subject);
   }

   /**
    * Retrieve the category text.
    *
    * @return category
    */
   public String getCategory()
   {
      return (m_category);
   }

   /**
    * Retrieve the revision number.
    *
    * @return revision number
    */
   public Integer getRevision()
   {
      return (m_revision);
   }

   /**
    * Retrieve the created date.
    * 
    * @return created date
    */
   public Date getCreationDate()
   {
      return (m_creationDate);
   }

   /**
    * Retrieve the last saved date.
    * 
    * @return last saved date
    */
   public Date getLastSaved()
   {
      return (m_lastSaved);
   }

   /**
    * Retrieve the template property.
    * 
    * @return property value
    */
   public String getTemplate()
   {
      return m_template;
   }

   /**
    * Retrieve the project user property.
    * 
    * @return property value
    */
   public String getProjectUser()
   {
      return m_projectUser;
   }

   /**
    * Retrieve the last printed property.
    * 
    * @return property value
    */
   public Date getLastPrinted()
   {
      return m_lastPrinted;
   }

   /**
    * Retrieve the application property.
    * 
    * @return property value
    */
   public String getApplication()
   {
      return m_application;
   }

   /**
    * Retrieve the editing time property.
    * 
    * @return property value
    */
   public Integer getEditingTime()
   {
      return m_editingTime;
   }

   /**
    * Retrieve the format property.
    * 
    * @return property value
    */
   public String getFormat()
   {
      return m_format;
   }

   /**
    * Retrieve the content type property.
    * 
    * @return property value
    */
   public String getContentType()
   {
      return m_contentType;
   }

   /**
    * Retrieve the content status property.
    * 
    * @return property value
    */
   public String getContentStatus()
   {
      return m_contentStatus;
   }

   /**
    * Retrieve the language property.
    * 
    * @return property value
    */
   public String getLanguage()
   {
      return m_language;
   }

   /**
    * Retrieve the document version property.
    * 
    * @return property value
    */
   public String getDocumentVersion()
   {
      return m_documentVersion;
   }

   /**
    * Retrieve a map of any names and values for any custom
    * properties held in the project file.
    *
    * @return custom properties map
    */
   public Map<String, Object> getCustomProperties()
   {
      return (m_customProperties);
   }

   private String m_projectTitle;
   private String m_subject;
   private String m_author;
   private String m_keywords;
   private String m_comments;
   private String m_template;
   private String m_projectUser;
   private Integer m_revision;
   private Date m_lastPrinted;
   private Date m_creationDate;
   private Date m_lastSaved;
   private String m_application;
   private Integer m_editingTime;

   private String m_category;
   private String m_format;
   private String m_manager;
   private String m_company;
   private String m_contentType;
   private String m_contentStatus;
   private String m_language;
   private String m_documentVersion;

   private HashMap<String, Object> m_customProperties;

   /**
    * Constants representing Summary Information properties.
    */
   private static final Integer PROJECT_TITLE = Integer.valueOf(102);
   private static final Integer SUBJECT = Integer.valueOf(103);
   private static final Integer AUTHOR = Integer.valueOf(104);
   private static final Integer KEYWORDS = Integer.valueOf(105);
   private static final Integer COMMENTS = Integer.valueOf(106);
   private static final Integer TEMPLATE = Integer.valueOf(107);
   private static final Integer PROJECT_USER = Integer.valueOf(108);
   private static final Integer REVISION_NUMBER = Integer.valueOf(109);
   private static final Integer LAST_PRINTED = Integer.valueOf(110);
   private static final Integer CREATION_DATE = Integer.valueOf(112);
   private static final Integer LAST_SAVED = Integer.valueOf(113);
   private static final Integer APPLICATION = Integer.valueOf(118);
   private static final Integer EDITING_TIME = Integer.valueOf(119);

   /**
    * Constants representing Document Summary Information properties.
    */
   private static final Integer CATEGORY = Integer.valueOf(102);
   private static final Integer FORMAT = Integer.valueOf(103);
   private static final Integer MANAGER = Integer.valueOf(114);
   private static final Integer COMPANY = Integer.valueOf(115);
   private static final Integer CONTENT_TYPE = Integer.valueOf(126);
   private static final Integer CONTENT_STATUS = Integer.valueOf(127);
   private static final Integer LANGUAGE = Integer.valueOf(128);
   private static final Integer DOCUMENT_VERSION = Integer.valueOf(129);

   private static final long KEY_FLAG = 0x1000000;
}
