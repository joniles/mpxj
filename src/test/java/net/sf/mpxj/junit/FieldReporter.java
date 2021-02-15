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
      populate(project.getTasks().getPopulatedFields(), key);
      populate(project.getResources().getPopulatedFields(), key);
      populate(project.getResourceAssignments().getPopulatedFields(), key);
   }

   /**
    * Write a report to a file.
    * 
    * @param file file name
    */
   public void report(String file) throws IOException
   {
      PrintWriter pw = new PrintWriter(file);
      String tableHeader = populateTableHeader();

      pw.println("# Field Guide");
      pw.println("The tables below provide an indication of which fields are populated when files of different types are read using MPXJ.");
      pw.println("The tables are not hand-crafted: they have been generated from test data and are therefore may be missing some details.");
      pw.println();

      //
      // Task
      //
      Set<FieldType> taskExtendedFields = new HashSet<>(Arrays.asList(TaskFieldLists.EXTENDED_FIELDS));
      taskExtendedFields.addAll(Arrays.asList(TaskFieldLists.ENTERPRISE_CUSTOM_FIELD));
      pw.println("## Task");

      pw.println("### Core Fields");
      pw.println(tableHeader);
      m_map.entrySet().stream().filter(e -> e.getKey().getFieldTypeClass() == FieldTypeClass.TASK && !taskExtendedFields.contains(e.getKey())).forEach(e -> writeTableRow(pw, e));

      pw.println("### Extended Fields");
      pw.println(tableHeader);
      m_map.entrySet().stream().filter(e -> taskExtendedFields.contains(e.getKey())).forEach(e -> writeTableRow(pw, e));

      //
      // Resource
      //
      Set<FieldType> resourceExtendedFields = new HashSet<>(Arrays.asList(ResourceFieldLists.EXTENDED_FIELDS));
      resourceExtendedFields.addAll(Arrays.asList(ResourceFieldLists.ENTERPRISE_CUSTOM_FIELD));
      pw.println("## Resource");

      pw.println("### Core Fields");
      pw.println(tableHeader);
      m_map.entrySet().stream().filter(e -> e.getKey().getFieldTypeClass() == FieldTypeClass.RESOURCE && !resourceExtendedFields.contains(e.getKey())).forEach(e -> writeTableRow(pw, e));

      pw.println("### Extended Fields");
      pw.println(tableHeader);
      m_map.entrySet().stream().filter(e -> resourceExtendedFields.contains(e.getKey())).forEach(e -> writeTableRow(pw, e));

      //
      // Resource Assignment
      //
      Set<FieldType> assignmentExtendedFields = new HashSet<>(Arrays.asList(AssignmentFieldLists.EXTENDED_FIELDS));
      assignmentExtendedFields.addAll(Arrays.asList(AssignmentFieldLists.ENTERPRISE_CUSTOM_FIELD));
      pw.println("## Resource Assignment");

      pw.println("### Core Fields");
      pw.println(tableHeader);
      m_map.entrySet().stream().filter(e -> e.getKey().getFieldTypeClass() == FieldTypeClass.ASSIGNMENT && !assignmentExtendedFields.contains(e.getKey())).forEach(e -> writeTableRow(pw, e));

      pw.println("### Extended Fields");
      pw.println(tableHeader);
      m_map.entrySet().stream().filter(e -> assignmentExtendedFields.contains(e.getKey())).forEach(e -> writeTableRow(pw, e));

      pw.flush();
      pw.close();
   }

   private String populateTableHeader()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("||");
      sb.append(m_keys.stream().collect(Collectors.joining("|")));
      sb.append("|\n");

      sb.append("|---|");
      sb.append(m_keys.stream().map(v -> "---").collect(Collectors.joining("|")));
      sb.append("|");

      return sb.toString();
   }

   private void writeTableRow(PrintWriter pw, Entry<FieldType, Set<String>> entry)
   {
      pw.print("|");
      pw.print(entry.getKey());
      pw.print("|");

      Set<String> set = entry.getValue();
      for (String key : m_keys)
      {
         pw.print(set.contains(key) ? "\u2713|" : "|");
      }
      pw.println();
   }

   private void populate(Set<? extends FieldType> fields, String key)
   {
      fields.forEach(f -> m_map.computeIfAbsent(f, k -> new HashSet<>()).add(key));
   }

   private String getTypeFullName(FieldType field)
   {
      return field.getFieldTypeClass() + "." + field.getName();
   }

   private final Set<String> m_keys = new TreeSet<>();
   private final Map<FieldType, Set<String>> m_map = new TreeMap<>((f1, f2) -> COMPARATOR.compare(getTypeFullName(f1), getTypeFullName(f2)));
   private static final Comparator<String> COMPARATOR = new AlphanumComparator();
}
