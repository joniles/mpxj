/*
 * file:       Relation.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2003
 * date:       14/01/2003
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


/**
 * This class represents the relationship between two tasks. These
 * relationships normally found in the lists of predecessors and
 * successors associated with a task record in an MPX file.
 */
public class Relation
{
   /**
    * Default constructor.
    */
   Relation ()
   {
      m_taskIDValue = 0;
      m_type = FINISH_START;
      m_duration = new MPXDuration (0, TimeUnit.DAYS);
   }

   /**
    * Constructs an instance of this class from a String representation
    * of a relationship.
    *
    * @param relationship String representation of a relationship
    * @throws MPXException normally indicating that parsing the string has failed
    */
   Relation (String relationship)
      throws MPXException
   {
      int index = 0;
      int length = relationship.length();

      //
      // Extract the identifier
      //
      while (index < length && Character.isDigit(relationship.charAt(index)) == true)
      {
         ++index;
      }

      m_taskIDValue = Integer.parseInt(relationship.substring (0, index));


      //
      // If we haven't reached the end, we next expect to find
      // SF, SS, FS, FF
      //
      if (index == length)
      {
         m_type = FINISH_START;
         m_duration = new MPXDuration (0, TimeUnit.DAYS);
      }
      else
      {
         if (index+1 == length)
         {
            throw new MPXException (MPXException.INVALID_FORMAT);
         }

         switch (relationship.charAt(index))
         {
            case 'S':
            {
               switch (relationship.charAt(index+1))
               {
                  case 'S':
                  {
                     m_type = START_START;
                     break;
                  }

                  case 'F':
                  {
                     m_type = START_FINISH;
                     break;
                  }

                  default:
                  {
                     throw new MPXException (MPXException.INVALID_FORMAT);
                  }
               }

               break;
            }

            case 'F':
            {
               switch (relationship.charAt(index+1))
               {
                  case 'S':
                  {
                     m_type = FINISH_START;
                     break;
                  }

                  case 'F':
                  {
                     m_type = FINISH_FINISH;
                     break;
                  }

                  default:
                  {
                     throw new MPXException (MPXException.INVALID_FORMAT);
                  }
               }

               break;
            }

            default:
            {
               throw new MPXException (MPXException.INVALID_FORMAT);
            }
         }

         index += 2;

         if (index == length)
         {
            m_duration = new MPXDuration (0, TimeUnit.DAYS);
         }
         else
         {
            m_duration = new MPXDuration (relationship.substring(index));
         }
      }
   }


   /**
    * This method generates a string in MPX format representing the
    * contents of this record.
    *
    * @return string containing the data for this record in MPX format.
    */
   public String toString ()
   {
      StringBuffer sb = new StringBuffer (Integer.toString(m_taskIDValue));

      if (m_duration.getDuration() != 0 || m_type != FINISH_START)
      {
         switch (m_type)
         {
            case FINISH_START:
            {
               sb.append ("FS");
               break;
            }

            case START_START:
            {
               sb.append ("SS");
               break;
            }

            case START_FINISH:
            {
               sb.append ("SF");
               break;
            }

            case FINISH_FINISH:
            {
               sb.append ("FF");
               break;
            }
         }
      }

      double duration = m_duration.getDuration();
      if (duration != 0)
      {
         if (duration > 0)
         {
            sb.append ('+');
         }
         sb.append (m_duration.toString());
      }

      return (sb.toString());
   }

   /**
    * Method used to retrieve the identifier of the task
    * related to the current task instance.
    *
    * @return task identifier
    */
   public int getTaskIDValue ()
   {
      return (m_taskIDValue);
   }

   /**
    * Method used to set the identifier of the task
    * related to the current task instance.
    *
    * @param id task identifier
    */
   public void setTaskIDValue (int id)
   {
      m_taskIDValue = id;
   }

   /**
    * Method used to retrieve the type of relationship being
    * represented.
    *
    * @return relationship type
    */
   public int getType ()
   {
      return (m_type);
   }

   /**
    * Method used to set the type of relationship being
    * represented.
    *
    * @param type relationship type
    */
   public void setType (int type)
   {
      m_type = type;
   }

   /**
    * This method retrieves the lag duration associated
    * with this relationship.
    *
    * @return lag duration
    */
   public MPXDuration getDuration ()
   {
      return (m_duration);
   }

   /**
    * This method sets the lag duration associated
    * with this relationship.
    *
    * @param duration the lag duration
    */
   public void setDuration (MPXDuration duration)
   {
      m_duration = duration;
   }

   /**
    * Constant representing a finish-finish relationship.
    */
   public static final int FINISH_FINISH = 0;

   /**
    * Constant representing a finish-start relationship.
    */
   public static final int FINISH_START = 1;

   /**
    * Constant representing a start-finish relationship.
    */
   public static final int START_FINISH = 2;

   /**
    * Constant representing a start-start relationship.
    */
   public static final int START_START = 3;



   private int m_taskIDValue;
   private int m_type;
   private MPXDuration m_duration;
}