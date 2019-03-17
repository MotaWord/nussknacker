#!/usr/bin/env bash

profile=ipm
if [ $# -ge 1 ]; then
  profile=$1
fi

cd ..
if [ "$profile" == "sample" ]; then
    ./sbtwrapper assemblySamples
elif [ "$profile" == "generic" ]; then
    ./sbtwrapper generic/assembly
elif [ "$profile" == "ipm" ]; then
    ./sbtwrapper ipm
fi

./sbtwrapper ui/assembly

cp ./ui/server/target/scala-*/nussknacker-ui-assembly.jar ./docker/app/build/nussknacker-ui-assembly.jar
cp ./engine/ipm/target/scala-*/ipmModel.jar ./docker/app/build/ipmModel.jar

cd ./docker
docker-compose kill
docker-compose rm -f -v
docker-compose up --build

cd -
