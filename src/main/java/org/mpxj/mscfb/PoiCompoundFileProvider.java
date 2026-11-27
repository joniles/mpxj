package org.mpxj.mscfb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

class PoiCompoundFileProvider implements CompoundFileProvider
{
   @Override public CompoundFile open(InputStream is) throws IOException
   {
      return new PoiCompoundFile(is);
   }

   @Override public CompoundFile open(File file) throws IOException
   {
      return new PoiCompoundFile(file);
   }

   @Override public CompoundFile open(FileChannel channel) throws IOException
   {
      return new PoiCompoundFile(channel);
   }
}
