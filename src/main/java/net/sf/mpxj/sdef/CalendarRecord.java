
package net.sf.mpxj.sdef;

class CalendarRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new StringField("Calendar Code", 1),
      new StringField("Workdays", 7),
      new StringField("Calendar Description", 30)
   };
}
