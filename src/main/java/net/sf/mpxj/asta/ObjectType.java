package net.sf.mpxj.asta;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

enum ObjectType
{
   BAR_OBJECT_TYPE(16),
   TASK_OBJECT_TYPE(20),
   MILESTONE_OBJECT_TYPE(21),
   CONSUMABLE_RESOURCE_OBJECT_TYPE(50),
   PERMANENT_RESOURCE_OBJECT_TYPE(51),
   PERMANENT_SCHEDULE_ALLOCATION_OBJECT_TYPE(59);

   ObjectType(int value)
   {
      m_value = value;
   }

   public static ObjectType getInstance(Integer value)
   {
      return MAP.get(value);
   }

   private final int m_value;

   private static final Map<Integer, ObjectType> MAP = Arrays.stream(ObjectType.values()).collect(Collectors.toMap(o -> Integer.valueOf(o.m_value), o -> o));
}
