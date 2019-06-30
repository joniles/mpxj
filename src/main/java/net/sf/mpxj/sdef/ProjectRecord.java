
package net.sf.mpxj.sdef;

class ProjectRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new DateField("Data Date"),
      new StringField("Project Identifier", 4),
      new StringField("Project Name", 48),
      new StringField("Contractor Name", 36),
      new StringField("Precedence", 1),
      new StringField("Contract Number", 6),
      new DateField("Project Start"),
      new DateField("Project End")
   };
}
