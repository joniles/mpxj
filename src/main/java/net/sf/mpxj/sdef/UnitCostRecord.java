
package net.sf.mpxj.sdef;

import net.sf.mpxj.DataType;

class UnitCostRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new StringField("Activity ID", 10),
      new DoubleField("Total QTY", 13),
      new DoubleField("Cost per Unit", 13),
      new DoubleField("QTY to Date", 13),
      new StringField("Unit of Measure", 3)
   };
}
