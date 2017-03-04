
package net.sf.mpxj.fasttrack;

public interface FastTrackColumn
{
   public void read(FastTrackTableType tableType, byte[] buffer, int startIndex, int length);

   public String getName();

   public int getIndexNumber();

   public int getFlags();

   public FastTrackField getType();

   public Object[] getData();
}
