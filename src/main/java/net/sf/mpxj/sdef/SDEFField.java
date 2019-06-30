package net.sf.mpxj.sdef;

public interface SDEFField
{
   int getLength();
   
   Object read(String line, int offset);
}
