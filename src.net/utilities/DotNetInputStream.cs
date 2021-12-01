using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace net.sf.mpxj.MpxjUtilities
{
    /// <summary>
    /// Implements a wrapper around a .Net stream allowing it to be used with MPXJ
    /// where a Java InputStream is expected.
    /// This code is based on DotNetInputStream.java from the Saxon project http://www.sf.net/projects/saxon
    /// Note that I've provided this class as a convenience so there are a matching pair of
    /// input/output stream wrapper shopped with MPXJ. IKVM also ships with an input stream wrapper:
    /// ikvm.io.InputStreamWrapper, which you could use instead of this one.
    /// </summary>
    public class DotNetInputStream : java.io.InputStream
    {
        private Stream stream;
        private long currentOffset;
        private long markedOffset = 0;

        public DotNetInputStream(Stream stream)
        {
            this.stream = stream;
        }

        public override int read()
        {
            int i = stream.ReadByte();
            if (i != -1)
            {
                currentOffset++;
                return i;
            }
            else
            {
                return -1;
            }
        }

        public override int read(byte[] b, int off, int len)
        {
            int i = stream.Read(b, off, len);
            if (i > 0)
            {
                currentOffset += i;
                return i;
            }
            else
            {
                return -1;
            }
        }

        public override bool markSupported()
        {
            return stream.CanSeek;
        }

        public override void mark(int readlimit)
        {
            markedOffset = currentOffset;
        }

        public override void reset()
        {
            currentOffset = markedOffset;
            stream.Seek(markedOffset, SeekOrigin.Begin);
        }

        public override void close()
        {
            stream.Close();
        }
    }
}
