
package net.sf.mpxj.sdef;

class PrecedenceRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new IntegerField("Activity ID", 10),
      new IntegerField("Preceding Activity", 10),
      new RelationTypeField("Predecessor Type"),
      new DurationField("Lag Duration", 4)
   };
}
