/*
 * file:       GroupClause.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       17/01/2007
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

package net.sf.mpxj;

import java.awt.Color;

import net.sf.mpxj.mpp.BackgroundPattern;
import net.sf.mpxj.mpp.FontStyle;

/**
 * This class represents a clause from a definition of a group.
 */
public final class GroupClause
{
   /**
    * Retrieve a flag indicating that values are grouped
    * in ascending order.
    *
    * @return boolean flag
    */
   public boolean getAscending()
   {
      return m_ascending;
   }

   /**
    * Sets a flag indicating that values are grouped
    * in ascending order.
    *
    * @param ascending boolean flag
    */
   public void setAscending(boolean ascending)
   {
      m_ascending = ascending;
   }

   /**
    * Retrieves the background color.
    *
    * @return background color
    */
   public Color getCellBackgroundColor()
   {
      return m_cellBackgroundColor;
   }

   /**
    * Sets the background color.
    *
    * @param color background color.
    */
   public void setCellBackgroundColor(Color color)
   {
      m_cellBackgroundColor = color;
   }

   /**
    * Retrieve the grouping field.
    *
    * @return grouping field
    */
   public FieldType getField()
   {
      return m_field;
   }

   /**
    * Set the grouping field.
    *
    * @param field grouping field
    */
   public void setField(FieldType field)
   {
      m_field = field;
   }

   /**
    * Retrieve the font.
    *
    * @return font
    */
   public FontStyle getFont()
   {
      return m_font;
   }

   /**
    * Retrieve the font.
    *
    * @param font font
    */
   public void setFont(FontStyle font)
   {
      m_font = font;
   }

   /**
    * Retrieve the group interval.
    *
    * @return group interval
    */
   public Object getGroupInterval()
   {
      return m_groupInterval;
   }

   /**
    * Sets the group interval.
    *
    * @param groupInterval group interval
    */
   public void setGroupInterval(Object groupInterval)
   {
      m_groupInterval = groupInterval;
   }

   /**
    * Retrieves the group on value.
    *
    * @return group on value
    */
   public int getGroupOn()
   {
      return m_groupOn;
   }

   /**
    * Sets the group on value.
    *
    * @param groupOn group on value
    */
   public void setGroupOn(int groupOn)
   {
      m_groupOn = groupOn;
   }

   /**
    * Retrieves the pattern.
    *
    * @return pattern
    */
   public BackgroundPattern getPattern()
   {
      return m_pattern;
   }

   /**
    * Sets the pattern.
    *
    * @param pattern pattern
    */
   public void setPattern(BackgroundPattern pattern)
   {
      m_pattern = pattern;
   }

   /**
    * Retrieves the "start at" value.
    *
    * @return "start at" value
    */
   public Object getStartAt()
   {
      return m_startAt;
   }

   /**
    * Sets the "start at" value.
    *
    * @param startAt "start at" value
    */
   public void setStartAt(Object startAt)
   {
      m_startAt = startAt;
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("[GroupClause field=");
      sb.append(m_field);
      sb.append(" ascending=");
      sb.append(m_ascending);
      sb.append(" font=");
      sb.append(m_font);
      sb.append(" color=");
      sb.append(m_cellBackgroundColor);
      sb.append(" pattern=");
      sb.append(m_pattern);
      sb.append(" groupOn=");
      sb.append(m_groupOn);
      sb.append(" startAt=");
      sb.append(m_startAt);
      sb.append(" groupInterval=");
      sb.append(m_groupInterval);
      return (sb.toString());
   }

   private FieldType m_field;
   private boolean m_ascending;
   private FontStyle m_font;
   private Color m_cellBackgroundColor;
   private BackgroundPattern m_pattern;
   private int m_groupOn; // TODO can we do this as an enumeration?
   private Object m_startAt;
   private Object m_groupInterval;
}
