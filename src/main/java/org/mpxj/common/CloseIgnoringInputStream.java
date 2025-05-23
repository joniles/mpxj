// Code copied from POI as POI no longer wish to maintain it.
// https://bz.apache.org/bugzilla/show_bug.cgi?id=66007

/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.mpxj.common;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * A wrapper around an {@link InputStream}, which
 *  ignores close requests made to it.
 *
 * Useful with {@link org.apache.poi.poifs.filesystem.POIFSFileSystem}, where you want
 *  to control the close yourself.
 */
public class CloseIgnoringInputStream extends FilterInputStream
{
   /**
    * Constructor.
    *
    * @param is input stream to wrap.
    */
   public CloseIgnoringInputStream(InputStream is)
   {
      super(is);
   }

   @Override public void close()
   {
      // Does nothing and ignores closing the wrapped stream
   }
}
