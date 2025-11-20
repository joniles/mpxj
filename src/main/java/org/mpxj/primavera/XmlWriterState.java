/*
 * file:       XmlWriterState.java
 * author:     Jon Iles
 * date:       2025-11-19
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

package org.mpxj.primavera;

import java.util.Set;

import org.mpxj.Currency;
import org.mpxj.FieldType;
import org.mpxj.common.ObjectSequence;
import org.mpxj.primavera.schema.APIBusinessObjects;
import org.mpxj.primavera.schema.ObjectFactory;

/**
 * State information shared between PMXML writer classes.
 */
class XmlWriterState
{
   /**
    * Constructor.
    *
    * @param userDefinedFields populated user defined fields
    * @param defaultCurrency default currency
    */
   public XmlWriterState(Set<FieldType> userDefinedFields, Currency defaultCurrency)
   {
      m_userDefinedFields = userDefinedFields;
      m_defaultCurrency = defaultCurrency;
   }

   /**
    * Retrieve the root node of the PMXML file.
    *
    * @return PMXML file root node
    */
   public APIBusinessObjects getApibo()
   {
      return m_apibo;
   }

   /**
    * Retrieve the next rate Object ID.
    *
    * @return next rate Object ID
    */
   public Integer getRateObjectID()
   {
      return m_rateObjectID.getNext();
   }

   /**
    * Retrieve the next WBS note Object ID.
    *
    * @return WBS note Object ID
    */
   public Integer getWbsNoteObjectID()
   {
      return m_wbsNoteObjectID.getNext();
   }

   /**
    * Retrieve the next Activity note Object ID.
    *
    * @return activity note object ID
    */
   public Integer getActivityNoteObjectID()
   {
      return m_activityNoteObjectID.getNext();
   }

   /**
    * Retrieve the set of populated user defined fields.
    *
    * @return populated user defined fields
    */
   public Set<FieldType> getUserDefinedFields()
   {
      return m_userDefinedFields;
   }

   /**
    * Returns true if notes have been written which require a default notes topic to be written.
    *
    * @return true if default notes topic required
    */
   public boolean getDefaultNotesTopicUsed()
   {
      return m_defaultNotesTopicUsed;
   }

   /**
    * Called when the default notes topic is used.
    */
   public void defaultNotesTopicUsed()
   {
      m_defaultNotesTopicUsed = true;
   }

   /**
    * Retrieve the default currency.
    *
    * @return default currency
    */
   public Currency getDefaultCurrency()
   {
      return m_defaultCurrency;
   }

   private final APIBusinessObjects m_apibo = new ObjectFactory().createAPIBusinessObjects();
   private final ObjectSequence m_rateObjectID = new ObjectSequence(1);
   private final ObjectSequence m_wbsNoteObjectID = new ObjectSequence(1);
   private final ObjectSequence m_activityNoteObjectID = new ObjectSequence(1);
   private final Set<FieldType> m_userDefinedFields;
   private final Currency m_defaultCurrency;
   private boolean m_defaultNotesTopicUsed;
}
