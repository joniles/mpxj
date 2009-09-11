/*
 * file:       LocaleData_de.java
 * author:     Harald Hett
 *             Jon Iles
 * copyright:  (c) Packwood Software 2004
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

package net.sf.mpxj.mpx;

import java.util.HashMap;
import java.util.ListResourceBundle;

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class defines the German translation of resource required by MPX files.
 */
public final class LocaleData_de extends ListResourceBundle
{
   /**
    * {@inheritDoc}
    */
   @Override public Object[][] getContents()
   {
      return (RESOURCE_DATA);
   }

   private static final String[][] TIME_UNITS_ARRAY_DATA =
   {
      {
         "m"
      },
      {
         "h"
      },
      {
         "t"
      },
      {
         "w"
      },
      {
         "mon"
      },
      {
         "y"
      },
      {
         "%"
      },
      {
         "fm"
      },
      {
         "fh"
      },
      {
         "ft"
      },
      {
         "fw"
      },
      {
         "fmon"
      },
      {
         "fy"
      },
      {
         "f%"
      }
   };
   private static final HashMap<String, Integer> TIME_UNITS_MAP_DATA = new HashMap<String, Integer>();

   static
   {
      for (int loop = 0; loop < TIME_UNITS_ARRAY_DATA.length; loop++)
      {
         Integer value = Integer.valueOf(loop);
         for (String name : TIME_UNITS_ARRAY_DATA[loop])
         {
            TIME_UNITS_MAP_DATA.put(name, value);
         }
      }
   }

   private static final String[] ACCRUE_TYPES_DATA =
   {
      "Anfang",
      "Ende",
      "Anteilig"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "EE",
      "EA",
      "AE",
      "AA"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "Am niedrigsten",
      "Sehr niedrig",
      "Niedriger",
      "Niedrig",
      "Mittel",
      "Hoch",
      "Höher",
      "Sehr hoch",
      "Am höchsten",
      "Nicht abgleichen"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "So früh wie möglich",
      "So spät wie möglich",
      "Muss anfangen am",
      "Muss enden am",
      "Anfang nicht früher als",
      "Anfang nicht später als",
      "Ende nicht früher als",
      "Ende nicht später als"
   };

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
      "Notizen",
      "Kontaktperson",
      "Ressourcengruppe",
      null,
      null,
      null,
      "Arbeit",
      "Geplante Arbeit",
      "Aktuelle Arbeit",
      "Verbleibende Arbeit",
      "Abweichung Arbeit",
      "% Arbeit abgeschlossen",
      null,
      null,
      null,
      null,
      "Kosten",
      "Geplante Kosten",
      "Aktuelle Kosten",
      "Verbleibende Kosten",
      "Abweichung Kosten",
      "Feste Kosten",
      "Kosten1",
      "Kosten2",
      "Kosten3",
      null,
      "Dauer",
      "Geplante Dauer",
      "Aktuelle Dauer",
      "Verbleibende Dauer",
      "% Abgeschlossen",
      "Abweichung Dauer",
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
      "Abweichung Anfang",
      "Abweichung Ende",
      "Einschränkungstermin",
      null,
      "Vorgänger",
      "Nachfolger",
      "Ressourcenamen",
      "Ressourcenkürzel",
      "Einmalige Nr. für Vorgänger",
      "Einmalige Nr. für Nachfolger",
      null,
      null,
      null,
      null,
      "Fest",
      "Meilenstein",
      "Kritisch",
      "Markiert",
      "Rollup",
      "SKAA",
      "SKBA",
      "PA",
      "KA",
      null,
      "Nr.",
      "Einschränkungsart",
      "Verzögerung",
      "Freie Pufferzeit",
      "Gesamte Pufferzeit",
      "Priorität",
      "Teilprojektdatei",
      "Projekt",
      "Einmalige Nr.",
      "Gliederungsnummer",
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
      "Objekte",
      "Verknüpfte Felder",
      "Balken ausblenden",
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
      "Bestätigt",
      "Aktualisierung erforderlich",
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
      "Wiederaufnahme"
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
      "Notizen",
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
      "Verbleibende Arbeit",
      "Überstundenarbeit",
      "Abweichung Arbeit",
      "% Arbeit abgeschlossen",
      null,
      null,
      null,
      "Kosten",
      "Geplante Kosten",
      "Aktuelle Kosten",
      "Verbleibende Kosten",
      "Abweichung Kosten",
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
      "Überlastet",
      "Spitze",
      "Basiskalender",
      "Einmalige Nr.",
      "Objekte",
      "Verknüpfte Felder"
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
      },
      {
         LocaleData.FILE_VERSION,
         "4,0"
      },

      {
         LocaleData.YES,
         "Ja"
      },
      {
         LocaleData.NO,
         "Nein"
      },

      {
         LocaleData.CURRENCY_SYMBOL,
         "€"
      },
      {
         LocaleData.CURRENCY_SYMBOL_POSITION,
         CurrencySymbolPosition.AFTER_WITH_SPACE
      },
      {
         LocaleData.CURRENCY_THOUSANDS_SEPARATOR,
         "."
      },
      {
         LocaleData.CURRENCY_DECIMAL_SEPARATOR,
         ","
      },

      {
         LocaleData.TIME_FORMAT,
         ProjectTimeFormat.TWENTY_FOUR_HOUR
      },
      {
         LocaleData.DATE_SEPARATOR,
         "."
      },
      {
         LocaleData.AM_TEXT,
         ""
      },
      {
         LocaleData.PM_TEXT,
         ""
      },
      {
         LocaleData.DATE_FORMAT,
         ProjectDateFormat.EEE_DD_MM_YY
      },
      {
         LocaleData.NA,
         "NV"
      },

      {
         LocaleData.TIME_UNITS_ARRAY,
         TIME_UNITS_ARRAY_DATA
      },
      {
         LocaleData.TIME_UNITS_MAP,
         TIME_UNITS_MAP_DATA
      },

      {
         LocaleData.ACCRUE_TYPES,
         ACCRUE_TYPES_DATA
      },
      {
         LocaleData.RELATION_TYPES,
         RELATION_TYPES_DATA
      },
      {
         LocaleData.PRIORITY_TYPES,
         PRIORITY_TYPES_DATA
      },
      {
         LocaleData.CONSTRAINT_TYPES,
         CONSTRAINT_TYPES_DATA
      },

      {
         LocaleData.TASK_NAMES,
         TASK_NAMES_DATA
      },
      {
         LocaleData.RESOURCE_NAMES,
         RESOURCE_NAMES_DATA
      }
   };
}
