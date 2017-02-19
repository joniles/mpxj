
package net.sf.mpxj.fasttrack;

public interface FastTrackBlock
{
   public void read(byte[] buffer, int startIndex, int length);

   public String getName();

   public Object[] getData();
}
