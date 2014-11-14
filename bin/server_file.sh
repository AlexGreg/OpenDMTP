#!/bin/sh
# -----------------------------------------------------------------------------
#  Valid Options:
#    -port=<port>
#    -storedir=<directory>
# -----------------------------------------------------------------------------

# --- set classpath separator for specific platform
if echo $OS | grep -q "Windows" ; then
    PATHSEP=";"
else
    PATHSEP=":"
fi
CPATH="./build/lib/mainfile.jar${PATHSEP}./build/lib/utils.jar${PATHSEP}./build/lib/dmtpserv.jar";

# ---
MAIN="org.opendmtp.server_file.Main"
ARGS="-start $1 $2"
echo "java -classpath $CPATH $MAIN $ARGS"
java -classpath $CPATH $MAIN $ARGS

# ---
