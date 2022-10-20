from multiprocessing import current_process
import sys, os
header = "<?xml version=\"1.0\"?>\n<?xml-stylesheet type=\"text/xsl\" href=\"configuration.xsl\"?>\n<configuration>\n"
conf_str = "<property>\n  <name>{}</name>\n  <value>{}</value>\n  </property>\n"
tile = "</configuration>\n"
current_param = {}
key_list = []   # Use to index the current checking key,value pair

# [PARENT-CONFIG-SAME] dummy.fence.key = org.apache.hadoop.ha.DummyHAService$DummyFencer
# [PARENT-CONFIG-SAME] hadoop.tokens -> null
# [PARENT-CONFIG-DIFF] ha.zookeeper.quorum = 127.0.0.1:2221 -> 127.0.0.1:32685
def parse_same_or_new(line):
    splited = line.strip().split(" ")
    if "PARENT-CONFIG-SAME" in line or "PARENT-CONFIG-SAME" in line:
        key = splited[1]
        value = splited[3]
    elif "PARENT-CONFIG-DIFF" in line:
        key = splited[1]
        value = splited[5]
    return key, value


def put_all_param_to_dic(input_config_file):
    with open(input_config_file, 'r') as f_input:
        for line in f_input:
            key, value = parse_same_or_new(line)
            current_param[key] = value
            key_list.append(key)


# Parse generated configuration from input_config_file
# Put them into target_config_file for debug
def run(test_name, input_config_file, target_config_file):
    put_all_param_to_dic(input_config_file)
    dict_len = len(current_param)
    if len(key_list) != dict_len:
        print("Length of key list is different from parameter set")
    else:
        for i in range(dict_len):
            current_key = key_list[i]
            run_and_check(test_name, current_key)


def run_and_check(test, current_key):
    with open(target_config_file, 'w') as f_target:
        f_target.write(header)
        for k, v in current_param.items():
            # Do not add current key into configuration file
            if k == current_key:
                continue
            f_target.write(conf_str.format(k, v))
        f_target.write(tile)
    mvn_cmd = "JAVA_HOME=\"/usr/lib/jvm/java-11-openjdk-amd64\" mvn surefire:test -Dtest={} | tee result4check".format(test)
    os.system(mvn_cmd)
    check_result("result4check", current_key)
    

# Delete parameter key-value pair if 
def check_result(check_file, current_key):
    with open(check_file, 'r') as f:
        content = f.read()
        # The failure/error still occurs without current_key, so remove it
        if "<<< FAILURE!" in content or "<<< ERROR!" in content:
            del current_param[current_key]


if __name__ == '__main__':
    test_name = sys.argv[1]
    input_config_file = sys.argv[2]
    target_config_file = sys.argv[3]
    run(test_name, input_config_file, target_config_file)
    print(current_param)