#! /bin/sh

# OS specific support
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Home"
           fi
           ;;
esac

# For Cygwin, ensure paths are in UNIX format
if $cygwin ; then
  [ -n "$ANT_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi
    
if [ -z "$JAVACMD" ] ; then 
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then 
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
      JAVACMD="java"
    fi
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

# For Cygwin, switch paths to Windows format
if $Cygwin; then
  JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  LOCALCLASSPATH=`cygpath --path --windows "$LOCALCLASSPATH"`
fi

"$JAVACMD" -classpath "$LOCALCLASSPATH" net.sourceforge.cvsgrab.CVSGrab "$@"
