/*
 * file:       LocaleData_de.java
 * author:     Jon Iles
 *             Harald Hett
 * copyright:  (c) Tapster Rock Limited 2004
 * date:       24/03/2004
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

package com.tapsterrock.mpx;

import java.util.HashMap;
import java.util.ListResourceBundle;


/**
 * This class defines the German translation of resource required by MPX files.
 */
public final class LocaleData_de extends ListResourceBundle
{
   /**
    * @see ListResourceBundle#getContents
    */
   public Object[][] getContents()
   {
      return (RESOURCE_DATA);
   }

   private static final String[] TIME_UNITS_ARRAY_DATA = {"m", "h", "t", "w", "mon", "y", "%", "fm", "fh", "ft", "fw", "fmon", "fy", "f%"};
   private static final HashMap TIME_UNITS_MAP_DATA = new HashMap ();

   static
   {
      for (int loop=0; loop < TIME_UNITS_ARRAY_DATA.length; loop++)
      {
         TIME_UNITS_MAP_DATA.put(TIME_UNITS_ARRAY_DATA[loop], new Integer(loop));
      }
   }

   private static final String[] TASK_NAMES_DATA = 
   {
      null,
      "Name",
      "PSP-Code",
      "Gliederungsebene",
      "Text1",
      "Text2",
      "Text3",
      "Text4",
      "Text5",
      "Text6",
      "Text7",
      "Text8",
      "Text9",
      "Text10",
      "Notes", // translate
      "Kontaktperson",
      "Resource Group", // translate
      null,
      null,
      null,
      "Arbeit",
      "Geplante Arbeit",
      "Aktuelle Arbeit",
      "Remaining Work", // translate
      "Work Variance", // translate
      "% Work Complete", // translate
      null,
      null,
      null,
      null,
      "Kosten",
      "Geplante Kosten",
      "Aktuelle Kosten",
      "Verbleibende Kosten",
      "Cost Variance", // translate
      "Feste Kosten",
      "Kosten1",
      "Kosten2",
      "Kosten3",
      null,
      "Dauer",
      "Geplante Dauer",
      "Actual Duration", // translate
      "Remaining Duration", // translate
      "% Abgeschlossen",
      "Duration Variance", // translate
      "Dauer1",
      "Dauer2",
      "Dauer3",
      null,
      "Anfang",
      "Ende",
      "Frühester Anfang",
      "Frühestes Ende",
      "Spätester Anfang",
      "Spätestes Ende",
      "Geplanter Anfang",
      "Geplantes Ende",
      "Aktueller Anfang",
      "Aktuelles Ende",
      "Anfang1",
      "Ende1",
      "Anfang2",
      "Ende2",
      "Anfang3",
      "Ende3",
      "Start Variance", // translate
      "Finish Variance", // translate
      "Einschränkungstermin",
      null,
      "Vorgänger",
      "Successors", // translate
      "Resource Names", // translate
      "Resource Initials", // translate
      "Unique ID Predecessors", // translate
      "Unique ID Successors", // translate
      null,
      null,
      null,
      null,
      "Fest",
      "Meilenstein",
      "Critical", // translate
      "Markiert",
      "Rollup",
      "BCWS", // translate
      "BCWP", // translate
      "SV", // translate
      "CV", // translate
      null,
      "Nr.",
      "Einschränkungsart",
      "Verzögerung",
      "Freie Pufferzeit",
      "Gesamte Pufferzeit",
      "Priorität",
      "Teilprojektdatei",
      "Project", // translate
      "Einmalige Nr.",
      "Outline Number", // outline number
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Attribut1",
      "Attribut2",
      "Attribut3",
      "Attribut4",
      "Attribut5",
      "Attribut6",
      "Attribut7",
      "Attribut8",
      "Attribut9",
      "Attribut10",
      "Sammelvorgang",
      "Objects", // translate
      "Linked Fields", // translate
      "Hide Bar", // translate
      null,
      "Erzeugt",
      "Anfang4",
      "Ende4",
      "Anfang5",
      "Ende5",
      null,
      null,
      null,
      null,
      null,
      "Confirmed", // translate
      "Update Needed", // translate
      null,
      null,
      null,
      "Zahl1",
      "Zahl2",
      "Zahl3",
      "Zahl4",
      "Zahl5",
      null,
      null,
      null,
      null,
      null,
      "Unterbrechungstermin",
      "Wiederaufnahme nicht früher als",
      "Resume" // translate      
   };
   
   private static final String[] RESOURCE_NAMES_DATA =
   {
      null,
      "Name",
      "Kürzel",
      "Gruppe",
      "Code",
      "Text1",
      "Text2",
      "Text3",
      "Text4",
      "Text5",
      "Notes", // translate
      "E-Mail-Adresse",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Arbeit",
      "Geplante Arbeit",
      "Aktuelle Arbeit",
      "Remaining Work", // translate
      "Überstundenarbeit",
      "Work Variance", // translate
      "% Work Complete",  // translate
      null,
      null,
      null,
      "Kosten",
      "Geplante Kosten",
      "Aktuelle Kosten",
      "Remaining Cost", // translate
      "Cost Variance", // translate
      null,
      null,
      null,
      null,
      null,
      "Nr.",
      "Max. Einheiten",
      "Standardsatz",
      "Überstundensatz",
      "Kosten pro Einsatz",
      "Fällig am",
      "Overallocated", // translate
      "Peak", // translated
      "Base Calendar", // translate
      "Einmalige Nr.",
      "Objects", // translate
      "Linked Fields" // translate      
   };
   
   private static final Object[][] RESOURCE_DATA =
   {
      {LocaleData.FILE_DELIMITER, ";"},
      {LocaleData.FILE_VERSION, "4,0"},

      {LocaleData.YES, "Ja"},
      {LocaleData.NO, "Nein"},
      
      {LocaleData.CURRENCY_SYMBOL, "€"},
      {LocaleData.CURRENCY_SYMBOL_POSITION, new Integer (2)},      
      {LocaleData.CURRENCY_THOUSANDS_SEPARATOR, "."},
      {LocaleData.CURRENCY_DECIMAL_SEPARATOR, ","}, 

      {LocaleData.TIME_FORMAT, new Integer(1)},   
      {LocaleData.DATE_SEPARATOR, "."}, 
      {LocaleData.AM_TEXT, ""},
      {LocaleData.PM_TEXT, ""},
      {LocaleData.DATE_FORMAT, new Integer (9)},
      {LocaleData.NA, "NV"},
      
      {LocaleData.TIME_UNITS_ARRAY, TIME_UNITS_ARRAY_DATA},
      {LocaleData.TIME_UNITS_MAP, TIME_UNITS_MAP_DATA},

      {LocaleData.TASK_NAMES, TASK_NAMES_DATA},
      {LocaleData.RESOURCE_NAMES, RESOURCE_NAMES_DATA}      
   };
}
