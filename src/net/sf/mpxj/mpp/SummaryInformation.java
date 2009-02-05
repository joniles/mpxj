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
import net.sf.mpxj.utility.NumberUtility;

import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.Section;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * This class encapsulates the functionlaity required to retrieve document
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
         m_revision = NumberUtility.parseInteger((String) map.get(REVISION_NUMBER));
         m_creationDate = (Date) map.get(CREATION_DATE);
         m_lastSaved = (Date) map.get(LAST_SAVED);

         ps = new PropertySet(new DocumentInputStream(((DocumentEntry) rootDir.getEntry("\005DocumentSummaryInformation"))));
         map = getPropertyMap(ps);
         m_category = (String) map.get(CATEGORY);
         m_company = (String) map.get(COMPANY);
         m_manager = (String) map.get(MANAGER);
         m_documentSummaryInformation = map;
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
   @SuppressWarnings("unchecked") private HashMap<Integer, Object> getPropertyMap(PropertySet ps)
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
    * Retrieve the Document Summary Information. This
    * allows the caller to examine custom document summary information
    * which may be defined in the project file.
    *
    * @return the Document Summary Information HashMap
    */
   public Map<Integer, Object> getDocumentSummaryInformation()
   {
      return (m_documentSummaryInformation);
   }

   private String m_projectTitle;
   private String m_subject;
   private String m_author;
   private String m_keywords;
   private String m_comments;
   private Integer m_revision;

   private String m_category;
   private String m_manager;
   private String m_company;

   private Date m_creationDate;
   private Date m_lastSaved;

   private HashMap<Integer, Object> m_documentSummaryInformation;

   /**
    * Constants representing Summary Information properties.
    */
   private static final Integer PROJECT_TITLE = Integer.valueOf(102);
   private static final Integer SUBJECT = Integer.valueOf(103);
   private static final Integer AUTHOR = Integer.valueOf(104);
   private static final Integer KEYWORDS = Integer.valueOf(105);
   private static final Integer COMMENTS = Integer.valueOf(106);
   private static final Integer REVISION_NUMBER = Integer.valueOf(109);
   private static final Integer CREATION_DATE = Integer.valueOf(112);
   private static final Integer LAST_SAVED = Integer.valueOf(113);

   /**
    * Constants representing Document Summary Information properties.
    */
   private static final Integer CATEGORY = Integer.valueOf(102);
   private static final Integer MANAGER = Integer.valueOf(114);
   private static final Integer COMPANY = Integer.valueOf(115);
   //private static final Integer CODEPAGE = Integer.valueOf(201);
}
