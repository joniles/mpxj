/*
 * file:       GroupReader14.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2010
 * date:       31/03/2010
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

package org.mpxj.mpp;

import java.awt.Color;
import java.util.Map;

import org.mpxj.FieldType;
import org.mpxj.Group;
import org.mpxj.GroupClause;
import org.mpxj.ProjectFile;
import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.FieldTypeHelper;

/**
 * This class allows filter definitions to be read from an MPP file.
 */
public final class GroupReader14
{
   /**
    * Entry point for processing group definitions.
    *
    * @param file project file
    * @param fixedData group fixed data
    * @param varData group var data
    * @param fontBases map of font bases
    */
   public void process(ProjectFile file, FixedData fixedData, Var2Data varData, Map<Integer, FontBase> fontBases)
   {
      int groupCount = fixedData.getItemCount();
      for (int groupLoop = 0; groupLoop < groupCount; groupLoop++)
      {
         byte[] groupFixedData = fixedData.getByteArrayValue(groupLoop);
         if (groupFixedData == null || groupFixedData.length < 4)
         {
            continue;
         }

         Integer groupID = Integer.valueOf(ByteArrayHelper.getInt(groupFixedData, 0));

         byte[] groupVarData = varData.getByteArray(groupID, GROUP_DATA);
         if (groupVarData == null)
         {
            continue;
         }

         String groupName = MPPUtility.getUnicodeString(groupFixedData, 4);

         // 8 byte header, 48 byte blocks for each clause
         //System.out.println(ByteArrayHelper.hexdump(groupVarData, true, 16, ""));

         // header=4 byte int for unique id
         // short 4 = show summary tasks
         // short int at byte 6 for number of clauses
         //Integer groupUniqueID = Integer.valueOf(MPPUtility.getInt(groupVarData, 0));
         boolean showSummaryTasks = (ByteArrayHelper.getShort(groupVarData, 4) != 0);

         Group group = new Group(groupID, groupName, showSummaryTasks);
         file.getGroups().add(group);

         int clauseCount = ByteArrayHelper.getShort(groupVarData, 10);
         int offset = 12;

         for (int clauseIndex = 0; clauseIndex < clauseCount; clauseIndex++)
         {
            if (offset + 71 > groupVarData.length)
            {
               break;
            }

            //System.out.println("Clause " + clauseIndex);
            //System.out.println(ByteArrayHelper.hexdump(groupVarData, offset, 71, false, 16, ""));

            GroupClause clause = new GroupClause();
            group.addGroupClause(clause);

            int fieldID = ByteArrayHelper.getInt(groupVarData, offset);
            FieldType type = FieldTypeHelper.getInstance(file, fieldID);
            clause.setField(type);

            // from byte 0 2 byte short int - field type
            // byte 3 - entity type 0b/0c
            // 4th byte in clause is 1=asc 0=desc
            // offset+8=font index, from font bases
            // offset+12=color, byte
            // offset+13=pattern, byte

            boolean ascending = (MPPUtility.getByte(groupVarData, offset + 4) != 0);
            clause.setAscending(ascending);

            int fontIndex = MPPUtility.getByte(groupVarData, offset + 8);
            FontBase fontBase = fontBases.get(Integer.valueOf(fontIndex));

            int style = MPPUtility.getByte(groupVarData, offset + 9);
            boolean bold = ((style & 0x01) != 0);
            boolean italic = ((style & 0x02) != 0);
            boolean underline = ((style & 0x04) != 0);
            Color fontColor = MPPUtility.getColor(groupVarData, offset + 10);

            FontStyle fontStyle = new FontStyle(fontBase, italic, bold, underline, false, fontColor, null, BackgroundPattern.SOLID);
            clause.setFont(fontStyle);
            clause.setCellBackgroundColor(MPPUtility.getColor(groupVarData, offset + 22));
            clause.setPattern(BackgroundPattern.getInstance(MPPUtility.getByte(groupVarData, offset + 34) & 0x0F));

            // offset+14=group on
            int groupOn = MPPUtility.getByte(groupVarData, offset + 38);
            clause.setGroupOn(groupOn);
            // offset+24=start at
            // offset+40=group interval

            Object startAt = null;
            Object groupInterval = null;
            if (type.getDataType() != null)
            {
               switch (type.getDataType())
               {
                  case DURATION:
                  case NUMERIC:
                  case CURRENCY:
                  {
                     startAt = Double.valueOf(MPPUtility.getDouble(groupVarData, offset + 47));
                     groupInterval = Double.valueOf(MPPUtility.getDouble(groupVarData, offset + 63));
                     break;
                  }

                  case PERCENTAGE:
                  {
                     startAt = Integer.valueOf(ByteArrayHelper.getInt(groupVarData, offset + 47));
                     groupInterval = Integer.valueOf(ByteArrayHelper.getInt(groupVarData, offset + 63));
                     break;
                  }

                  case BOOLEAN:
                  {
                     startAt = (ByteArrayHelper.getShort(groupVarData, offset + 47) == 1 ? Boolean.TRUE : Boolean.FALSE);
                     break;
                  }

                  case DATE:
                  {
                     startAt = MPPUtility.getTimestamp(groupVarData, offset + 47);
                     groupInterval = Integer.valueOf(ByteArrayHelper.getInt(groupVarData, offset + 63));
                     break;
                  }

                  default:
                  {
                     break;
                  }
               }
            }

            clause.setStartAt(startAt);
            clause.setGroupInterval(groupInterval);

            offset += 71;
         }

         //System.out.println(group);
      }
   }

   private static final Integer GROUP_DATA = Integer.valueOf(6);
}
