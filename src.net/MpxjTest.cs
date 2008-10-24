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
            java.lang.System.setProperty("mpxj.junit.datadir", "c:\\java\\mpxj\\junit\\data");
            Test suite = new MPXJTest();
            TestRunner.runAndWait(suite);
        }
    }
}
