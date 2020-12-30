#!/bin/bash
ps aux | grep kafka-jmx | grep -v grep | awk '{print $2}'
java -javaagent:jmx_prometheus_javaagent-0.13.0.jar=8070:config.yaml -jar kafka-jmx-exporter-1.0.0.jar
