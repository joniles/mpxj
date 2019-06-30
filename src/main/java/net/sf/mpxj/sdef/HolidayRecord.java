
package net.sf.mpxj.sdef;

class HolidayRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new StringField("Calendar Code", 1),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date"),
      new DateField("Holiday Date")
   };
}
