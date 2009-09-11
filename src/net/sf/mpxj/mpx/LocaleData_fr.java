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

package net.sf.mpxj.mpx;

import java.util.HashMap;
import java.util.ListResourceBundle;

import net.sf.mpxj.CodePage;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class defines the French translation of resource required by MPX files.
 */
public final class LocaleData_fr extends ListResourceBundle
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
      "Début", //   "Start",
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
      "Très Bas", //   "Very Low",
      "Plus Bas", //   "Lower",
      "Bas", //   "Low",
      "Moyen", //   "Medium",
      "Elevé", //   "High",
      "Plus Elevé", //   "Higher",
      "Très Elevé", //   "Very High",
      "Le Plus Elevé", //   "Highest",
      "Ne Pas Niveler" //   "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "Dès Que Possible", //   "As Soon As Possible",
      "Le Plus Tard Possible", //   "As Late As Possible",
      "Doit Commencer Le", //   "Must Start On",
      "Doit Finir Le", //   "Must Finish On",
      "Début Au Plus Tôt Le", //   "Start No Earlier Than",
      "Début Au Plus Tard Le", //   "Start No Later Than",
      "Fin Au Plus Tôt Le", //   "Finish No Earlier Than",
      "Fin Au Plus Tard Le" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "Nom", //   "Name",
      "WBS", //   "WBS",
      "Nível Externo", //   "Outline Level",
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
      "Travail Réel", //   "Actual Work",
      "Travail Restant", //   "Remaining Work",
      "Variation de Travail", //   "Work Variance",
      "% Travail achevé", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "Coût", //   "Cost",
      "Coût Planifié", //   "Baseline Cost",
      "Coût Réel", //   "Actual Cost",
      "Coût Restant", //   "Remaining Cost",
      "Variation de Coût", //   "Cost Variance",
      "Coût Fixe", //   "Fixed Cost",
      "Coût1", //   "Cost1",
      "Coût2", //   "Cost2",
      "Coût3", //   "Cost3",
      null, //
      "Durée", //   "Duration",
      "Durée Planifiée", //   "Baseline Duration",
      "Durée Réelle", //   "Actual Duration",
      "Durée Restante", //   "Remaining Duration",
      "% Achevé", //   "% Complete",
      "Variation de Durée", //   "Duration Variance",
      "Durée1", //   "Duration1",
      "Durée2", //   "Duration2",
      "Durée3", //   "Duration3",
      null, //
      "Début", //   "Start",
      "Fin", //   "Finish",
      "Début Au Plus Tôt", //   "Early Start",
      "Fin Au Plus Tôt", //   "Early Finish",
      "Début Au Plus Tard", //   "Late Start",
      "Fin Au Plus Tard", //   "Late Finish",
      "Début Planifié", //   "Baseline Start",
      "Fin Planifiée", //   "Baseline Finish",
      "Début Réel", //   "Actual Start",
      "Fin Réelle", //   "Actual Finish",
      "Début1", //   "Start1",
      "Fin1", //   "Finish1",
      "Début2", //   "Start2",
      "Fin2", //   "Finish2",
      "Début3", //   "Start3",
      "Fin3", //   "Finish3",
      "Marge de Début", //   "Start Variance",
      "Marge de Fin", //   "Finish Variance",
      "Date Contrainte", //   "Constraint Date",
      null, //
      "Prédécesseurs", //   "Predecessors",
      "Successeurs", //   "Successors",
      "Noms Ressources", //   "Resource Names",
      "Ressources Initiales", //   "Resource Initials",
      "ID Unique des Prédécesseurs",//   "Unique ID Predecessors",
      "ID Unique des Successeurs", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "Fixe", //   "Fixed",
      "Jalon", //   "Milestone",
      "Critique", //   "Critical",
      "Marqué", //   "Marked",
      "Rollup", //   "Rollup",
      "BCWS", //   "BCWS",
      "BCWP", //   "BCWP",
      "SV", //   "SV",
      "CV", //   "CV",
      null, //
      "ID", //   "ID",
      "Type de Contrainte", //   "Constraint Type",
      "Délai", //   "Delay",
      "Marge Libre", //   "Free Slack",
      "Marge Totale", //   "Total Slack",
      "Priorité", //   "Priority",
      "Arquivo Subprojeto", //   "Subproject File",
      "Projet", //   "Project",
      "ID Unique", //   "Unique ID",
      "Numéro Externe", //   "Outline Number",
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
      "Champs Liés", //   "Linked Fields",
      "Cacher Barre", //   "Hide Bar",
      null, //
      "Créé", //   "Created",
      "Début4", //   "Start4",
      "Fin4", //   "Finish4",
      "Début5", //   "Start5",
      "Fin5", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Confirmé", //   "Confirmed",
      "Mise à Jour Nécessaire", //   "Update Needed",
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
      "Continuer Pas Plus Tôt Que", //   "Resume No Earlier Than",
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
      "Travail Planifié", //   "Baseline Work",
      "Travail Réel", //   "Actual Work",
      "Travail Restant", //   "Remaining Work",
      "Heures sup.", //   "Overtime Work",
      "Variation de Travail", //   "Work Variance",
      "% Travail achevé", //   "% Work Complete",
      null, //
      null, //
      null, //
      "Coût", //   "Cost",
      "Coût Planifié", //   "Baseline Cost",
      "Coût Réel", //   "Actual Cost",
      "Coût Restant", //   "Remaining Cost",
      "Variation de Coût", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "ID", //   "ID",
      "Unités Maximales", //   "Max Units",
      "Taux Standard", //   "Standard Rate",
      "Taux heures sup.", //   "Overtime Rate",
      "Coût par Utilisation", //   "Cost Per Use",
      "Resulte em", //   "Accrue At",
      "En Surcharge", //   "Overallocated",
      "Pointe", //   "Peak",
      "Calendrier de Base", //   "Base Calendar",
      "ID Unique", //   "Unique ID",
      "Objets", //   "Objects",
      "Champs Liés", //   "Linked Fields",
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
         "NA"
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
