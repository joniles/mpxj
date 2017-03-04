
package net.sf.mpxj.fasttrack;

public interface FastTrackColumn
{
   public void read(byte[] buffer, int startIndex, int length);

   public String getName();

   public int getIndexNumber();

   public int getFlags();

   public Object[] getData();
}
