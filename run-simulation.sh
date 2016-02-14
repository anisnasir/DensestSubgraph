#!/bin/bash
JAR="target/Densest-0.0.1-SNAPSHOT.jar"
input="test_case10.txt"
windowSize="11"
LOGGING="true"

command="java -jar ${JAR} ${input} ${windowSize} ${LOGGING}  >> output_${fileName}"

$command

