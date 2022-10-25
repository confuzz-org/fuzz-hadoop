import os,shutil,sys,subprocess,shlex


constraint_file = "/Users/alenwang/Documents/xlab/fuzz-hadoop/hadoop-common-project/hadoop-common/mappingDir/constraint"
timeout_per_round = 600 # second
# Return two string of test_class and test_name 
def get_test_class_method(line):
    res = line.split('#')
    return res[0].strip(), res[1].strip()

def run(input_file, target_dir):
    with open(input_file, 'r') as f:
        for line in f:
            test_class, test_method = get_test_class_method(line)
            print("======================ConfFuzzing {}#{} =========================".format(test_class, test_method), flush=True)
            log_file = "logs/log_{}_{}".format(test_class, test_method)
            fuzz_cmd = "JAVA_HOME=\"/usr/lib/jvm/java-11-openjdk-amd64\" mvn jqf:fuzz -Dclass={} -Dmethod={} -Dtarget={}/ -Dconstraint.file=mappingDir/constraint -Dtime=10m -Djqf.failOnDeclaredExceptions -DsetSurefireConfig -DconfigFuzz | tee {}".format(test_class, test_method, target_dir,  log_file)
            cmd = shlex.split(fuzz_cmd)
            #print(cmd)
            try:
                p = subprocess.call(fuzz_cmd, shell=True, timeout=timeout_per_round)
            except subprocess.TimeoutExpired:
                print(f'=======================ConfFuzzing Timeout for {test_class}#{test_method} expired=======================', flush=True)

            
if __name__ == '__main__':
    input_file = sys.argv[1].strip()
    target_dir = sys.argv[2].strip()
    run(input_file, target_dir)
