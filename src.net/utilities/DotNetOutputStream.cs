using System.IO;

namespace net.sf.mpxj.MpxjUtilities
{
    /// <summary>
    /// Implements a wrapper around a .Net stream allowing it to be used with MPXJ
    /// where a Java OutputStream is expected.
    /// This code is based on DotNetOutputStream.java from the Saxon project http://www.sf.net/projects/saxon
    /// </summary>
    public class DotNetOutputStream : java.io.OutputStream
    {
        private Stream stream;

        public DotNetOutputStream(Stream stream)
        {
            this.stream = stream;
        }

        public override void write(int b)
        {
            stream.WriteByte((byte)b);
        }

        public override void write(byte[] b, int off, int len)
        {
            stream.Write(b, off, len);
        }

        public override void write(byte[] b)
        {
            stream.Write(b, 0, b.Length);
        }

        public override void flush()
        {
            stream.Flush();
        }

        public override void close()
        {
            stream.Close();
        }
    }
}
