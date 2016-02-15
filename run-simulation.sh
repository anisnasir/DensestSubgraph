#!/bin/bash
JAR="/root/DensestSubgraph/target/Densest-0.0.1-SNAPSHOT.jar"
#input="test_case10.txt"
input="com-dblp.ungraph.txt"
#input="com-lj.ungraph.txt"
windowSize="11"
LOGGING="false"

command="java -jar ${JAR} ${input} ${windowSize} ${LOGGING} "

$command

