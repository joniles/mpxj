/*
 * file:       UniversalProjectWriter.java
 * author:     Jon Iles
 * date:       2024-05-10
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


package net.sf.mpxj.writer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.json.JsonWriter;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.primavera.PrimaveraPMFileWriter;
import net.sf.mpxj.primavera.PrimaveraXERFileWriter;
import net.sf.mpxj.sdef.SDEFWriter;

/**
 * Provides a wrapper around the writer classes implemented by MPXJ
 * to simplify their use.
 * <p/>
 * For example: {@code new UniversalProjectWriter.withFormat(ProjectWriterFileFormat.MPX).write(projectFile, fileName)}
 */
public final class UniversalProjectWriter implements ProjectWriter
{
   /**
    * Call this method to set the desired file format.
    * Returns this instance to allow method chaining.
    *
    * @param format require format
    * @return this instance
    */
   public ProjectWriter withFormat(FileFormat format)
   {
      m_format = format;
      return this;
   }

   @Override public void write(ProjectFile projectFile, String fileName) throws IOException
   {
      getWriter().write(projectFile, fileName);
   }

   @Override public void write(ProjectFile projectFile, File file) throws IOException
   {
      getWriter().write(projectFile, file);
   }

   @Override public void write(ProjectFile projectFile, OutputStream outputStream) throws IOException
   {
      getWriter().write(projectFile, outputStream);
   }

   private ProjectWriter getWriter()
   {
      if (m_format == null)
      {
         throw new IllegalArgumentException("No file format specified");
      }

      Supplier<ProjectWriter> supplier = WRITER_MAP.get(m_format);
      if (supplier == null)
      {
         throw new IllegalArgumentException("No file format found");
      }

      return supplier.get();
   }

   private FileFormat m_format;

   private static final Map<FileFormat, Supplier<ProjectWriter>> WRITER_MAP = new HashMap<>();
   static
   {
      WRITER_MAP.put(FileFormat.JSON, JsonWriter::new);
      WRITER_MAP.put(FileFormat.MPX, MPXWriter::new);
      WRITER_MAP.put(FileFormat.MSPDI, MSPDIWriter::new);
      WRITER_MAP.put(FileFormat.PLANNER, PlannerWriter::new);
      WRITER_MAP.put(FileFormat.PMXML, PrimaveraPMFileWriter::new);
      WRITER_MAP.put(FileFormat.XER, PrimaveraXERFileWriter::new);
      WRITER_MAP.put(FileFormat.SDEF, SDEFWriter::new);
   }
}
