package net.sf.mpxj.primavera;

final class MaxUnits
{
   public MaxUnits(Number number)
   {
      m_number = number;
   }

   public Number toNumber()
   {
      return m_number;
   }

   private final Number m_number;
}
