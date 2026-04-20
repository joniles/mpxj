/*
 * file:       AstaData.java
 * author:     Jon Iles
 * date:       2026-04-20
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

package org.mpxj.asta;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This interface is implemented by classes to allow
 * data to be read from an Asta database.
 */
interface AstaData
{
   /**
    * Retrieve rows from a table with an optional where clause.
    *
    * @param tableName table name
    * @param keys name value pairs used to construct a where clause
    * @return list of Row instances
    */
   List<Row> getRows(String tableName, Map<String, Integer> keys) throws AstaDatabaseException;

   /**
    * Retrieve rows from a table with an optional where clause.
    * Column names can be normalised using the supplied name map.
    *
    * @param table table name
    * @param keys name value pairs used to construct a where clause
    * @param nameMap column name mapping
    * @return list of Row instances
    */
   List<Row> getRows(String table, Map<String, Integer> keys, Map<String, String> nameMap) throws AstaDatabaseException;

   /**
    * Allocate any resources necessary to read from a database file.
    *
    * @param file database file
    */
   void allocateResources(File file) throws AstaDatabaseException;

   /**
    * Release any previously allocated resources.
    */
   void releaseResources();
}
