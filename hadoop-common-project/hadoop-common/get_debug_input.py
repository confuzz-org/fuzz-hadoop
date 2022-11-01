import os, sys, shutil

def get_reproducable(raw_file, repro_dir, output_dir):
    with open(raw_file, 'r') as f:
        for line in f:
            splited = line.strip().split('\t')
            method = splited[0]
            failure_id = splited[1]
            #print(method, failure_id)
            get_input(repro_dir, output_dir, method, failure_id)


def get_input(repro_dir, output_dir, method, failure_id):
    class_name = method.split('#')[0]
    test_name = method.split('#')[1]
    log_file_path = os.path.join(repro_dir, class_name, test_name, "log_id_00000{}".format(failure_id))

    content = []
    with open(log_file_path, 'r') as f:
        for line in f:
            if "[TEST]=" in line or "test=" in line or "-CONFIG-" in line:
                content.append(line)
    if not os.path.exists(output_dir):
        os.makedirs(output_dir, exist_ok=False)
    output_file = os.path.join(output_dir, f"input-{test_name}-{failure_id}")
    with open(output_file, 'w') as f:
        for line in content:
            f.write(line)


        

if __name__ == '__main__':
    raw_file = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/input_raw"
    repro_dir = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/repro_fuzz_all"
    output_dir = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/debug_input"
    get_reproducable(raw_file, repro_dir, output_dir)
