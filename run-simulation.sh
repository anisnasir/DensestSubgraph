#!/bin/bash
JAR="target/Densest-0.0.1-SNAPSHOT.jar"
input_dir="/Users/anis/Datasets/Densest/"
#input="test_case1.txt"
#input="snap_facebook.txt"
#input="com-dblp.ungraph.txt"
#input="com-lj.ungraph.txt"
input="twitter_combined_modified.txt"
windowSize="100001"
LOGGING="false"

command="java -jar ${JAR} ${input_dir} ${input} ${windowSize} ${LOGGING} "

$command

