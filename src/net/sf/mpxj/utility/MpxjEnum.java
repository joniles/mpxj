/*
 * file:       MpxjEnum.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       2007-11-09
 */

package net.sf.mpxj.utility;

/**
 * This interface defines the common features of enums used by MPXJ.
 */
public interface MpxjEnum
{
   /**
    * This method is used to retrieve the int value (not the ordinal)
    * associated with an enum instance.
    * 
    * @return enum value
    */
   public int getValue();

}
