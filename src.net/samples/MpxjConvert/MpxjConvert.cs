using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using net.sf.mpxj;
using net.sf.mpxj.reader;
using net.sf.mpxj.writer;

namespace MpxjSample
{
    class MpxjConvert
    {
        static void Main(string[] args)
        {
          try
          {
             if (args.Length != 3)
             {
                Console.Out.WriteLine ("Usage: MpxjConvert <input file name> <output format> <output file name>");
             }
             else
             {
                MpxjConvert convert = new MpxjConvert();
                convert.process(args[0], FileFormat.valueOf(args[1]), args[3]);
             }
          }

          catch (Exception ex)
          {
              Console.Out.WriteLine(ex.ToString());
          }
        }

        public void process (string inputFile, FileFormat outputFormat, string outputFile)
        {
            Console.Out.WriteLine("Reading input file started.");
            DateTime start = DateTime.Now;
            ProjectFile projectFile = new UniversalProjectReader().read(inputFile);
            TimeSpan elapsed = DateTime.Now - start;
            Console.Out.WriteLine("Reading input file completed in " + elapsed.TotalMilliseconds + "ms.");

            Console.Out.WriteLine("Writing output file started.");
            start = DateTime.Now;
            new UniversalProjectWriter().withFormat(outputFormat).write(projectFile, outputFile);
            elapsed = DateTime.Now - start;
            Console.Out.WriteLine("Writing output completed in " + elapsed.TotalMilliseconds + "ms.");
        }
    }
}
