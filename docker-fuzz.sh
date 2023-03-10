# first run 'docker build -t 'hadoop-build' -f Dockerfile .' from the docker dir
containerName=fuzzingcon
testModule=$1
testClass=$2
testMethod=$3
constraintFile=constraint
regexFile=regex
duration=$4

docker run --name ${containerName} -u ctestfuzz -d -i -t "hadoop-build" bash
docker exec -u ctestfuzz ${containerName} \
    "cd fuzz-hadoop/${testModule} && mvn confuzz:fuzz -Dmeringue.testClass=${testClass} -Dmeringue.testMethod=${testMethod} -DconstraintFile=${constraintFile} -DregexFile=${regexFile} -Dmeringue.duration=${duration}" # Do not run in detached mode!
docker exec -u ctestfuzz ${containerName} \
    "cd ${testModule} && mvn confuzz:analyze -Dmeringue.testClass=${testClass} -Dmeringue.testMethod=${testMethod} -DconstraintFile=${constraintFile} -DregexFile=${regexFile} -Dmeringue.duration=${duration}" # Do not run in detached mode!
mkdir -p result/$testClass/$testMethod/output
docker cp -r "${containerName}:/home/ctestfuzz/fuzz-hadoop/$testModule/meringue" "result/$testClass/$testMethod/output"
docker stop ${containerName}
docker rm ${containerName}
