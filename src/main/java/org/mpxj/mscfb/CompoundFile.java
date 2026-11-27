package org.mpxj.mscfb;

import java.io.Closeable;
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

public class CompoundFile implements Closeable
{
   public CompoundFile(InputStream is) throws IOException
   {
      m_fs = new POIFSFileSystem(is);
   }

   public CompoundFile(File file) throws IOException
   {
      m_fs = new POIFSFileSystem(file);
   }

   public CompoundFile(FileChannel channel) throws IOException
   {
      m_fs = new POIFSFileSystem(channel, true, false);
   }

   public List<String> listFiles(String path) throws IOException
   {
      DirectoryNode node = getNode(path);
      String finalPath = path.endsWith("/") ? path : path + "/";
      return node.getEntryNames().stream().map(s -> finalPath + s).sorted().collect(Collectors.toList());
   }

   public InputStream getInputStream(String path) throws IOException
   {
      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      int index = path.lastIndexOf("/");
      String dir = index == 0 ? "/" : path.substring(0, index);
      String file = path.substring(index+1);

      return new DocumentInputStream((DocumentEntry) getNode(dir).getEntry(file));
   }

   public Map<String, String> getSummaryInformation() throws IOException
   {
      if (!m_fs.getRoot().hasEntry(SummaryInformation.DEFAULT_STREAM_NAME))
      {
         return Collections.emptyMap();
      }

      SummaryInformation summaryInformation;
      try
      {
         summaryInformation = new SummaryInformation(new PropertySet(getInputStream("/"+ SummaryInformation.DEFAULT_STREAM_NAME)));
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

   public Map<String, String> getDocumentSummaryInformation() throws IOException
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

   public Map<String, Object> getCustomProperties() throws IOException
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

   public boolean hasEntry(String path) throws IOException
   {
      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      int index = path.lastIndexOf("/");
      String dir = index == 0 ? "/" : path.substring(0, index);
      String file = path.substring(index+1);

      return getNode(dir).hasEntry(file);
   }

   public boolean isDirectory(String path) throws IOException
   {
      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      if (path.equals("/"))
      {
         return true;
      }

      int index = path.lastIndexOf("/");
      String dir = index == 0 ? "/" : path.substring(0, index);
      String file = path.substring(index+1);

      return getNode(dir).getEntry(file) instanceof DirectoryEntry;
   }

   public boolean isFile(String path) throws IOException
   {
      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      int index = path.lastIndexOf("/");
      String dir = index == 0 ? "/" : path.substring(0, index);
      String file = path.substring(index+1);

      return getNode(dir).getEntry(file) instanceof DocumentEntry;
   }

   public void createDocument(String path, InputStream is) throws IOException
   {
      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      int index = path.lastIndexOf("/");
      String dir = index == 0 ? "/" : path.substring(0, index);
      String file = path.substring(index+1);

      getNode(dir).createDocument(file, is);
   }

   public void writeFilesystem(OutputStream os) throws IOException
   {
      m_fs.writeFilesystem(os);
   }

   public void delete(String path) throws IOException
   {
      if (!path.startsWith("/"))
      {
         throw new IOException("Path must be absolute");
      }

      int index = path.lastIndexOf("/");
      String dir = index == 0 ? "/" : path.substring(0, index);
      String file = path.substring(index+1);

      getNode(dir).getEntry(file).delete();
   }

   @Override public void close() throws IOException
   {
      m_fs.close();
   }

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

   private DocumentSummaryInformation getDocumentSummaryInformationObject() throws IOException
   {
      String path = "/" + DocumentSummaryInformation.DEFAULT_STREAM_NAME;
      if (!hasEntry(path))
      {
         return null;
      }

      DocumentSummaryInformation documentSummaryInformation;

      try
      {
         PropertySet ps = new PropertySet(getInputStream(path));
         documentSummaryInformation = new DocumentSummaryInformation(ps);
      }

      catch (IllegalStateException | UnexpectedPropertySetTypeException | NoPropertySetStreamException e)
      {
         return null;
      }

      return documentSummaryInformation;
   }

   private LocalDateTime getLocalDateTime(Date date)
   {
      if (date == null)
      {
         return null;
      }

      return LocalDateTime.ofInstant(date.toInstant(), TimeZone.getDefault().toZoneId());
   }

   private String getFormattedLocalDateTime(Date date)
   {
      if (date == null)
      {
         return null;
      }

      return DATE_TIME_FORMAT.format(getLocalDateTime(date));
   }

   private final POIFSFileSystem m_fs;
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
