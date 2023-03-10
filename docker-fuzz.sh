containerName=fuzzingcon
testModule=$1
testClass=$2
testMethod=$3
constraintFile=constraint
regexFile=regex
duration=$4

docker run --name ${containerName} -u ctestfuzz -w "/home/ctestfuzz/fuzz-hadoop/${testModule}" -d -i -t "hadoop-build" bash
docker exec -u ctestfuzz ${containerName} mvn confuzz:fuzz -Dmeringue.testClass=${testClass} -Dmeringue.testMethod=${testMethod} -DconstraintFile=${constraintFile} -DregexFile=${regexFile} -Dmeringue.duration=${duration}
docker exec -u ctestfuzz ${containerName} mvn confuzz:analyze -Dmeringue.testClass=${testClass} -Dmeringue.testMethod=${testMethod} -DconstraintFile=${constraintFile} -DregexFile=${regexFile} -Dmeringue.duration=${duration}
docker exec -u ctestfuzz ${containerName} mvn confuzz:debug -Dmeringue.testClass=${testClass} -Dmeringue.testMethod=${testMethod} -DconstraintFile=${constraintFile} -DregexFile=${regexFile}
mkdir -p result/$testClass
docker cp -r "${containerName}:/home/ctestfuzz/fuzz-hadoop/$testModule/target/meringue/${testClass}/${testMethod}" "result/$testClass/"
docker stop ${containerName}
docker rm ${containerName}
