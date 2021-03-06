from __future__ import absolute_import

import os
import jpype
import jpype.imports

from jpype import *

mpxj_dir = os.path.join(os.path.dirname(__file__), "lib")
dirpath, _, filenames = next(os.walk(mpxj_dir))

for filename in filenames:
	jpype.addClassPath(os.path.join(dirpath, filename))
