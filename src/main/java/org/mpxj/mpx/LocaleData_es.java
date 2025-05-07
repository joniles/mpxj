/*
 * file:       LocaleData_es.java
 * author:     Agust\u00EDn Bart\u00F3
 *             Jon Iles
 * copyright:  (c) Packwood Software 2004
 * date:       09/10/2008
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
import java.util.Map;

import org.mpxj.CodePage;
import org.mpxj.CurrencySymbolPosition;
import org.mpxj.DateOrder;
import org.mpxj.ProjectDateFormat;
import org.mpxj.ProjectTimeFormat;

/**
 * This class defines the Spanish resources required by MPX files.
 */
public final class LocaleData_es extends ListResourceBundle
{
   @Override public Object[][] getContents()
   {
      return (RESOURCE_DATA);
   }

   private static final String[][] TIME_UNITS_ARRAY_DATA =
   {
      {
         "m"
      }, // Minutes - Minutos
      {
         "h"
      }, // Hours - Horas
      {
         "d"
      }, // Days - D\u00ED\u00ADas
      {
         "s"
      }, // Weeks - Semanas
      {
         "ms"
      }, // Months - Meses
      {
         "a"
      }, // Years - A\u00F1os
      {
         "%"
      }, // Percent - Porcentaje
      {
         "em"
      }, // Elapsed Minutes - Minutos Transcurridos
      {
         "eh"
      }, // Elapsed Hours - Horas Transcurridos
      {
         "ed"
      }, // Elapsed Days - D\u00C3\u00ADas Transcurridos
      {
         "es"
      }, // Elapsed Weeks - Semanas Transcurridas
      {
         "ems"
      }, // Elapsed Months - Meses Transcurridos
      {
         "ea"
      }, // Elapsed Years - A\u00F1os Transcurridos
      {
         "e%"
      }
            // Elapsed Percent - Porcentaje Transcurridos
   };

