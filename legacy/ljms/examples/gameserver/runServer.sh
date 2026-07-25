#!/bin/sh
./jre1.2.2/bin/java -classpath ./server.jar:$(CLASSPATH) Server -cp 6283 -logs /home/httpd/html/chalktalk2
