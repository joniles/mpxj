/*
 * file:       LocaleData_pt.java
 * author:     Cláudio Engelsdorff Avila
 *             Jon Iles
 * copyright:  (c) Packwood Software 2004
 * date:       16/08/2004
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
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class defines the Portuguese translation of resource required by MPX files.
 */
public final class LocaleData_pt extends ListResourceBundle
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
         "d"
      },
      {
         "s"
      },
      {
         "mes"
      },
      {
         "a"
      },
      {
         "%"
      },
      {
         "em"
      },
      {
         "eh"
      },
      {
         "ed"
      },
      {
         "es"
      },
      {
         "emes"
      },
      {
         "ea"
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
      "Inicio", //   "Start",
      "Fim", //   "End",
      "" //   "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "TT", //   "FF",
      "TI", //   "FS",
      "IT", //   "SF",
      "II" //   "SS"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "Muito Baixa", //   "Lowest",
      "Muito Baixo", //   "Very Low",
      "Lower", //   "Lower",
      "Baixo", //   "Low",
      "Média", //   "Medium",
      "Alta", //   "High",
      "Higher", //   "Higher",
      "Muito Alta", //   "Very High",
      "Altíssimo", //   "Highest",
      "Não Nivelar" //   "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "O Mais Breve Possível", //   "As Soon As Possible",
      "O Mais Tarde Possível", //   "As Late As Possible",
      "Deve Iniciar Em", //   "Must Start On",
      "Deve Terminar Em", //   "Must Finish On",
      "Não Iniciar Antes De", //   "Start No Earlier Than",
      "Não Iniciar Depois De", //   "Start No Later Than",
      "Não Terminar Antes De", //   "Finish No Earlier Than",
      "Não Terminar Depois De" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "Nome", //   "Name",
      "WBS", //   "WBS",
      "Nível Externo", //   "Outline Level",
      "Texto1", //   "Text1",
      "Texto2", //   "Text2",
      "Texto3", //   "Text3",
      "Texto4", //   "Text4",
      "Texto5", //   "Text5",
      "Texto6", //   "Text6",
      "Texto7", //   "Text7",
      "Texto8", //   "Text8",
      "Texto9", //   "Text9",
      "Texto10", //   "Text10",
      "Anotações", //   "Notes",
      "Contacto", //   "Contact",
      "Grupo de Recursos", //   "Resource Group",
      null, //
      null, //
      null, //
      "Trabalho", //   "Work",
      "Linha Base de Trabalho", //   "Baseline Work",
      "Trabalho Realizado", //   "Actual Work",
      "Trabalho Restante", //   "Remaining Work",
      "Variação Trabalho", //   "Work Variance",
      "% Trabalho Completo", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "Custo", //   "Cost",
      "Linha de Base de Custo", //   "Baseline Cost",
      "Custo Real", //   "Actual Cost",
      "Custo Restante", //   "Remaining Cost",
      "Variação de Custo", //   "Cost Variance",
      "Custo Fixo", //   "Fixed Cost",
      "Custo1", //   "Cost1",
      "Custo2", //   "Cost2",
      "Custo3", //   "Cost3",
      null, //
      "Duração", //   "Duration",
      "Linha Base de Duração", //   "Baseline Duration",
      "Duração Real", //   "Actual Duration",
      "Duração Restante", //   "Remaining Duration",
      "% Completo", //   "% Complete",
      "Variação de Duração", //   "Duration Variance",
      "Duração1", //   "Duration1",
      "Duração2", //   "Duration2",
      "Duração3", //   "Duration3",
      null, //
      "Inicio", //   "Start",
      "Fim", //   "Finish",
      "Inicio Cedo", //   "Early Start",
      "Fim Cedo", //   "Early Finish",
      "Inicio Tardio", //   "Late Start",
      "Fim Tardio", //   "Late Finish",
      "Inicio Linha de Base", //   "Baseline Start",
      "Fim Linha de Base", //   "Baseline Finish",
      "Inicio Real", //   "Actual Start",
      "Fim Real", //   "Actual Finish",
      "Inicio1", //   "Start1",
      "Fim1", //   "Finish1",
      "Inicio2", //   "Start2",
      "Fim2", //   "Finish2",
      "Inicio3", //   "Start3",
      "Fim3", //   "Finish3",
      "Variação Inicio", //   "Start Variance",
      "Variação Fim", //   "Finish Variance",
      "Limitação de Data", //   "Constraint Date",
      null, //
      "Predecessores", //   "Predecessors",
      "Sucessores", //   "Successors",
      "Nome dos Recursos", //   "Resource Names",
      "Iniciais dos Recursos", //   "Resource Initials",
      "ID Unico dos Predecessores",//   "Unique ID Predecessors",
      "ID Unico dos Sucessores", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "Fixado", //   "Fixed",
      "Milestone", //   "Milestone",
      "Crítico", //   "Critical",
      "Marcado", //   "Marked",
      "Rollup", //   "Rollup",
      "BCWS", //   "BCWS",
      "BCWP", //   "BCWP",
      "SV", //   "SV",
      "CV", //   "CV",
      null, //
      "ID", //   "ID",
      "Tipo de Limitação", //   "Constraint Type",
      "Espera", //   "Delay",
      "Folga Livre", //   "Free Slack",
      "Total Folga", //   "Total Slack",
      "Prioridade", //   "Priority",
      "Arquivo Subprojeto", //   "Subproject File",
      "Projeto", //   "Project",
      "ID Unico", //   "Unique ID",
      "Número Externo", //   "Outline Number",
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
      "Flag1", //   "Flag1",
      "Flag2", //   "Flag2",
      "Flag3", //   "Flag3",
      "Flag4", //   "Flag4",
      "Flag5", //   "Flag5",
      "Flag6", //   "Flag6",
      "Flag7", //   "Flag7",
      "Flag8", //   "Flag8",
      "Flag9", //   "Flag9",
      "Flag10", //   "Flag10",
      "Sumário", //   "Summary",
      "Objetos", //   "Objects",
      "Campos Ligados", //   "Linked Fields",
      "Esconder Barra", //   "Hide Bar",
      null, //
      "Criado", //   "Created",
      "Inicio4", //   "Start4",
      "Fim4", //   "Finish4",
      "Inicio5", //   "Start5",
      "Fim5", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Confirmado", //   "Confirmed",
      "Atualização Necessária", //   "Update Needed",
      null, //
      null, //
      null, //
      "Número1", //   "Number1",
      "Número2", //   "Number2",
      "Número3", //   "Number3",
      "Número4", //   "Number4",
      "Número5", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Pare", //   "Stop",
      "Não Continuar Antes de", //   "Resume No Earlier Than",
      "Continuar" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "Nome", //   "Name",
      "Iniciais", //   "Initials",
      "Grupo", //   "Group",
      "Código", //   "Code",
      "Texto1", //   "Text1",
      "Texto2", //   "Text2",
      "Texto3", //   "Text3",
      "Texto4", //   "Text4",
      "Texto5", //   "Text5",
      "Anotações", //   "Notes",
      "Endereço Email", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "Trabaho", //   "Work",
      "Linha Base de Trabalho", //   "Baseline Work",
      "Trabalho Real", //   "Actual Work",
      "Trabalho Restante", //   "Remaining Work",
      "Trabalho Extra", //   "Overtime Work",
      "Variação Trabalho", //   "Work Variance",
      "% Trabalho Completo", //   "% Work Complete",
      null, //
      null, //
      null, //
      "Custo", //   "Cost",
      "Linha Base de Custo", //   "Baseline Cost",
      "Custo Real", //   "Actual Cost",
      "Custo Restante", //   "Remaining Cost",
      "Variação de Custo", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "ID", //   "ID",
      "Máximo de Unidades", //   "Max Units",
      "Taxa Padrão", //   "Standard Rate",
      "Taxa de Tempo Extra", //   "Overtime Rate",
      "Custo por Uso", //   "Cost Per Use",
      "Resulte em", //   "Accrue At",
      "Sobrecarregado", //   "Overallocated",
      "Pico", //   "Peak",
      "Calendário Base", //   "Base Calendar",
      "ID Unico", //   "Unique ID",
      "Objetos", //   "Objects",
      "Campos Ligados", //   "Linked Fields",
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
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
         "Sim"
      },
      {
         LocaleData.NO,
         "Não"
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
