
package net.sf.mpxj.sdef;

import net.sf.mpxj.DataType;

class ProgressRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new IntegerField("Activity ID", 10),
      new DateField("Actual Start Date"),
      new DateField("Actual Finish Date"),
      new DurationField("Remaining Duration", 3),
      new DoubleField("Activity Cost", 12),
      new DoubleField("Cost to Date", 12),
      new DoubleField("Stored Material", 12),
      new DateField("Early Start Date"),
      new DateField("Early Finish Date"),
      new DateField("Late Start Date"),
      new DateField("Late Finish Date"),
      new StringField("Float Sign", 1),
      new DurationField("Total Float", 3)
   };
}
