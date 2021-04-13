#!/bin/sh

# See: https://stackoverflow.com/questions/242538/unix-shell-script-find-out-which-directory-the-script-file-resides
SCRIPTPATH=$(dirname "$0")

java -cp "$SCRIPTPATH/../lib/*:$SCRIPTPATH/../mpxj.jar" $1 $2 $3 $4 $5 $6 $7 $8 $9