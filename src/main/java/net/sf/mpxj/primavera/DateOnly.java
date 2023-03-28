package net.sf.mpxj.primavera;
import java.util.Date;

final class DateOnly
{
   public DateOnly(Date date)
   {
      m_date = date;
   }

   public Date toDate()
   {
      return m_date;
   }

   private final Date m_date;
}
