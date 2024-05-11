using System;
using System.Collections.Generic;
using System.IO;
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
             if (args.Length != 2)
             {
                Console.Out.WriteLine ("Usage: MpxjConvert <input file name> <output file name>");
             }
             else
             {
                MpxjConvert convert = new MpxjConvert();
                convert.process(args[0],  args[1]);
             }
          }

          catch (Exception ex)
          {
              Console.Out.WriteLine(ex.ToString());
          }
        }

        public void process (string inputFile, string outputFile)
        {
            Console.Out.WriteLine("Reading input file started.");
            DateTime start = DateTime.Now;
            ProjectFile projectFile = new UniversalProjectReader().read(inputFile);
            TimeSpan elapsed = DateTime.Now - start;
            Console.Out.WriteLine("Reading input file completed in " + elapsed.TotalMilliseconds + "ms.");

            if (projectFile == null)
            {
                throw new ArgumentException("Unsupported file type");
            }

            var extension = Path.GetExtension(outputFile);
            if (extension == null || extension == "")
            {
                throw new ArgumentException($"Filename has no extension {outputFile}");
            }

            FileFormatMap.TryGetValue(extension, out var format);
            if (format == null)
            {
                throw new ArgumentException($"Cannot write files of type: {extension}");
            }

            Console.Out.WriteLine("Writing output file started.");
            start = DateTime.Now;
            new UniversalProjectWriter().withFormat(format).write(projectFile, outputFile);
            elapsed = DateTime.Now - start;
            Console.Out.WriteLine("Writing output completed in " + elapsed.TotalMilliseconds + "ms.");
        }

        private static readonly Dictionary<string, FileFormat> FileFormatMap = new Dictionary<string, FileFormat>()
        {
            { "MPX", FileFormat.MPX },
            { "XML", FileFormat.MSPDI },
            { "PMXML", FileFormat.PMXML },
            { "PLANNER", FileFormat.PLANNER },
            { "JSON", FileFormat.JSON },
            { "SDEF", FileFormat.SDEF },
            { "XER", FileFormat.XER }
        };
    }
}
