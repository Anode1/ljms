#
# Main Makefile
#
# Notice - it is not fully functional
#

ROOT = .

MKDIR = mkdir -p 
RM = -rm -f
CP = cp
MV = mv
JAR = jar
JAVA = java
JAVADOC = javadoc
JDEBUGFLAGS	= -g -deprecation
#JDEBUGFLAGS	= -O -depend -nowarn
JAVAH		= javah
CLASSES       =$(ROOT)/classes
CLASSPATH=($CLASSPATH):($ROOT)/classes:
COMPILE_LINE=javac -deprecation -d $(ROOT)/classes -classpath $(CLASSPATH) `find $(ROOT)/src -name '*.java`

# C-compiler stuff
INC=-I/usr/java1.2/include -I/usr/java1.2/include/solaris
CFLAGS= -G -Wall $(INC)

PRODUCTVERSION = 1_2_3
PRODUCTVERSION_DOT = 1.2.3
PRODUCTNAME = ljms
JARFILE = ljms.jar
SAMPLESJARFILE = ljmsSamples.jar
BINZIPFILE = ${PRODUCTNAME}-bin.${PRODUCTVERSION_DOT}.zip
SRCZIPFILE = ${PRODUCTNAME}-src.${PRODUCTVERSION_DOT}.zip
BINTARFILE = ${PRODUCTNAME}-bin.${PRODUCTVERSION_DOT}.tar
SRCTARFILE = ${PRODUCTNAME}-src.${PRODUCTVERSION_DOT}.tar
BINJARFILE = ${PRODUCTNAME}-bin.${PRODUCTVERSION_DOT}.jar
SRCJARFILE = ${PRODUCTNAME}-src.${PRODUCTVERSION_DOT}.jar



# Rules

# create class directory if not present.

.SUFFIXES:
.SUFFIXES: .java .class

classes:
	echo $(COMPILE_LINE);
	COMPILE_LINE
	
everything:
	classes	
	
all:
	for dir in `find . -type d ! -name CVS`; do \
		if (ls $$dir/*.java > /dev/null 2>&1); then \
			echo "Making $$dir"; \
			(cd $$dir; make) \
		fi \
	done

javadoc:
	javadoc -classpath $(CLASSPATH) -sourcepath . -d doc/api -public \
    -version -author -windowtitle "org.is. Java Documentation" `make --no-print-directory package-list`

jar:

prod:

clean:
	find . -name '*.class' -exec rm -f {} \;

cleanall:
	for dir in `find . -type d ! -name CVS`; do \
		if (ls $$dir/*.java > /dev/null 2>&1); then \
			echo "Making $$dir"; \
			(cd $$dir; make clean) \
		fi \
	done

