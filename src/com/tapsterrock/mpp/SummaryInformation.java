/*
 * file:       SummaryInformation.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2004
 * date:       Dec 2, 2004
 */

package com.tapsterrock.mpp;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.Section;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import com.tapsterrock.mpx.MPXException;

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
    * @throws MPXException
    */
   public SummaryInformation (DirectoryEntry rootDir)
      throws MPXException
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

         ps = new PropertySet(new DocumentInputStream (((DocumentEntry)rootDir.getEntry("\005DocumentSummaryInformation"))));
         map = getPropertyMap(ps);
         m_category = (String)map.get(CATEGORY);
         m_company = (String)map.get(COMPANY);
         m_manager = (String)map.get(MANAGER);

         //
         // I've come across some instances where the finish date is
         // a String value, apparently the duration. We may have to use
         // the label information present in the document summary in order
         // to select the correct field if we find that they are being assigned
         // different numbers in some cases.
         //
         Object o = map.get(FINISH);
         if (o instanceof Date)
         {
            m_finish = (Date)map.get(FINISH);
         }

         o = map.get(START);
         if (o instanceof Date)
         {
            m_start = (Date)map.get(START);
         }
      }

      catch (Exception ex)
      {
         throw new MPXException (MPXException.READ_ERROR, ex);
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
           map.put(new Integer(index+property.getID()), property.getValue());
           //System.out.println ("id="+(index+property.getID())+" value="+property.getValue());
         }
         index += 100;
      }
      return (map);
   }

   /**
    * Retrieve the author's name
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
    * Retrieve the project start date.
    *
    * @return start date
    */
   public Date getStartDate ()
   {
      return (m_start);
   }

   /**
    * Retrieve the project finish date.
    *
    * @return finish date
    */
   public Date getFinishDate ()
   {
      return (m_finish);
   }

   private String m_projectTitle;
   private String m_subject;
   private String m_author;
   private String m_keywords;
   private String m_comments;

   private String m_category;
   private String m_manager;
   private String m_company;
   private Date m_start;
   private Date m_finish;

   /**
    * Constants representing Summary Information properties
    */
   private static final Integer PROJECT_TITLE = new Integer (102);
   private static final Integer SUBJECT = new Integer (103);
   private static final Integer AUTHOR = new Integer (104);
   private static final Integer KEYWORDS = new Integer (105);
   private static final Integer COMMENTS = new Integer (106);

   /**
    * Constants representing Document Summary Information properties
    */
   private static final Integer CATEGORY = new Integer (102);
   private static final Integer MANAGER = new Integer (114);
   private static final Integer COMPANY = new Integer (115);
   private static final Integer CODEPAGE = new Integer (201);
   private static final Integer PERCENT_COMPLETE = new Integer (202);
   private static final Integer COST = new Integer (203);
   private static final Integer DURATION = new Integer (204);
   private static final Integer FINISH = new Integer (205);
   private static final Integer START = new Integer (206);
   private static final Integer WORK = new Integer (207);
   private static final Integer PERCENT_WORK_COMPLETE = new Integer (208);
}
