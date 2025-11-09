#!/bin/sh
mvn clean package
cp target/nexitool-MAIN-jar-with-dependencies.jar  ~/.bin/nexitool.jar
