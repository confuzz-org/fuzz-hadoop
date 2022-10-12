import os,shutil,sys,subprocess


constraint_file = "/Users/alenwang/Documents/xlab/fuzz-hadoop/hadoop-common-project/hadoop-common/mappingDir/constraint"
# Return two string of test_class and test_name 
def get_test_class_method(line):
    res = line.split('#')
    return res[0].strip(), res[1].strip()+"Fuzz"

def run(input_file, target_dir):
    with open(input_file, 'r') as f:
        for line in f:
            test_class, test_method = get_test_class_method(line)
            log_file = "logs/log_{}_{}".format(test_class, test_method)
            fuzz_cmd = "JAVA_HOME=/usr/local/opt/openjdk@11 mvn jqf:fuzz -Dclass={} -Dmethod={} -Dtarget={}/ -Dconstraint.file=mappingDir/constraint -Dtime=10m -DexitOnCrash -Djqf.failOnDeclaredExceptions -DsetSurefireConfig -DconfigFuzz | tee {}".format(test_class, test_method, target_dir,  log_file)

            print("======================Fuzzing {}#{} =========================".format(test_class, test_method))
            os.system(fuzz_cmd)
            subprocess.check_output(fuzz_cmd, shell=True, timeout=600)

            
if __name__ == '__main__':
    input_file = sys.argv[1].strip()
    target_dir = sys.argv[2].strip()
    run(input_file, target_dir)
