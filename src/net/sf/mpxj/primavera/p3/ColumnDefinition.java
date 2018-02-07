package net.sf.mpxj.primavera.p3;

interface ColumnDefinition
{
   public String getName();
   
   public Object read(int offset, byte[] data);
}
