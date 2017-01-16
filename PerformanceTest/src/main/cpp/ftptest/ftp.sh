#!/bin/sh

i=0
while [ $i -eq 0 ]
do
ftp -v -n 192.168.0.3 << END
user gegege gegege
bin
get testfile
bye
END
done