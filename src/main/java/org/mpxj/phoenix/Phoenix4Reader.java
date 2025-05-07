/*
 * file:       Phoenix4Reader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28 November 2015
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

package org.mpxj.phoenix;

import java.io.InputStream;

import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mpxj.common.LocalDateTimeHelper;
import org.xml.sax.SAXException;

import org.mpxj.MPXJException;
import org.mpxj.ProjectFile;
import org.mpxj.common.UnmarshalHelper;
import org.mpxj.phoenix.schema.phoenix4.Project;
import org.mpxj.phoenix.schema.phoenix4.Project.Storepoints.Storepoint;
import org.mpxj.reader.AbstractProjectStreamReader;

/**
 * This class creates a new ProjectFile instance by reading a Phoenix Project Manager file.
 */
final class Phoenix4Reader extends AbstractProjectStreamReader
{
   public Phoenix4Reader(boolean useActivityCodesForTaskHierarchy)
   {
      m_useActivityCodesForTaskHierarchy = useActivityCodesForTaskHierarchy;
   }

   @Override public ProjectFile read(InputStream stream) throws MPXJException
   {
      try
      {
         if (CONTEXT == null)
         {
            throw CONTEXT_EXCEPTION;
         }

         Project phoenixProject = (Project) UnmarshalHelper.unmarshal(CONTEXT, new SkipNulInputStream(stream));
         Project.Layouts.Layout activeLayout = getActiveLayout(phoenixProject);
         Storepoint storepoint = getCurrentStorepoint(phoenixProject);

         return new Phoenix4ProjectReader(m_useActivityCodesForTaskHierarchy).read(phoenixProject, activeLayout, storepoint);
      }

      catch (ParserConfigurationException | SAXException | JAXBException ex)
      {
         throw new MPXJException("Failed to parse file", ex);
      }
   }

   /**
    * Retrieve the most recent storepoint.
    *
    * @param phoenixProject project data
    * @return Storepoint instance
    */
   private Storepoint getCurrentStorepoint(Project phoenixProject)
   {
      List<Storepoint> storepoints = phoenixProject.getStorepoints().getStorepoint();
      storepoints.sort((o1, o2) -> LocalDateTimeHelper.compare(o2.getCreationTime(), o1.getCreationTime()));
      return storepoints.get(0);
   }

   /**
    * Find the current active layout.
    *
    * @param phoenixProject phoenix project data
    * @return current active layout
    */
   private Project.Layouts.Layout getActiveLayout(Project phoenixProject)
   {
      //
      // Start with the first layout we find
      //
      Project.Layouts.Layout activeLayout = phoenixProject.getLayouts().getLayout().get(0);

      //
      // If this isn't active, find one which is... and if none are,
      // we'll just use the first.
      //
      if (!activeLayout.isActive().booleanValue())
      {
         for (Project.Layouts.Layout layout : phoenixProject.getLayouts().getLayout())
         {
            if (layout.isActive().booleanValue())
            {
               activeLayout = layout;
               break;
            }
         }
      }

      return activeLayout;
   }

   private final boolean m_useActivityCodesForTaskHierarchy;

   /**
    * Cached context to minimise construction cost.
    */
   private static JAXBContext CONTEXT;

   /**
    * Note any error occurring during context construction.
    */
   private static JAXBException CONTEXT_EXCEPTION;

   static
   {
      try
      {
         //
         // JAXB RI property to speed up construction
         //
         System.setProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");

         //
         // Construct the context
         //
         CONTEXT = JAXBContext.newInstance("org.mpxj.phoenix.schema.phoenix4", Phoenix4Reader.class.getClassLoader());
      }

      catch (JAXBException ex)
      {
         CONTEXT_EXCEPTION = ex;
         CONTEXT = null;
      }
   }
}
