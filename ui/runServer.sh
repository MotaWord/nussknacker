#!/usr/bin/env bash

profile=generic
if [ $# -ge 1 ]; then
  profile=$1
fi

cd server
java -Dlogback.configurationFile=./logback.xml -Dconfig.file=./conf/$profile/application.conf -jar ./target/scala-2.11/nussknacker-ui-assembly.jar 8081
