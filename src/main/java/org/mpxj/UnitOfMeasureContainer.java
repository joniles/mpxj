/*
 * file:       UnitOfMeasureContainer.java
 * author:     Jon Iles
 * date:       2023-10-09
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

package org.mpxj;

/**
 * Represents units of measure available to the current project.
 */
public class UnitOfMeasureContainer extends ProjectEntityContainer<UnitOfMeasure>
{
   /**
    * Constructor.
    *
    * @param sequenceProvider sequence provider
    */
   public UnitOfMeasureContainer(UniqueIdObjectSequenceProvider sequenceProvider)
   {
      super(sequenceProvider);
   }

   /**
    * Create or retrieve a unit of measure by its abbreviation.
    *
    * @param abbreviation abbreviation
    * @return UnitOfMeasure instance or null if abbreviation is empty
    */
   public UnitOfMeasure getOrCreateByAbbreviation(String abbreviation)
   {
      if (abbreviation == null || abbreviation.isEmpty())
      {
         return null;
      }
      return stream().filter(u -> abbreviation.equals(u.getAbbreviation())).findFirst().orElseGet(() -> buildUnitOfMeasure(abbreviation));
   }

   /**
    * Create a unit of measure from a name.
    *
    * @param name unit of measure name
    * @return UnitOfMeasure instance
    */
   private UnitOfMeasure buildUnitOfMeasure(String name)
   {
      UnitOfMeasure uom = new UnitOfMeasure.Builder(m_sequenceProvider)
         .name(name)
         .abbreviation(name)
         .sequenceNumber(Integer.valueOf(stream().mapToInt(u -> u.getSequenceNumber().intValue()).max().orElse(0) + 1))
         .build();
      add(uom);
      return uom;
   }
}