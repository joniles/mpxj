/*
 * file:       FieldReporter.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2021
 * date:       14/02/2021
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.junit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.mpxj.FieldType;
import org.mpxj.FieldTypeClass;
import org.mpxj.ProjectFile;
import org.mpxj.ProjectProperties;
import org.mpxj.UserDefinedField;
import org.mpxj.common.AlphanumComparator;
import org.mpxj.common.AssignmentFieldLists;
import org.mpxj.common.ResourceFieldLists;
import org.mpxj.common.TaskFieldLists;
import org.mpxj.common.FieldLists;

/**
 * Collect details of which fields are populated for each file type.
 */
public class FieldReporter
{
   /**
    * Clear collected data.
    */
   public void clear()
   {
      m_keys.clear();
      m_map.clear();

      m_mppKeys.clear();
      m_mppMap.clear();
   }

   /**
    * Extract data from a project.
    *
    * @param project ProjectFile instance
    */
   public void process(ProjectFile project)
   {
      ProjectProperties props = project.getProjectProperties();
      String fileType = props.getFileType();
      String fileApplication = fileType.equals("MSPDI") || fileType.equals("MPP") ? "Microsoft" : props.getFileApplication();

      String key = fileApplication + " (" + fileType + ")";
      m_keys.add(key);
      populate(m_map, project, key);

      if (fileType.equals("MPP"))
      {
         key = "MPP" + props.getMppFileType();
         m_mppKeys.add(key);
         populate(m_mppMap, project, key);
      }
   }

   /**
    * Write a report to a file.
    *
    * @param file file name
    */
   public void report(String file) throws IOException
   {
      report(file, m_keys, m_map, "Field Guide", "The tables below provide an indication of which fields are populated when files of different types are read using MPXJ");
   }

   /**
    * Write a report to a file.
    *
    * @param file file name
    */
   public void reportMpp(String file) throws IOException
   {
      report(file, m_mppKeys, m_mppMap, "MPP Field Guide", "The tables below provide an indication of which fields are populated when different MPP file versions are read using MPXJ");
   }

   private void report(String file, Set<String> keys, Map<FieldType, Set<String>> map, String title, String text) throws IOException
   {
      PrintWriter pw = new PrintWriter(file, "UTF-8");

      pw.println("<style type='text/css' rel='stylesheet'>");
      pw.println("table {");
      pw.println("   display: block;");
      pw.println("   height: 300px;");
      pw.println("   overflow: auto;");
      pw.println("   width: 100%;");
      pw.println("}");
      pw.println("");
      pw.println("th {");
      pw.println("   position: sticky;");
      pw.println("   top: 0;");
      pw.println("   z-index: 1; ");
      pw.println("}");
      pw.println("</style>");
      pw.println();

      pw.println("# " + title);
      pw.println(text);
      pw.println("The tables are not hand-crafted: they have been generated from test data and are therefore may be missing some details.");
      pw.println();

      writeTables("Project", pw, keys, map, e -> isProjectField(e.getKey()));
      writeTables("Task", pw, keys, map, e -> isTaskField(e.getKey()));
      writeTables("Resource", pw, keys, map, e -> isResourceField(e.getKey()));
      writeTables("Resource Assignment", pw, keys, map, e -> isAssignmentField(e.getKey()));

      pw.flush();
      pw.close();
   }

   private boolean isProjectField(FieldType type)
   {
      return type.getFieldTypeClass() == FieldTypeClass.PROJECT;
   }

   private boolean isTaskField(FieldType type)
   {
      return type.getFieldTypeClass() == FieldTypeClass.TASK;
   }

   private boolean isResourceField(FieldType type)
   {
      return type.getFieldTypeClass() == FieldTypeClass.RESOURCE;
   }

   private boolean isAssignmentField(FieldType type)
   {
      return type.getFieldTypeClass() == FieldTypeClass.ASSIGNMENT;
   }

   private boolean isCustomField(FieldType type)
   {
      return CUSTOM_FIELDS.contains(type);
   }

   private boolean isBaselineField(FieldType type)
   {
      return type.toString().contains("Baseline");
   }

   private boolean isEnterpriseField(FieldType type)
   {
      return type.toString().contains("Enterprise");
   }

