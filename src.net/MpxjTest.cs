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
            if (args.Length != 1)
            {
                Console.Out.WriteLine("Usage: MpxjTest <test data directory>");
            }
            else
            {
                java.lang.System.setProperty("mpxj.junit.datadir", args[0]);
                Test suite = new MPXJTest();
                TestRunner.runAndWait(suite);
            }
        }
    }
}
