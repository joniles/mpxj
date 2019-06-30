
package net.sf.mpxj.sdef;

class VolumeRecord extends AbstractSDEFRecord
{
   @Override protected SDEFField[] getFieldDefinitions()
   {
      return FIELDS;
   }
   
   private static final SDEFField[] FIELDS = new SDEFField[]
   {
      new IntegerField("Disk Number", 2)
   };
}
