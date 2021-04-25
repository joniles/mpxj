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

package net.sf.mpxj.junit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sf.mpxj.FieldType;
import net.sf.mpxj.FieldTypeClass;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.common.AlphanumComparator;
import net.sf.mpxj.common.AssignmentFieldLists;
import net.sf.mpxj.common.ResourceFieldLists;
import net.sf.mpxj.common.TaskFieldLists;

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

      pw.println("## Project");
      writeTables(pw, keys, map, e -> isProjectField(e.getKey()));

      pw.println("## Task");
      writeTables(pw, keys, map, e -> isTaskField(e.getKey()));

      pw.println("## Resource");
      writeTables(pw, keys, map, e -> isResourceField(e.getKey()));

      pw.println("## Resource Assignment");
      writeTables(pw, keys, map, e -> isAssignmentField(e.getKey()));

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

   private boolean isExtendedField(FieldType type)
   {
      return EXTENDED_FIELDS.contains(type);
   }

   private boolean isBaselineField(FieldType type)
   {
      return type.toString().contains("Baseline");
   }

   private boolean isEnterpriseField(FieldType type)
   {
      return type.toString().contains("Enterprise");
   }

   private void writeTables(PrintWriter pw, Set<String> keys, Map<FieldType, Set<String>> map, Predicate<Entry<FieldType, Set<String>>> filterPredicate)
   {
      String tableHeader = populateTableHeader(keys);

      pw.println("### Core Fields");
      pw.println(tableHeader);
      map.entrySet().stream().filter(filterPredicate).filter(e -> !isBaselineField(e.getKey()) && !isExtendedField(e.getKey()) && !isEnterpriseField(e.getKey())).forEach(e -> writeTableRow(pw, keys, e));
      pw.println();

      pw.println("### Baseline Fields");
      pw.println(tableHeader);
      map.entrySet().stream().filter(filterPredicate).filter(e -> isBaselineField(e.getKey()) && !isExtendedField(e.getKey()) && !isEnterpriseField(e.getKey())).forEach(e -> writeTableRow(pw, keys, e));
      pw.println();

      pw.println("### Extended Fields");
      pw.println(tableHeader);
      map.entrySet().stream().filter(filterPredicate).filter(e -> !isBaselineField(e.getKey()) && isExtendedField(e.getKey()) && !isEnterpriseField(e.getKey())).forEach(e -> writeTableRow(pw, keys, e));
      pw.println();

      pw.println("### Enterprise Fields");
      pw.println(tableHeader);
      map.entrySet().stream().filter(filterPredicate).filter(e -> !isBaselineField(e.getKey()) && !isExtendedField(e.getKey()) && isEnterpriseField(e.getKey())).forEach(e -> writeTableRow(pw, keys, e));
      pw.println();
   }

   private String populateTableHeader(Set<String> keys)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Field|");
      sb.append(keys.stream().collect(Collectors.joining("|")));
      sb.append("\r\n");

      sb.append("---|");
      sb.append(keys.stream().map(v -> "---").collect(Collectors.joining("|")));

      return sb.toString();
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
      fields.forEach(f -> map.computeIfAbsent(f, k -> new HashSet<>()).add(key));
   }

   private String getTypeFullName(FieldType field)
   {
      return field.getFieldTypeClass() + "." + field.getName();
   }

   private final Set<String> m_keys = new TreeSet<>();
   private final Map<FieldType, Set<String>> m_map = new TreeMap<>((f1, f2) -> COMPARATOR.compare(getTypeFullName(f1), getTypeFullName(f2)));

   private final Set<String> m_mppKeys = new TreeSet<>((k1, k2) -> COMPARATOR.compare(k1, k2));
   private final Map<FieldType, Set<String>> m_mppMap = new TreeMap<>((f1, f2) -> COMPARATOR.compare(getTypeFullName(f1), getTypeFullName(f2)));

   private static final Comparator<String> COMPARATOR = new AlphanumComparator();

   private static final Set<FieldType> EXTENDED_FIELDS = new HashSet<>();
   static
   {
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.EXTENDED_FIELDS));
      EXTENDED_FIELDS.addAll(Arrays.asList(ResourceFieldLists.EXTENDED_FIELDS));
      EXTENDED_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.EXTENDED_FIELDS));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_DURATION_UNITS));
      EXTENDED_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_DURATION_UNITS));
      EXTENDED_FIELDS.addAll(Arrays.asList(AssignmentFieldLists.CUSTOM_DURATION_UNITS));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_OUTLINE_CODE));
      EXTENDED_FIELDS.addAll(Arrays.asList(TaskFieldLists.CUSTOM_OUTLINE_CODE_INDEX));
      EXTENDED_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_OUTLINE_CODE));
      EXTENDED_FIELDS.addAll(Arrays.asList(ResourceFieldLists.CUSTOM_OUTLINE_CODE_INDEX));
   }
}
