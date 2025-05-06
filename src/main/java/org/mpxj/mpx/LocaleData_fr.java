/*
 * file:       LocaleData_fr.java
 * author:     Benoit Baranne
 *             Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       12/04/2005
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

package org.mpxj.mpx;

import java.util.HashMap;
import java.util.ListResourceBundle;

import org.mpxj.CodePage;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DateOrder;
import org.mpxj.ProjectDateFormat;
import org.mpxj.ProjectTimeFormat;

/**
 * This class defines the French translation of resource required by MPX files.
 */
public final class LocaleData_fr extends ListResourceBundle
{
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
         "j"
      },
      {
         "s"
      },
      {
         "ms"
      },
      {
         "a"
      },
      {
         "%"
      },
      {
         "me"
      },
      {
         "he"
      },
      {
         "je"
      },
      {
         "se"
      },
      {
         "mse"
      },
      {
         "???"
      },
      {
         "e%"
      }
   };
   private static final HashMap<String, Integer> TIME_UNITS_MAP_DATA = new HashMap<>();

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
      "D\u00E9but", //   "Start",
      "Fin", //   "End",
      "Proportion" //   "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "FF", //   "FF",
      "FD", //   "FS",
      "DF", //   "SF",
      "DD" //   "SS"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "Le Plus Bas", //   "Lowest",
      "Tr\u00E8s Bas", //   "Very Low",
      "Plus Bas", //   "Lower",
      "Bas", //   "Low",
      "Moyen", //   "Medium",
      "Elev\u00E9", //   "High",
      "Plus Elev\u00E9", //   "Higher",
      "Tr\u00E8s Elev\u00E9", //   "Very High",
      "Le Plus Elev\u00E9", //   "Highest",
      "Ne Pas Niveler" //   "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "D\u00E8s Que Possible", //   "As Soon As Possible",
      "Le Plus Tard Possible", //   "As Late As Possible",
      "Doit Commencer Le", //   "Must Start On",
      "Doit Finir Le", //   "Must Finish On",
      "D\u00E9but Au Plus T\u00F4t Le", //   "Start No Earlier Than",
      "D\u00E9but Au Plus Tard Le", //   "Start No Later Than",
      "Fin Au Plus T\u00F4t Le", //   "Finish No Earlier Than",
      "Fin Au Plus Tard Le" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "Nom", //   "Name",
      "WBS", //   "WBS",
      "Niveau hi\u00E9rarchique", //   "Outline Level",
      "Texte1", //   "Text1",
      "Texte2", //   "Text2",
      "Texte3", //   "Text3",
      "Texte4", //   "Text4",
      "Texte5", //   "Text5",
      "Texte6", //   "Text6",
      "Texte7", //   "Text7",
      "Texte8", //   "Text8",
      "Texte9", //   "Text9",
      "Texte10", //   "Text10",
      "Notes", //   "Notes",
      "Contact", //   "Contact",
      "Groupe de Ressources", //   "Resource Group",
      null, //
      null, //
      null, //
      "Travail", //   "Work",
      "Travail Normal", //   "Baseline Work",
      "Travail R\u00E9el", //   "Actual Work",
      "Travail Restant", //   "Remaining Work",
      "Variation de Travail", //   "Work Variance",
      "% Travail achev\u00E9", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "Co\u00FBt", //   "Cost",
      "Co\u00FBt Planifi\u00E9", //   "Baseline Cost",
      "Co\u00FBt R\u00E9el", //   "Actual Cost",
      "Co\u00FBt Restant", //   "Remaining Cost",
      "Variation de Co\u00FBt", //   "Cost Variance",
      "Co\u00FBt Fixe", //   "Fixed Cost",
      "Co\u00FBt1", //   "Cost1",
      "Co\u00FBt2", //   "Cost2",
      "Co\u00FBt3", //   "Cost3",
      null, //
      "Dur\u00E9e", //   "Duration",
      "Dur\u00E9e Planifi\u00E9e", //   "Baseline Duration",
      "Dur\u00E9e R\u00E9elle", //   "Actual Duration",
      "Dur\u00E9e Restante", //   "Remaining Duration",
      "% Achev\u00E9", //   "% Complete",
      "Variation de Dur\u00E9e", //   "Duration Variance",
      "Dur\u00E9e1", //   "Duration1",
      "Dur\u00E9e2", //   "Duration2",
      "Dur\u00E9e3", //   "Duration3",
      null, //
      "D\u00E9but", //   "Start",
      "Fin", //   "Finish",
      "D\u00E9but Au Plus T\u00F4t", //   "Early Start",
      "Fin Au Plus T\u00F4t", //   "Early Finish",
      "D\u00E9but Au Plus Tard", //   "Late Start",
      "Fin Au Plus Tard", //   "Late Finish",
      "D\u00E9but Planifi\u00E9", //   "Baseline Start",
      "Fin Planifi\u00E9e", //   "Baseline Finish",
      "D\u00E9but R\u00E9el", //   "Actual Start",
      "Fin R\u00E9elle", //   "Actual Finish",
      "D\u00E9but1", //   "Start1",
      "Fin1", //   "Finish1",
      "D\u00E9but2", //   "Start2",
      "Fin2", //   "Finish2",
      "D\u00E9but3", //   "Start3",
      "Fin3", //   "Finish3",
      "Marge de D\u00E9but", //   "Start Variance",
      "Marge de Fin", //   "Finish Variance",
      "Date Contrainte", //   "Constraint Date",
      null, //
      "Pr\u00E9d\u00E9cesseurs", //   "Predecessors",
      "Successeurs", //   "Successors",
      "Noms Ressources", //   "Resource Names",
      "Ressources Initiales", //   "Resource Initials",
      "ID Unique des Pr\u00E9d\u00E9cesseurs", //   "Unique ID Predecessors",
      "ID Unique des Successeurs", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "Fixe", //   "Fixed",
      "Jalon", //   "Milestone",
      "Critique", //   "Critical",
      "Marqu\u00E9", //   "Marked",
      "Rollup", //   "Rollup",
      "BCWS", //   "BCWS",
      "BCWP", //   "BCWP",
      "SV", //   "SV",
      "CV", //   "CV",
      null, //
      "ID", //   "ID",
      "Type de Contrainte", //   "Constraint Type",
      "D\u00E9lai", //   "Delay",
      "Marge Libre", //   "Free Slack",
      "Marge Totale", //   "Total Slack",
      "Priorit\u00E9", //   "Priority",
      "Fichier de sous-projet", //   "Subproject File",
      "Projet", //   "Project",
      "ID Unique", //   "Unique ID",
      "Num\u00E9ro Externe", //   "Outline Number",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "Indicateur1", //   "Flag1",
      "Indicateur2", //   "Flag2",
      "Indicateur3", //   "Flag3",
      "Indicateur4", //   "Flag4",
      "Indicateur5", //   "Flag5",
      "Indicateur6", //   "Flag6",
      "Indicateur7", //   "Flag7",
      "Indicateur8", //   "Flag8",
      "Indicateur9", //   "Flag9",
      "Indicateur10", //   "Flag10",
      "Sommaire", //   "Summary",
      "Objets", //   "Objects",
      "Champs Li\u00E9s", //   "Linked Fields",
      "Cacher Barre", //   "Hide Bar",
      null, //
      "Cr\u00E9\u00E9", //   "Created",
      "D\u00E9but4", //   "Start4",
      "Fin4", //   "Finish4",
      "D\u00E9but5", //   "Start5",
      "Fin5", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Confirm\u00E9", //   "Confirmed",
      "Mise \u00E0 Jour N\u00E9cessaire", //   "Update Needed",
      null, //
      null, //
      null, //
      "Numero1", //   "Number1",
      "Numero2", //   "Number2",
      "Numero3", //   "Number3",
      "Numero4", //   "Number4",
      "Numero5", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Stop", //   "Stop",
      "Continuer Pas Plus T\u00F4t Que", //   "Resume No Earlier Than",
      "Continuer" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "Nom", //   "Name",
      "Initiales", //   "Initials",
      "Groupe", //   "Group",
      "Code", //   "Code",
      "Texte1", //   "Text1",
      "Texte2", //   "Text2",
      "Texte3", //   "Text3",
      "Texte4", //   "Text4",
      "Texte5", //   "Text5",
      "Notes", //   "Notes",
      "Adresse de messagerie", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "Travail", //   "Work",
      "Travail Planifi\u00E9", //   "Baseline Work",
      "Travail R\u00E9el", //   "Actual Work",
      "Travail Restant", //   "Remaining Work",
      "Heures sup.", //   "Overtime Work",
      "Variation de Travail", //   "Work Variance",
      "% Travail achev\u00E9", //   "% Work Complete",
      null, //
      null, //
      null, //
      "Co\u00FBt", //   "Cost",
      "Co\u00FBt Planifi\u00E9", //   "Baseline Cost",
      "Co\u00FBt R\u00E9el", //   "Actual Cost",
      "Co\u00FBt Restant", //   "Remaining Cost",
      "Variation de Co\u00FBt", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "ID", //   "ID",
      "Unit\u00E9s Maximales", //   "Max Units",
      "Taux Standard", //   "Standard Rate",
      "Taux heures sup.", //   "Overtime Rate",
      "Co\u00FBt par Utilisation", //   "Cost Per Use",
      "Allocation", //   "Accrue At",
      "En Surcharge", //   "Overallocated",
      "Pointe", //   "Peak",
      "Calendrier de Base", //   "Base Calendar",
      "ID Unique", //   "Unique ID",
      "Objets", //   "Objects",
      "Champs Li\u00E9s", //   "Linked Fields",
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
      },
      {
         LocaleData.PROGRAM_NAME,
         "Microsoft Project for Windows"
      },
      {
         LocaleData.FILE_VERSION,
         "4.0"
      },
      {
         LocaleData.CODE_PAGE,
         CodePage.ANSI
      },

      {
         LocaleData.CURRENCY_SYMBOL,
         ""
      },
      {
         LocaleData.CURRENCY_SYMBOL_POSITION,
         CurrencySymbolPosition.BEFORE
      },
      {
         LocaleData.CURRENCY_DIGITS,
         Integer.valueOf(2)
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
         LocaleData.DATE_ORDER,
         DateOrder.DMY
      },
      {
         LocaleData.TIME_FORMAT,
         ProjectTimeFormat.TWENTY_FOUR_HOUR
      },
      {
         LocaleData.DATE_SEPARATOR,
         "/"
      },
      {
         LocaleData.TIME_SEPARATOR,
         ":"
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
         ProjectDateFormat.DD_MM_YYYY
      },
      {
         LocaleData.BAR_TEXT_DATE_FORMAT,
         Integer.valueOf(0)
      },
      {
         LocaleData.NA,
         "NC"
      },

      {
         LocaleData.YES,
         "Oui"
      },
      {
         LocaleData.NO,
         "Non"
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
