/*
 * file:       AstaDatabaseReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2011
 * date:       07/04/2011
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

package net.sf.mpxj.asta;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import net.sf.mpxj.common.AutoCloseableHelper;
import net.sf.mpxj.common.JdbcOdbcHelper;
import net.sf.mpxj.common.NumberHelper;
import net.sf.mpxj.common.ResultSetHelper;

/**
 * This class provides a generic front end to read project data from
 * a database.
 *
 * @deprecated renamed to AstaJdbcReader
 */
@Deprecated public final class AstaDatabaseReader extends AstaJdbcReader
{

}