#!/bin/sh
delay=5
elapsed=0

deviceSdk () {
    adb shell getprop ro.build.version.sdk 2>/dev/null || echo 0
}

until [ $elapsed -gt 60 -o $(deviceSdk) -gt 0 ]
do
    echo "Waiting for emulator: value=$(deviceSdk); time=$elapsed"
    elapsed=$((elapsed + delay))
    sleep $delay
done
