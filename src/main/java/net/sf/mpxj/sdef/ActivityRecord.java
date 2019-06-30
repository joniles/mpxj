
package net.sf.mpxj.sdef;

class ActivityRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new IntegerField("Activity ID", 10),
      new StringField("Activity Description", 30),
      new DurationField("Activity Duration", 3),
      new DateField("Constraint Date"),
      new ConstraintTypeField("Constraint Type"),
      new StringField("Calendar Code", 1),
      new StringField("Hammock Code", 1),
      new IntegerField("Workers Per Day", 3),
      new StringField("Responsibility Code", 4),
      new StringField("Work Area Code", 4),
      new StringField("Mod of Claim No", 6),
      new StringField("Bid Item", 6),
      new StringField("Phase of Work", 2),
      new StringField("Category of Work", 1),
      new StringField("Feature of Work", 10)
   };
}
