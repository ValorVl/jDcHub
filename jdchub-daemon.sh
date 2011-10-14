#!/bin/sh

cd ${0%${0##*/}}

if [ -z "$JAVA_HOME" ]; then
    echo "The JAVA_HOME environment variable is not defined"
    echo "This environment variable is needed to run this program"
    exit 1
fi

APP_DIR=.
CLASSPATH=${APP_DIR}:${APP_DIR}/*:${APP_DIR}/lib/*

# Put properties here
PROPERTIES=${KASSA_REPORTS_PROPERTIES}

start()
{
    ${JAVA_HOME}/bin/java $PROPERTIES -cp ${CLASSPATH} ru.sincore.Main >/dev/null 2>&1 &
}

stop()
{
    ps auxww | grep ru.sincore.Main | grep -v grep | awk '{print $2}' | xargs kill
}

status()
{
    ps auxww | grep -v grep | grep ru.sincore.Main > /dev/null && echo "ranning" || echo "stopped"
}

usage()
{
cat << __EOF__
Use: $0 <start|stop|status>
__EOF__
}

case $1 in
    start)
	start
    ;;
    stop)
	stop
    ;;
    status)
	status
    ;;
    *)
	usage
	exit 1
    ;;
esac

exit 0
