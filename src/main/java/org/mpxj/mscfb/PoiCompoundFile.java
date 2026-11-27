/*
 * file:       PoiCompoundFile.java
 * author:     Jon Iles
 * date:       2026-11-27
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

package org.mpxj.mscfb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.CustomProperty;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.NoPropertySetStreamException;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.UnexpectedPropertySetTypeException;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Implementation of the CompoundFile interface using the POI
 * library to work with the file.
 */
final class PoiCompoundFile implements CompoundFile
{
   /**
    * Constructor.
    *
    * @param is InputStream instance
    */
   public PoiCompoundFile(InputStream is) throws IOException
   {
      m_fs = new POIFSFileSystem(is);
   }

   /**
    * Constructor.
    *
    * @param file File instance
    */
   public PoiCompoundFile(File file) throws IOException
   {
      m_fs = new POIFSFileSystem(file);
   }

   /**
    * Constructor.
    *
    * @param channel FileChannel instance
    */
   public PoiCompoundFile(FileChannel channel) throws IOException
   {
      m_fs = new POIFSFileSystem(channel, true, false);
   }

   @Override public List<String> list(String path) throws IOException
   {
      DirectoryNode node = getNode(path);
      String finalPath = path.endsWith("/") ? path : path + "/";
      return node.getEntryNames().stream().map(s -> finalPath + s).sorted().collect(Collectors.toList());
   }

   @Override public InputStream read(String path) throws IOException
   {
      String[] names = getDirAndFileNames(path);
      return new DocumentInputStream((DocumentEntry) getNode(names[0]).getEntry(names[1]));
   }

   @Override public Map<String, String> getSummaryInformation() throws IOException
   {
      if (!m_fs.getRoot().hasEntry(SummaryInformation.DEFAULT_STREAM_NAME))
      {
         return Collections.emptyMap();
      }

      SummaryInformation summaryInformation;
      try
      {
         summaryInformation = new SummaryInformation(new PropertySet(read("/"+ SummaryInformation.DEFAULT_STREAM_NAME)));
      }

      catch (UnexpectedPropertySetTypeException | NoPropertySetStreamException e)
      {
         return Collections.emptyMap();
      }

      Map<String, String> map = new HashMap<>();
      map.put("TITLE", summaryInformation.getTitle());
      map.put("SUBJECT", summaryInformation.getSubject());
      map.put("AUTHOR", summaryInformation.getAuthor());
      map.put("KEYWORDS", summaryInformation.getKeywords());
      map.put("COMMENTS", summaryInformation.getComments());
      map.put("TEMPLATE", summaryInformation.getTemplate());
      map.put("LAST_AUTHOR", summaryInformation.getLastAuthor());
      map.put("REVISION_NUMBER", summaryInformation.getRevNumber());
      map.put("CREATED", getFormattedLocalDateTime(summaryInformation.getCreateDateTime()));
      map.put("LAST_SAVED", getFormattedLocalDateTime(summaryInformation.getLastSaveDateTime()));
      map.put("APPLICATION_NAME", summaryInformation.getApplicationName());
      map.put("EDIT_TIME", String.valueOf(summaryInformation.getEditTime()));
      map.put("LAST_PRINTED", getFormattedLocalDateTime(summaryInformation.getLastPrinted()));

      return map;
   }

   @Override public Map<String, String> getDocumentSummaryInformation() throws IOException
   {
      DocumentSummaryInformation documentSummaryInformation = getDocumentSummaryInformationObject();
      if (documentSummaryInformation == null)
      {
         return Collections.emptyMap();
      }

      Map<String, String> map = new HashMap<>();
      map.put("CATEGORY", documentSummaryInformation.getCategory());
      map.put("PRESENTATION_FORMAT", documentSummaryInformation.getPresentationFormat());
      map.put("MANAGER", documentSummaryInformation.getManager());
      map.put("COMPANY", documentSummaryInformation.getCompany());
      map.put("CONTENT_TYPE", documentSummaryInformation.getContentType());
      map.put("CONTENT_STATUS", documentSummaryInformation.getContentStatus());
      map.put("LANGUAGE", documentSummaryInformation.getLanguage());
      map.put("DOCUMENT_VERSION", documentSummaryInformation.getDocumentVersion());

      return map;
   }