   private void writeTables(String title, PrintWriter pw, Set<String> keys, Map<FieldType, Set<String>> map, Predicate<Entry<FieldType, Set<String>>> filterPredicate)
   {
      List<Entry<FieldType, Set<String>>> coreFields = map.entrySet().stream().filter(filterPredicate).filter(e -> !isBaselineField(e.getKey()) && !isCustomField(e.getKey()) && !isEnterpriseField(e.getKey())).collect(Collectors.toList());
      List<Entry<FieldType, Set<String>>> baselineFields = map.entrySet().stream().filter(filterPredicate).filter(e -> isBaselineField(e.getKey()) && !isCustomField(e.getKey()) && !isEnterpriseField(e.getKey())).collect(Collectors.toList());
      List<Entry<FieldType, Set<String>>> customFields = map.entrySet().stream().filter(filterPredicate).filter(e -> !isBaselineField(e.getKey()) && isCustomField(e.getKey()) && !isEnterpriseField(e.getKey())).collect(Collectors.toList());
      List<Entry<FieldType, Set<String>>> enterpriseFields = map.entrySet().stream().filter(filterPredicate).filter(e -> !isBaselineField(e.getKey()) && !isCustomField(e.getKey()) && isEnterpriseField(e.getKey())).collect(Collectors.toList());

      if (coreFields.isEmpty() && baselineFields.isEmpty() && customFields.isEmpty() && enterpriseFields.isEmpty())
      {
         return;
      }

      String tableHeader = populateTableHeader(keys);
      pw.println("## " + title);

      writeTable("Core Fields", coreFields, pw, tableHeader, keys);
      writeTable("Baseline Fields", baselineFields, pw, tableHeader, keys);
      writeTable("Custom Fields", customFields, pw, tableHeader, keys);
      writeTable("Enterprise Fields", enterpriseFields, pw, tableHeader, keys);
   }

   private String populateTableHeader(Set<String> keys)
   {
      return "Field|" + String.join("|", keys) + "\r\n" + "---|" + keys.stream().map(v -> "---").collect(Collectors.joining("|"));
   }

   private void writeTable(String title, List<Entry<FieldType, Set<String>>> fields, PrintWriter pw, String tableHeader, Set<String> keys)
   {
      if (!fields.isEmpty())
      {
         pw.println("### " + title);
         pw.println(tableHeader);
         fields.forEach(e -> writeTableRow(pw, keys, e));
         pw.println();
      }
   }

   private void writeTableRow(PrintWriter pw, Set<String> keys, Entry<FieldType, Set<String>> entry)
   {
      pw.print(entry.getKey());

      Set<String> set = entry.getValue();
      for (String key : keys)
      {
         pw.print(set.contains(key) ? "|\u2713" : "|\u00A0");
      }
      pw.println();
   }

   private void populate(Map<FieldType, Set<String>> map, ProjectFile project, String key)
   {
      populate(map, project.getProjectProperties().getPopulatedFields(), key);
      populate(map, project.getTasks().getPopulatedFields(), key);
      populate(map, project.getResources().getPopulatedFields(), key);
      populate(map, project.getResourceAssignments().getPopulatedFields(), key);
   }

   private void populate(Map<FieldType, Set<String>> map, Set<? extends FieldType> fields, String key)
   {
      fields.stream().filter(f -> !(f instanceof UserDefinedField)).forEach(f -> map.computeIfAbsent(f, k -> new HashSet<>()).add(key));
   }

   private String getTypeFullName(FieldType field)
   {
      return field.getFieldTypeClass() + "." + field.getName();
   }

   private final Set<String> m_keys = new TreeSet<>();
   private final Map<FieldType, Set<String>> m_map = new TreeMap<>((f1, f2) -> COMPARATOR.compare(getTypeFullName(f1), getTypeFullName(f2)));

   private final Set<String> m_mppKeys = new TreeSet<>(COMPARATOR);
   private final Map<FieldType, Set<String>> m_mppMap = new TreeMap<>((f1, f2) -> COMPARATOR.compare(getTypeFullName(f1), getTypeFullName(f2)));

   private static final Comparator<String> COMPARATOR = new AlphanumComparator();

   private static final Set<FieldType> CUSTOM_FIELDS = new HashSet<>();

   static
   {
      CUSTOM_FIELDS.addAll(FieldLists.CUSTOM_FIELDS);
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DURATION_UNITS));
      CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_DURATION_UNITS));
      CUSTOM_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_DURATION_UNITS));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_OUTLINE_CODE));
      CUSTOM_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_OUTLINE_CODE_INDEX));
      CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_OUTLINE_CODE));
      CUSTOM_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_OUTLINE_CODE_INDEX));
   }
}
