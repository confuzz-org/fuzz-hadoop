from pathlib import Path
import json
import configparser
from multiprocessing import current_process
import sys, os
import copy
header = "<?xml version=\"1.0\"?>\n<?xml-stylesheet type=\"text/xsl\" href=\"configuration.xsl\"?>\n<configuration>\n"
conf_str = "<property>\n  <name>{}</name>\n  <value>{}</value>\n  </property>\n"
tile = "</configuration>\n"

# Read the config from the config.ini file
config = configparser.ConfigParser()
config.read('config.ini')
JAVA11_HOME = config['DEFAULT']['JAVA11_HOME']
fuzzingDir = Path(config['DEFAULT']['FuzzingDir'])
debuggingDir = Path(config['DEFAULT']['DebuggingDir'])

def build_file(test, run_id, prefix = "", suffix = ".json"):
    return fuzzingDir / "target" / "fuzz-results"\
            / test.replace('#', '/') / "failures"\
            / ('_'.join([prefix, f"{run_id}"]) + suffix)

def read_config(test, run_id):
    failed_config_file = build_file(test, run_id, prefix="config")
    if not failed_config_file.exists():
        print("No trace file found. Is there no failure for the fuzzing?")
        exit(-1)
    with open(failed_config_file) as f:
        failed_config = json.load(f)
    return failed_config

def inject_config(config, without_key = None):
    target_config_file = debuggingDir / "target" / "classes" / "core-ctest.xml"
    with open(target_config_file, "w") as f:
        f.write(header)
        for k, v in config.items():
            if k == without_key or v == None:
                continue
            f.write(conf_str.format(k, v))
        f.write(tile)

def run(test, run_id, output_dir):
    failed_config = read_config(test, run_id)
    minimal_config = copy.deepcopy(failed_config)
    for key in failed_config.keys():
        # Run test without current_key injected
        # If failure still occurs, remove current_key from current_dic
        inject_config(minimal_config, key)
        mvn_cmd = f"cd {debuggingDir};JAVA_HOME=\"{JAVA11_HOME}\" mvn surefire:test -Dtest={test} -Dmaven.antrun.skip | tee result4check"
        os.system(mvn_cmd)
        if check_result(test):
            del minimal_config[key]
    output_file = output_dir / f"{test}_failure_{run_id}.json"
    output_file.parent.mkdir(parents = True, exist_ok = True)
    with open(output_file, "w") as f:
        json.dump(minimal_config, f)

# Delete parameter key-value pair if failure/error still occurs
def check_result(test):
    with open(debuggingDir / "target" / "surefire-reports" / f"{test.split('#')[0]}.txt") as f:
        last_line = f.readlines()[3]
        print(last_line)
        failureNum = int(last_line.split(",")[1].split(":")[1])
        errorNum = int(last_line.split(",")[2].split(":")[1])
        return not (failureNum == 0 and errorNum == 0)

def maven_clean_install():
    os.system(f"JAVA_HOME=\"{JAVA11_HOME}\" mvn clean install -DskipTests")

if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("Usage: python3 configGen.py <test> <run_id> <output_dir>")
        exit(0)
    test, run_id, output_dir = sys.argv[1:]
    #maven_clean_install()
    run_id = "0"*(6-len(run_id)) + run_id
    run(test, run_id, Path(output_dir))
