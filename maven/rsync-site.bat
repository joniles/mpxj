rem
rem A little bit of DOS mucking about to ensure we get a new
rem window, so rsync can prompt me for a password!
rem
start cmd /k rsync --progress -r -e ssh --delete site/ joniles,mpxj@web.sourceforge.net:htdocs