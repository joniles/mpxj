package net.sf.mpxj.cpm;

import java.time.LocalDateTime;

class AnnotatedDateTime
{
   public static AnnotatedDateTime from(LocalDateTime value)
   {
      if (value == null)
      {
         return null;
      }

      return new AnnotatedDateTime(value, false);
   }

   public static AnnotatedDateTime fromActual(LocalDateTime value)
   {
      if (value == null)
      {
         return null;
      }

      return new AnnotatedDateTime(value, true);
   }

   private AnnotatedDateTime(LocalDateTime value, boolean actual)
   {
      m_value = value;
      m_actual = actual;
   }

   public LocalDateTime getValue()
   {
      return m_value;
   }

   public boolean isActual()
   {
      return m_actual;
   }

   public boolean isBefore(AnnotatedDateTime value)
   {
      return m_value.isBefore(value.m_value);
   }

   public boolean isAfter(AnnotatedDateTime value)
   {
      return m_value.isAfter(value.m_value);
   }

   public boolean isBefore(LocalDateTime value)
   {
      return m_value.isBefore(value);
   }

   public boolean isAfter(LocalDateTime value)
   {
      return m_value.isAfter(value);
   }

   public String toString()
   {
      return m_value + (m_actual ? "A" : "");
   }

   private final LocalDateTime m_value;
   private final boolean m_actual;
}
