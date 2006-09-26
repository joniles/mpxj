/*
 * file:       SummaryInformation.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2004
 * date:       Dec 2, 2004
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

import java.util.HashMap;
import java.util.Iterator;
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
   public SummaryInformation (DirectoryEntry rootDir)
      throws MPXJException
   {
      try
      {
         PropertySet ps = new PropertySet(new DocumentInputStream (((DocumentEntry)rootDir.getEntry("\005SummaryInformation"))));
         HashMap map = getPropertyMap(ps);
         m_projectTitle = (String)map.get(PROJECT_TITLE);
         m_subject = (String)map.get(SUBJECT);
         m_author = (String)map.get(AUTHOR);
         m_keywords = (String)map.get(KEYWORDS);
         m_comments = (String)map.get(COMMENTS);
         m_revision = NumberUtility.parseInteger((String)map.get(REVISION_NUMBER));

         ps = new PropertySet(new DocumentInputStream (((DocumentEntry)rootDir.getEntry("\005DocumentSummaryInformation"))));
         map = getPropertyMap(ps);
         m_category = (String)map.get(CATEGORY);
         m_company = (String)map.get(COMPANY);
         m_manager = (String)map.get(MANAGER);
         m_documentSummaryInformation = map;
      }

      catch (Exception ex)
      {
         throw new MPXJException (MPXJException.READ_ERROR, ex);
      }
   }

   /**
    * This method reads the contents of a property set and returns
    * a map relating property IDs and values together.
    *
    * @param ps property set
    * @return map
    */
   private HashMap getPropertyMap (PropertySet ps)
   {
      HashMap map = new HashMap();
      Property[] properties;
      Property property;
      List sections = ps.getSections();
      Iterator iter = sections.iterator();
      Section section;
      int index = 100;

      while (iter.hasNext() == true)
      {
         section = (Section)iter.next();
         properties = section.getProperties();
         for (int loop=0; loop < properties.length; loop++)
         {
           property = properties[loop];
           // the following casuses an "unnecessary cast" warning in JDK1.4
           // this is in place to ensure compatibility with JDK1.5
           map.put(new Integer(index+(int)property.getID()), property.getValue());
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
   public String getCategory ()
   {
      return (m_category);
   }

   /**
    * Retrieve the revision number.
    *
    * @return revision number
    */
   public Integer getRevision ()
   {
      return (m_revision);
   }

   /**
    * Retrive the Document Summary Information. This
    * allows the caller to examine custom document summary information
    * which may be defined in the project file.
    *
    * @return the Document Summary Information HashMap
    */
   public Map getDocumentSummaryInformation()
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

   private HashMap m_documentSummaryInformation;

   /**
    * Constants representing Summary Information properties.
    */
   private static final Integer PROJECT_TITLE = new Integer (102);
   private static final Integer SUBJECT = new Integer (103);
   private static final Integer AUTHOR = new Integer (104);
   private static final Integer KEYWORDS = new Integer (105);
   private static final Integer COMMENTS = new Integer (106);
   private static final Integer REVISION_NUMBER = new Integer (109);

   /**
    * Constants representing Document Summary Information properties.
    */
   private static final Integer CATEGORY = new Integer (102);
   private static final Integer MANAGER = new Integer (114);
   private static final Integer COMPANY = new Integer (115);
   //private static final Integer CODEPAGE = new Integer (201);
}
