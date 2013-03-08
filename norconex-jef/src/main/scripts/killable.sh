#!/bin/sh
# Translate SIGTERM into SIGKILL.
# Required for imposing a "hard-kill" when executing a process
# termination command that would not, like Java Process.destroy() method.
trap "kill -KILL \\$p1; exit" 15
${*} &
p1=${!}
wait $p1