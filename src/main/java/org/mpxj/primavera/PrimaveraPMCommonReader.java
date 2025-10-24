package org.mpxj.primavera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mpxj.ActivityCode;
import org.mpxj.ActivityCodeContainer;
import org.mpxj.ActivityCodeValue;
import org.mpxj.ProjectContext;
import org.mpxj.common.BooleanHelper;
import org.mpxj.common.ColorHelper;
import org.mpxj.common.HierarchyHelper;
import org.mpxj.primavera.schema.ActivityCodeType;
import org.mpxj.primavera.schema.ActivityCodeTypeType;

class PrimaveraPMCommonReader
{

   /**
    * Process activity code definitions.
    *
    * @param types list of activity code types
    * @param typeValues list of activity code values
    */
   protected void processActivityCodeDefinitions(ProjectContext context, List<ActivityCodeTypeType> types, List<ActivityCodeType> typeValues)
   {
      ActivityCodeContainer container = context.getActivityCodes();
      Map<Integer, ActivityCode> map = new HashMap<>();

      for (ActivityCodeTypeType type : types)
      {
         ActivityCode code = new ActivityCode.Builder(context)
            .uniqueID(type.getObjectId())
            .scope(ActivityCodeScopeHelper.getInstanceFromXml(type.getScope()))
            .scopeEpsUniqueID(type.getEPSObjectId())
            .scopeProjectUniqueID(type.getProjectObjectId())
            .sequenceNumber(type.getSequenceNumber())
            .name(type.getName())
            .secure(BooleanHelper.getBoolean(type.isIsSecureCode()))
            .maxLength(type.getLength())
            .build();
         container.add(code);
         map.put(code.getUniqueID(), code);
      }

      typeValues = HierarchyHelper.sortHierarchy(typeValues, ActivityCodeType::getObjectId, ActivityCodeType::getParentObjectId);
      for (ActivityCodeType typeValue : typeValues)
      {
         ActivityCode code = map.get(typeValue.getCodeTypeObjectId());
         if (code != null)
         {
            ActivityCodeValue value = new ActivityCodeValue.Builder(context)
               .activityCode(code)
               .uniqueID(typeValue.getObjectId())
               .sequenceNumber(typeValue.getSequenceNumber())
               .name(typeValue.getCodeValue())
               .description(typeValue.getDescription())
               .color(ColorHelper.parseHtmlColor(typeValue.getColor()))
               .parentValue(code.getValueByUniqueID(typeValue.getParentObjectId()))
               .build();
            code.addValue(value);
         }
      }
   }
}