   @Override public Map<String, Object> getCustomProperties() throws IOException
   {
      DocumentSummaryInformation documentSummaryInformation = getDocumentSummaryInformationObject();
      if (documentSummaryInformation == null)
      {
         return Collections.emptyMap();
      }

      Map<String, Object> map = new TreeMap<>();
      CustomProperties customProperties = documentSummaryInformation.getCustomProperties();
      if (customProperties != null)
      {
         for (CustomProperty property : customProperties.properties())
         {
            Object value = property.getValue();
            if (value instanceof Date)
            {
               value = getLocalDateTime((Date) value);
            }
            map.put(property.getName(), value);
         }
      }

      return map;
   }

   @Override public boolean exists(String path) throws IOException
   {
      String[] names = getDirAndFileNames(path);
      return getNode(names[0]).hasEntry(names[1]);
   }

   @Override public boolean isDirectory(String path) throws IOException
   {
      if (path.equals("/"))
      {
         return true;
      }

      String[] names = getDirAndFileNames(path);
      return getNode(names[0]).getEntry(names[1]) instanceof DirectoryEntry;
   }

   @Override public boolean isFile(String path) throws IOException
   {
      if (path.equals("/"))
      {
         return false;
      }

      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      String[] names = getDirAndFileNames(path);
      return getNode(names[0]).getEntry(names[1]) instanceof DocumentEntry;
   }

   @Override public void create(String path, InputStream stream) throws IOException
   {
      String[] names = getDirAndFileNames(path);
      getNode(names[0]).createDocument(names[1], stream);
   }

   @Override public void write(OutputStream stream) throws IOException
   {
      m_fs.writeFilesystem(stream);
   }

   @Override public void delete(String path) throws IOException
   {
      String[] names = getDirAndFileNames(path);
      getNode(names[0]).getEntry(names[1]).delete();
   }

   @Override public void close() throws IOException
   {
      m_fs.close();
   }

   /**
    * Given a path, locate a DirectoryNode within the compound file.
    *
    * @param path target path
    * @return DirectoryNode instance
    */
   private DirectoryNode getNode(String path) throws IOException
   {
      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      DirectoryNode node = m_fs.getRoot();
      if (path.length() > 1)
      {
         String[] folders = path.substring(1).split("/");
         for (String folder : folders)
         {
            Entry entry = node.getEntry(folder);
            if (!entry.isDirectoryEntry())
            {
               throw new IOException(folder + " is not a directory");
            }
            node = (DirectoryNode) entry;
         }
      }

      return node;
   }

   /**
    * Retrieve a DocumentSummaryInformation instance for this compound file.
    *
    * @return DocumentSummaryInformation instance
    */
   private DocumentSummaryInformation getDocumentSummaryInformationObject() throws IOException
   {
      String path = "/" + DocumentSummaryInformation.DEFAULT_STREAM_NAME;
      if (!exists(path))
      {
         return null;
      }

      DocumentSummaryInformation documentSummaryInformation;

      try
      {
         PropertySet ps = new PropertySet(read(path));
         documentSummaryInformation = new DocumentSummaryInformation(ps);
      }

      catch (IllegalStateException | UnexpectedPropertySetTypeException | NoPropertySetStreamException e)
      {
         return null;
      }

      return documentSummaryInformation;
   }

   /**
    * Converta Date to a LocalDateTime.
    *
    * @param date Date instance
    * @return LocalDateTime instance
    */
   private LocalDateTime getLocalDateTime(Date date)
   {
      if (date == null)
      {
         return null;
      }

      return LocalDateTime.ofInstant(date.toInstant(), TimeZone.getDefault().toZoneId());
   }

   /**
    * Format a Date instance as text.
    *
    * @param date Date instance
    * @return text representation
    */
   private String getFormattedLocalDateTime(Date date)
   {
      if (date == null)
      {
         return null;
      }

      return DATE_TIME_FORMAT.format(getLocalDateTime(date));
   }

   /**
    * Given a path of a file, break it down into the parent directory name and the child file name.
    *
    * @param path target path
    * @return array containing the parent directory name and the child file name
    */
   private String[] getDirAndFileNames(String path) throws IOException
   {
      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      int index = path.lastIndexOf("/");
      return new String[] {index == 0 ? "/" : path.substring(0, index), path.substring(index+1)};
   }

   private final POIFSFileSystem m_fs;
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
