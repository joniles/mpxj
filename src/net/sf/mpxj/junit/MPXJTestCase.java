/*
 * file:       MPXJTestCase.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2002-2006
 * date:       24/02/2006
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

import junit.framework.TestCase;

/**
 * Base class implementing common test case functionality.
 */
public abstract class MPXJTestCase extends TestCase
{
   /**
    * Constructor. Note that the system property mpxj.junit.datadir must
    * be defined to allow the test code to find the required sample files.
    */
   public MPXJTestCase()
   {
      m_basedir = System.getProperty("mpxj.junit.datadir");
      if (m_basedir == null || m_basedir.length() == 0)
      {
         assertTrue("missing datadir property", false);
      }

      String runtime = System.getProperty("java.runtime.name");
      m_ikvm = (runtime != null && runtime.indexOf("IKVM") != -1);
   }

   protected String m_basedir;
   protected boolean m_ikvm;
}
