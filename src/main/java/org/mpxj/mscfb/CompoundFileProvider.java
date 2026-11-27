package org.mpxj.mscfb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public interface CompoundFileProvider
{
   CompoundFile open(InputStream is) throws IOException;

   CompoundFile open(File file) throws IOException;

   CompoundFile open(FileChannel channel) throws IOException;
}
