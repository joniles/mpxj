/*
 * file:       FilterReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2006
 * date:       2006-10-31
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

import java.util.LinkedList;
import java.util.List;

import net.sf.mpxj.Filter;
import net.sf.mpxj.FilterContainer;
import net.sf.mpxj.GenericCriteriaPrompt;
import net.sf.mpxj.ProjectProperties;

/**
 * This class allows filter definitions to be read from an MPP file.
 */
public abstract class FilterReader
{
   /**
    * Retrieves the type used for the VarData lookup.
    *
    * @return VarData type
    */
   protected abstract Integer getVarDataType();

   /**
    * Retrieves the criteria reader used for this filter.
    *
    * @return criteria reader
    */
   protected abstract CriteriaReader getCriteriaReader();

   /**
    * Entry point for processing filter definitions.
    *
    * @param properties project properties
    * @param filters project filters
    * @param fixedData filter fixed data
    * @param varData filter var data
    */
   public void process(ProjectProperties properties, FilterContainer filters, FixedData fixedData, Var2Data varData)
   {
      int filterCount = fixedData.getItemCount();
      boolean[] criteriaType = new boolean[2];
      CriteriaReader criteriaReader = getCriteriaReader();

      for (int filterLoop = 0; filterLoop < filterCount; filterLoop++)
      {
         byte[] filterFixedData = fixedData.getByteArrayValue(filterLoop);
         if (filterFixedData == null || filterFixedData.length < 4)
         {
            continue;
         }

         Filter filter = new Filter();
         filter.setID(Integer.valueOf(MPPUtility.getInt(filterFixedData, 0)));
         filter.setName(MPPUtility.removeAmpersands(MPPUtility.getUnicodeString(filterFixedData, 4)));
         byte[] filterVarData = varData.getByteArray(filter.getID(), getVarDataType());
         if (filterVarData == null)
         {
            continue;
         }

         //System.out.println(MPPUtility.hexdump(filterVarData, true, 16, ""));
         List<GenericCriteriaPrompt> prompts = new LinkedList<GenericCriteriaPrompt>();

         filter.setShowRelatedSummaryRows(MPPUtility.getByte(filterVarData, 4) != 0);
         filter.setCriteria(criteriaReader.process(properties, filterVarData, 0, -1, prompts, null, criteriaType));

         filter.setIsTaskFilter(criteriaType[0]);
         filter.setIsResourceFilter(criteriaType[1]);
         filter.setPrompts(prompts);

         filters.addFilter(filter);
         //System.out.println(filter);
      }
   }
}
