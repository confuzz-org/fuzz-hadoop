import os, shutil, sys
from configGen import run

def debug_all(input_file, debug_input_dir, output_dir):
    counter = 0
    with open(input_file, 'r') as f:
        for line in f:
            splited = line.strip().split('\t')
            test_name = splited[0].split('#')[1]
            failure_id = splited[1]
            debug_file_name = "{}-{}-{}".format("input", test_name, failure_id)
            debug_file_path = os.path.join(debug_input_dir, debug_file_name)
            print("Counter-{}:====================={}===================".format(counter, debug_file_name), flush=True)
            counter += 1
            #run(debug_file_path, output_dir)
            os.system("python3 configGen.py {} {}".format(debug_file_path, output_dir))
            

if __name__ == '__main__':
    input_file = sys.argv[1] #"/home/shuai/xlab/cfuzz/hadoop/hadoop-common-project/hadoop-common/recheck_input" 
    debug_input_dir = sys.argv[2] #"/home/shuai/xlab/cfuzz/hadoop/hadoop-common-project/hadoop-common/debug_input"
    output_dir = sys.argv[3]
    debug_all(input_file, debug_input_dir, output_dir)
