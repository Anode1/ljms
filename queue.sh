#!/bin/sh
#
# LJMS worker.
#
#   ./queue.sh start     start in the background
#   ./queue.sh run       run in the foreground (development)
#   ./queue.sh stop      finish the task in hand, then exit
#   ./queue.sh status    is it running?
#
# Safe to call "start" from cron every few minutes: an already-running worker
# makes it a no-op, so cron doubles as a restart-if-dead watchdog.
#
# Database credentials live in Processor.java, not here — one place to edit.
# Pass a node name only if you run more than one worker on this host.
LJMS_NODE="${LJMS_NODE:-}"

PIDFILE=logs/queue.pid
LOGFILE=logs/queue.out

CP_SEP=:
case "$(uname -s 2>/dev/null || echo unknown)" in
  CYGWIN*|MINGW*|MSYS*) CP_SEP=';' ;;
esac

# build/ plus whatever is in lib/ (junit for tests, and your JDBC driver)
CP="build"
for jar in lib/*.jar; do
  [ -f "$jar" ] && CP="${CP}${CP_SEP}${jar}"
done
[ -n "${CLASSPATH}" ] && CP="${CP}${CP_SEP}${CLASSPATH}"

JAVA_OPTS="${JAVA_OPTS:--Xms32m -Xmx256m}"
MAIN=org.ljms.Processor

mkdir -p logs

running() {
  [ -f "$PIDFILE" ] && kill -0 "$(cat "$PIDFILE")" 2>/dev/null
}

case "${1:-start}" in

  run)
    exec java ${JAVA_OPTS} -cp "${CP}" ${MAIN} ${LJMS_NODE}
    ;;

  start)
    if running; then
      echo "LJMS worker already running (pid $(cat "$PIDFILE"))"
      exit 0
    fi
    nohup java ${JAVA_OPTS} -cp "${CP}" ${MAIN} ${LJMS_NODE} >> "$LOGFILE" 2>&1 &
    echo $! > "$PIDFILE"
    echo "LJMS worker started (pid $(cat "$PIDFILE")), output: $LOGFILE"
    ;;

  stop)
    if ! running; then
      echo "LJMS worker is not running"
      rm -f "$PIDFILE"
      exit 0
    fi
    PID=$(cat "$PIDFILE")
    # TERM triggers the shutdown hook: the loop exits after the task in hand.
    kill "$PID"
    echo "Stopping LJMS worker (pid $PID)..."
    i=0
    while kill -0 "$PID" 2>/dev/null; do
      i=$((i+1))
      if [ $i -gt 60 ]; then
        echo "Still running after 60s - leaving it alone (kill -9 $PID to force)"
        exit 1
      fi
      sleep 1
    done
    rm -f "$PIDFILE"
    echo "Stopped"
    ;;

  status)
    if running; then
      echo "LJMS worker running (pid $(cat "$PIDFILE"))"
    else
      echo "LJMS worker is not running"
      exit 1
    fi
    ;;

  *)
    echo "Usage: $0 {start|run|stop|status}"
    exit 2
    ;;
esac
