#! /bin/sh

#   Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
#   reserved.
# OS specific support
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home   
           fi
           ;;
esac

if [ -z "$CVSGRAB_HOME" ] ; then
  # try to find CVSGRAB
  if [ -d /opt/cvsgrab ] ; then 
    CVSGRAB_HOME=/opt/cvsgrab
  fi

  if [ -d "${HOME}/opt/cvsgrab" ] ; then 
    CVSGRAB_HOME="${HOME}/opt/cvsgrab"
  fi

  ## resolve links - $0 may be a link to cvsgrab's home
  PRG="$0"
  progname=`basename "$0"`
  saveddir=`pwd`

  # need this for relative symlinks
  cd `dirname "$PRG"`
  
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '.*/.*' > /dev/null; then
	PRG="$link"
    else
	PRG=`dirname "$PRG"`"/$link"
    fi
  done
  
  CVSGRAB_HOME=`dirname "$PRG"`/..

  cd "$saveddir"

  # make it fully qualified
  CVSGRAB_HOME=`cd "$CVSGRAB_HOME" && pwd`
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched

if $cygwin ; then
  [ -n "$CVSGRAB_HOME" ] &&
    CVSGRAB_HOME=`cygpath --unix "$CVSGRAB_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi
    
# set CVSGRAB_LIB location
CVSGRAB_LIB="${CVSGRAB_HOME}/lib"

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
for i in "${CVSGRAB_LIB}"/*.jar
do
  # if the directory is empty, then it will return the input string
  # this is stupid, so case for it
  if [ -f "$i" ] ; then
    if [ -z "$LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH="$i"
     else
      LOCALCLASSPATH="$i":"$LOCALCLASSPATH"
    fi
  fi
done

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  CVSGRAB_HOME=`cygpath --path --windows "$CVSGRAB_HOME"`
  JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  LOCALCLASSPATH=`cygpath --path --windows "$LOCALCLASSPATH"`
  CYGHOME=`cygpath --path --windows "$HOME"`
fi

LOG_ARGS1="-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog" 
LOG_ARGS2="-Dorg.apache.commons.logging.simplelog.showShortLogName=false"

"$JAVACMD" -classpath "$LOCALCLASSPATH" "$LOG_ARGS1" "$LOG_ARGS2" net.sourceforge.cvsgrab.CVSGrab "$@"
