/*
 * file:       GanttBarDateFormat.java
 * author:     Jon Iles
 * date:       2010-05-20
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

package org.mpxj.mpp;

/**
 * Enumeration representing the formats which may be shown on a Gantt chart timescale.
 *
 * <h3>Format Examples</h3>
 * <table summary="Format Examples" cellpadding="2" cellspacing="3" border="0" >
 * <thead>
 * <tr class="tableSubHeadingColor">
 * <th class="colFirst" align="left">Value</th>
 * <th class="colLast" align="left">Example</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr class="rowColor">
 * <td> {@link #DEFAULT}</td>
 * <td> N/A </td>
 * <tr class="altColor">
 * <td> {@link #DDMMYY_MMSS}</td>
 * <td> 28/01/02 12:33 </td>
 * <tr class="rowColor">
 * <td> {@link #DDMMYY}</td>
 * <td> 28/01/02 </td>
 * <tr class="altColor">
 * <td> {@link #DDMMYYYY}</td>
 * <td> 28/01/2002 </td>
 * <tr class="rowColor">
 * <td> {@link #DD_MMMM_YYYY_HHMM}</td>
 * <td> 28 January 2002 12:33 </td>
 * <tr class="altColor">
 * <td> {@link #DD_MMMM_YYYY}</td>
 * <td> 28 January 2002 </td>
 * <tr class="rowColor">
 * <td> {@link #DD_MMM_HHMM}</td>
 * <td> 28 Jan 12:33 </td>
 * <tr class="altColor">
 * <td> {@link #DD_MMM_YY}</td>
 * <td> 28 Jan '02 </td>
 * <tr class="rowColor">
 * <td> {@link #DD_MMMM}</td>
 * <td> 28 January </td>
 * <tr class="altColor">
 * <td> {@link #DD_MMM}</td>
 * <td> 28 Jan </td>
 * <tr class="rowColor">
 * <td> {@link #DDD_DDMMYY_HHMM}</td>
 * <td> Mon 28/01/02 12:33 </td>
 * <tr class="altColor">
 * <td> {@link #DDD_DDMMYY}</td>
 * <td> Mon 28/01/02 </td>
 * <tr class="rowColor">
 * <td> {@link #DDD_DD_MMM_YY}</td>
 * <td> Mon 28 Jan '02 </td>
 * <tr class="altColor">
 * <td> {@link #DDD_HHMM}</td>
 * <td> Mon 12:33 </td>
 * <tr class="rowColor">
 * <td> {@link #DDD_DD_MMM}</td>
 * <td> Mon 28 Jan </td>
 * <tr class="altColor">
 * <td> {@link #DDD_DDMM}</td>
 * <td> Mon 28/01 </td>
 * <tr class="rowColor">
 * <td> {@link #DDD_DD}</td>
 * <td> Mon 28 </td>
 * <tr class="altColor">
 * <td> {@link #DDMM}</td>
 * <td> 28/01 </td>
 * <tr class="rowColor">
 * <td> {@link #DD}</td>
 * <td> 28 </td>
 * <tr class="altColor">
 * <td> {@link #HHMM}</td>
 * <td> 12:33 </td>
 * <tr class="rowColor">
 * <td> {@link #MWW}</td>
 * <td> 1/W05 </td>
 * <tr class="altColor">
 * <td> {@link #MWWYY_HHMM}</td>
 * <td> 1/W05/02 12:33 </td>
 * </tbody>
 * </table>
 */
public enum GanttBarDateFormat
{
   DEFAULT,
   DDMMYY_MMSS,
   DDMMYY,
   DDMMYYYY,
   DD_MMMM_YYYY_HHMM,
   DD_MMMM_YYYY,
   DD_MMM_HHMM,
   DD_MMM_YY,
   DD_MMMM,
   DD_MMM,
   DDD_DDMMYY_HHMM,
   DDD_DDMMYY,
   DDD_DD_MMM_YY,
   DDD_HHMM,
   DDD_DD_MMM,
   DDD_DDMM,
   DDD_DD,
   DDMM,
   DD,
   HHMM,
   MWW,
   MWWYY_HHMM
}
