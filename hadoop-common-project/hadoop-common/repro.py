import os, sys, shutil, shlex, subprocess


# Prase one failure from the given line
# return the failure test class name,
# test method name and failure id
def parse_one_failure(line):
    splited = line.split("/")
    test_class = splited[2]
    test_method = splited[3]
    failure_id = splited[5]
    return test_class, test_method, failure_id        


def reproduce(failure_file, output_dir):
    if os.path.exists(output_dir):
        shutil.rmtree(output_dir)
    os.makedirs(output_dir)
    
    with open(failure_file, 'r') as f:
        for line in f:
            test_class, test_method, failure_id = parse_one_failure(line)
            log_file_dir = os.path.join(output_dir, test_class, test_method)
            os.makedirs(log_file_dir, exist_ok=True)
            log_file = os.path.join(log_file_dir, "log_{}".format(failure_id))
            print(log_file)
            repro_cmd = "JAVA_HOME=\"/usr/lib/jvm/java-11-openjdk-amd64\" mvn jqf:repro -Dclass={}  -Dmethod={}  -Dconstraint.file=mappingDir/constraint -Djqf.failOnDeclaredExceptions -DsetSurefireConfig -DconfigFuzz -Dinput={}".format(test_class, test_method, line)
            cmd = shlex.split(repro_cmd)
            with open(str(log_file).strip(), 'w') as f:
                subprocess.call(repro_cmd, shell=True, stdout=f)
                exit(-1)

if __name__ == '__main__':
    failure_file = sys.argv[1]
    output_dir = sys.argv[2]
    reproduce(failure_file, output_dir)
    
    
