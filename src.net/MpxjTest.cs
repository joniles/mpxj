using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using junit.framework;
using junit.textui;
using net.sf.mpxj.junit;

namespace MpxjSample
{
    class MpxjTest
    {
        static void Main(string[] args)
        {
            if (args.Length == 0 || args.Length > 2)
            {
                Console.Out.WriteLine("Usage: MpxjTest <mpxj test data directory> [<private test data directory>]");
            }
            else
            {
                java.lang.System.setProperty("mpxj.junit.datadir", args[0]);
                if (args.Length == 2)
                {
                    java.lang.System.setProperty("mpxj.junit.privatedir", args[1]);
                }
                Test suite = new MPXJTest();
                TestRunner.runAndWait(suite);
            }
        }
    }
}
