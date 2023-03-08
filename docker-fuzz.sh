containerName=fuzzingcon

docker run --name ${containerName} -u ctestfuzz "hadoop-fuzz-ctests" bash
docker exec -u ctestfuzz ${containerName} "${Command}" # Do not run in detached mode!
docker cp "${containerName}:/home/ctestfuzz/file" "."
docker stop ${containerName}
docker rm ${containerName}
