#! /bin/sh

if [ -z "$JAVACMD" ] ; then 
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then 
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=java
  fi
fi
 
if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

if [ -n "$CLASSPATH" ] ; then
  LOCALCLASSPATH="$CLASSPATH"
fi

# add in the dependency .jar files in non-RPM mode (the default)
DIRLIBS=./lib
for i in "${DIRLIBS}"/*.jar
do
  # if the directory is empty, then it will return the input string
  # this is stupid, so case for it
  if [ "$i" != "${DIRLIBS}/*.jar" ] ; then
    if [ -z "$LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH=$i
    else
      LOCALCLASSPATH="$i":"$LOCALCLASSPATH"
    fi
  fi
done

"$JAVACMD" -classpath "$LOCALCLASSPATH" net.sourceforge.cvsgrab.CVSGrab "$@"
