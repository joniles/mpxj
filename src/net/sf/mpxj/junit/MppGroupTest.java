/*
 * file:       MppGroupTest.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       24 January 2007
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.mpxj.Group;
import net.sf.mpxj.GroupClause;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.mpp.ColorType;
import net.sf.mpxj.mpp.FontStyle;
import net.sf.mpxj.mpp.BackgroundPattern;
import net.sf.mpxj.mpp.MPPReader;

/**
 * Tests to exercise MPP file read functionality for various versions of
 * MPP file.
 */
public class MppGroupTest extends MPXJTestCase
{

   /**
    * Test group data read from an MPP9 file.
    * 
    * @throws Exception
    */
   public void testMpp9Groups() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp9group.mpp");
      testGroups(mpp);
   }

   /**
    * Test group data read from an MPP12 file.
    * 
    * @throws Exception
    */
   public void testMpp12Groups() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp12group.mpp");
      testGroups(mpp);
   }

   /**
    * Test group data read from an MPP14 file.
    * 
    * @throws Exception
    */
   public void testMpp14Groups() throws Exception
   {
      ProjectFile mpp = new MPPReader().read(m_basedir + "/mpp14group.mpp");
      testGroups(mpp);
   }

   /**
    * Test group data.
    * 
    * @param mpp ProjectFile instance
    */
   private void testGroups(ProjectFile mpp)
   {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

      Group group = mpp.getGroupByName("Group 1");
      assertNotNull(group);
      assertEquals("Group 1", group.getName());
      assertFalse(group.getShowSummaryTasks());

      List<GroupClause> clauses = group.getGroupClauses();
      assertNotNull(clauses);
      assertEquals(6, clauses.size());

      //
      // Test clause 1
      //
      GroupClause clause = clauses.get(0);
      assertEquals(TaskField.DURATION1, clause.getField());
      assertTrue(clause.getAscending());
      FontStyle font = clause.getFont();
      assertEquals("Arial", font.getFontBase().getName());
      assertEquals(8, font.getFontBase().getSize());
      assertTrue(font.getBold());
      assertFalse(font.getItalic());
      assertFalse(font.getUnderline());
      assertEquals(ColorType.BLACK.getColor(), font.getColor());
      assertEquals(ColorType.YELLOW.getColor(), clause.getCellBackgroundColor());
      assertEquals(1, clause.getGroupOn());
      assertEquals(1, ((Double) clause.getStartAt()).intValue());
      assertEquals(2, ((Double) clause.getGroupInterval()).intValue());
      assertEquals(BackgroundPattern.DOTTED, clause.getPattern());

      //
      // Test clause 2
      //
      clause = clauses.get(1);
      assertEquals(TaskField.NUMBER1, clause.getField());
      assertFalse(clause.getAscending());
      font = clause.getFont();
      assertEquals("Arial", font.getFontBase().getName());
      assertEquals(8, font.getFontBase().getSize());
      assertTrue(font.getBold());
      assertFalse(font.getItalic());
      assertFalse(font.getUnderline());
      assertEquals(ColorType.BLACK.getColor(), font.getColor());
      assertEquals(ColorType.SILVER.getColor(), clause.getCellBackgroundColor());
      assertEquals(1, clause.getGroupOn());
      assertEquals(3, ((Double) clause.getStartAt()).intValue());
      assertEquals(4, ((Double) clause.getGroupInterval()).intValue());
      assertEquals(BackgroundPattern.CHECKERED, clause.getPattern());

      //
      // Test clause 3
      //
      clause = clauses.get(2);
      assertEquals(TaskField.COST1, clause.getField());
      assertTrue(clause.getAscending());
      font = clause.getFont();
      assertEquals("Arial", font.getFontBase().getName());
      assertEquals(8, font.getFontBase().getSize());
      assertTrue(font.getBold());
      assertFalse(font.getItalic());
      assertFalse(font.getUnderline());
      assertEquals(ColorType.BLACK.getColor(), font.getColor());
      assertEquals(ColorType.YELLOW.getColor(), clause.getCellBackgroundColor());
      assertEquals(1, clause.getGroupOn());
      assertEquals(5, ((Double) clause.getStartAt()).intValue());
      assertEquals(6, ((Double) clause.getGroupInterval()).intValue());
      assertEquals(BackgroundPattern.LIGHTDOTTED, clause.getPattern());

      //
      // Test clause 4
      //
      clause = clauses.get(3);
      assertEquals(TaskField.PERCENT_COMPLETE, clause.getField());
      assertFalse(clause.getAscending());
      font = clause.getFont();
      assertEquals("Arial", font.getFontBase().getName());
      assertEquals(8, font.getFontBase().getSize());
      assertTrue(font.getBold());
      assertFalse(font.getItalic());
      assertFalse(font.getUnderline());
      assertEquals(ColorType.BLACK.getColor(), font.getColor());
      assertEquals(ColorType.SILVER.getColor(), clause.getCellBackgroundColor());
      assertEquals(1, clause.getGroupOn());
      assertEquals(7, ((Integer) clause.getStartAt()).intValue());
      assertEquals(8, ((Integer) clause.getGroupInterval()).intValue());
      assertEquals(BackgroundPattern.SOLID, clause.getPattern());

      //
      // Test clause 5
      //
      clause = clauses.get(4);
      assertEquals(TaskField.FLAG1, clause.getField());
      assertTrue(clause.getAscending());
      font = clause.getFont();
      assertEquals("Arial", font.getFontBase().getName());
      assertEquals(8, font.getFontBase().getSize());
      assertTrue(font.getBold());
      assertFalse(font.getItalic());
      assertFalse(font.getUnderline());
      assertEquals(ColorType.BLACK.getColor(), font.getColor());
      assertEquals(ColorType.YELLOW.getColor(), clause.getCellBackgroundColor());
      assertEquals(BackgroundPattern.DOTTED, clause.getPattern());

      //
      // Test clause 6
      //
      clause = clauses.get(5);
      assertEquals(TaskField.DATE1, clause.getField());
      assertFalse(clause.getAscending());
      font = clause.getFont();
      assertEquals("Arial", font.getFontBase().getName());
      assertEquals(8, font.getFontBase().getSize());
      assertTrue(font.getBold());
      assertFalse(font.getItalic());
      assertFalse(font.getUnderline());
      assertEquals(ColorType.BLACK.getColor(), font.getColor());
      assertEquals(ColorType.SILVER.getColor(), clause.getCellBackgroundColor());
      assertEquals(1, clause.getGroupOn());
      assertEquals("07/02/2006 00:00", df.format((Date) clause.getStartAt()));
      assertEquals(10, ((Integer) clause.getGroupInterval()).intValue());
      assertEquals(BackgroundPattern.CHECKERED, clause.getPattern());
   }
}
