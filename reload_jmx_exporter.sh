#!/bin/bash
ps aux | grep kafka-jmx | grep -v grep | awk '{print $2}'
