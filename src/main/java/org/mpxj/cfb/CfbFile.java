package org.mpxj.cfb;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class CfbFile implements Closeable
{
   // TEMP
   public CfbFile(POIFSFileSystem fs)
   {
      m_fs = fs;
   }

   public CfbFile(InputStream is) throws IOException
   {
      m_fs = new POIFSFileSystem(is);
   }

   public CfbFile(File file) throws IOException
   {
      m_fs = new POIFSFileSystem(file);
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
      String dir = path.substring(0, index);
      String file = path.substring(index+1);

      return new DocumentInputStream((DocumentEntry) getNode(dir).getEntry(file));
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

   @Override public void close() throws IOException
   {
      m_fs.close();
   }

   private final POIFSFileSystem m_fs;
}