   private static final Map<String, Integer> TIME_UNITS_MAP_DATA = new HashMap<>();

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
      "Comienzo", // Start
      "Fin", // End
      "Prorrateo" // Prorated
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "FF", // FF - Fin a fin (FF)
      "FC", // FS - Fin a comienzo (FC)
      "CF", // SF - Comienzo a fin (CF)
      "CC" // SS - Comienzo a comienzo (CC)
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "M\u00ED\u00ADnima", // Lowest
      "Muy Baja", // Very Low
      "M\u00E1s Baja", // Lower
      "Baja", // Low
      "Media", // Medium
      "Alta", // High
      "M\u00E1s Alta", // Higher
      "Muy Alta", // Very High
      "M\u00E1xima", // Highest
      "No Redistribuir" // Do Not Level
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "Lo antes posible", // As Soon As Possible
      "Lo m\u00E1s tarde posible", // As Late As Possible
      "Debe comenzar el", // Must Start On
      "Debe finalizar el", // Must Finish On
      "No comenzar antes del", // Start No Earlier Than
      "No comenzar despu\u00E9s del", // Start No Later Than
      "No finalizar antes del", // Finish No Earlier Than
      "No finalizar despu\u00E9s del" // Finish No Later Than
   };

   // TODO Complete TASK_NAMES_DATA translation
   private static final String[] TASK_NAMES_DATA =
   {
      null, // ???
      "Nombre", // Name
      "WBS", // TODO Translate "WBS"
      "Nivel de Esquema", // Outline Level
      "Texto1", // Text1
      "Texto2", // Text2
      "Texto3", // Text3
      "Texto4", // Text4
      "Texto5", // Text5
      "Texto6", // Text6
      "Texto7", // Text7
      "Texto8", // Text8
      "Texto9", // Text9
      "Texto10", // Text10
      "Notas", // Notes
      "Contacto", // Contact
      "Grupo de Recursos", // Resource Group
      null, // ???
      null, // ???
      null, // ???
      "Trabajo", // Work
      "L\u00EDnea de Base de Trabajo", // Baseline Work
      "Trabajo Real", // Actual Work
      "Trabajo Restante", // Remaining Work
      "Variaci\u00F3n de Trabajo", // Work Variance
      "% Trabajo Completado", // % Work Complete
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Costo", // Cost
      "L\u00ED\u00ADnea de Base de Costo", // Baseline Cost
      "Costo Real", // Actual Cost
      "Costo Restante", // Remaining Cost
      "Variaci\u00F3n de Costo", // Cost Variance
      "Costo Fijo", // Fixed Cost
      "Costo1", // Cost1
      "Costo2", // Cost2
      "Costo3", // Cost3
      null, // ???
      "Duraci\u00F3n", // Duration
      "L\u00EDnea Base de Duraci\u00F3n", // Baseline Duration
      "Duraci\u00F3n Real", // Actual Duration
      "Duraci\u00F3n Restante", // Remaining Duration
      "% Completado", // % Complete
      "Variaci\u00F3n de Duraci\u00F3n", // Duration Variance
      "Duraci\u00F3n1", // Duration1
      "Duraci\u00F3n2", // Duration2
      "Duraci\u00F3n3", // Duration3
      null, // ???
      "Comienzo", // Start
      "Fin", // Finish
      "Comienzo Temprano", // Early Start
      "Fin Temprano", // Early Finish
      "Comienzo Tard\u00ED\u00ADo", // Late Start
      "Fin Tard\u00C3\u00ADo", // Late Finish
      "L\u00ED\u00ADnea de Base de Comienzo", // Baseline Start
      "L\u00EDnea de Base de Fin", // Baseline Finish
      "Comienzo Real", // Actual Start
      "Fin Real", // Actual Finish
      "Comienzo1", // Start1
      "Fin1", // Finish1
      "Comienzo2", // Start2
      "Fin2", // Finish2
      "Comienzo3", // Start3
      "Fin3", // Finish3
      "Variaci\u00F3n de Comienzo", // Start Variance
      "Variaci\u00F3n de Fin", // Finish Variance
      "Fecha de Restricci\u00F3n", // Constraint Date
      null, // ???
      "Predecesoras", // Predecessors
      "Sucesoras", // Successors
      "Nombres de Recursos", // Resource Names
      "Iniciales de Recursos", // Resource Initials
      "Predecesoras de ID \u00DAnico", // Unique ID Predecessors
      "Sucesoras de ID \u00DAnico", // Unique ID Successors
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Fijo", // Fixed
      "Hito", // Milestone
      "Cr\u00ED\u00ADtico", // Critical
      "Marked", // TODO Translate "Marked"
      "Rollup", // TODO Translate "Rollup"
      "BCWS", // TODO Translate "BCWS"
      "BCWP", // TODO Translate "BCWP"
      "SV", // TODO Translate "SV"
      "CV", // TODO Translate "CV"
      null, // ???
      "ID", // TODO Translate "ID"
      "Tipo de Restricci\u00F3n", // Constraint Type
      "Demora", // Delay
      "Free Slack", // TODO Translate "Free Slack"
      "Total Slack", // TODO Translate "Total Slack"
      "Prioridad", // Priority
      "Subproject File", // TODO Translate "Subproject File"
      "Proyecto", // Project
      "ID \u00DAnico", // Unique ID
      "N\u00FAmero de Esquema", // Outline Number
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Indicador1", // Flag1
      "Indicador2", // Flag2
      "Indicador3", // Flag3
      "Indicador4", // Flag4
      "Indicador5", // Flag5
      "Indicador6", // Flag6
      "Indicador7", // Flag7
      "Indicador8", // Flag8
      "Indicador9", // Flag9
      "Indicador10", // Flag10
      "Summary", // TODO Translate "Summary"
      "Objetos", // Objects
      "Campos Enlazados", // Linked Fields
      "Ocultar Barra", // Hide Bar
      null, // ???
      "Creada", // Created
      "Comienzo4", // Start4
      "Fin4", // Finish4
      "Comienzo5", // Start5
      "Fin5", // Finish5
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Confirmed", // TODO Translate "Confirmed"
      "Update Needed", // TODO Translate "Update Needed"
      null, // ???
      null, // ???
      null, // ???
      "N\u00FAmero1", // Number1
      "N\u00FAmero2", // Number2
      "N\u00FAmero3", // Number3
      "N\u00FAmero4", // Number4
      "N\u00FAmero5", // Number5
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Stop", // TODO Translate "Stop"
      "Resume No Earlier Than", // TODO Translate "Resume No Earlier Than"
      "Resume" // TODO Translate "Resume"
   };

   // TODO Complete RESOURCE_NAMES_DATA translation
   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, // ???
      "Nombre", // Name
      "Iniciales", // Initials
      "Grupo", // Group
      "C\u00F3digo", // Code
      "Texto1", // Text1
      "Texto2", // Text2
      "Texto3", // Text3
      "Texto4", // Text4
      "Texto5", // Text5
      "Notas", // Notes
      "Correo electr\u00F3nico", // Email Address
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Trabajo", // Work
      "L\u00EDnea de Base de Trabajo", // Baseline Work
      "Trabajo Real", // Actual Work
      "Trabajo Restante", // Remaining Work
      "Overtime Work", // TODO Translate "Overtime Work"
      "Variaci\u00F3n de Trabajo", // Work Variance
      "% Trabajo Completado", // % Work Complete
      null, // ???
      null, // ???
      null, // ???
      "Costo", // Cost
      "L\u00ED\u00ADnea de Base de Costo", // Baseline Cost
      "Costo Real", // Actual Cost
      "Costo Restante", // Remaining Cost
      "Variaci\u00F3n de Costo", // Cost Variance
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "ID", // TODO Translate "ID"
      "Max Units", // TODO Translate "Max Units"
      "Standard Rate", // TODO Translate "Standard Rate"
      "Overtime Rate", // TODO Translate "Overtime Rate"
      "Costo Por Uso", // Cost Per Use
      "Acumulaci\u00F3n de costos", // Accrue At
      "Sobreasignado", // Overallocated
      "Pico", // Peak
      "Calendario Base", // Base Calendar
      "ID \u00DAnico", // Unique ID
      "Objetos", // Objects
      "Campos Enlazados", // Linked Fields
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ","
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
         "$"
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
         LocaleData.DEFAULT_START_TIME,
         Integer.valueOf(480)
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
         "am"
      },
      {
         LocaleData.PM_TEXT,
         "pm"
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
         "S\u00ED\u00AD"
      },
      {
         LocaleData.NO,
         "No"
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
