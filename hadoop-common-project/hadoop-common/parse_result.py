import os, sys, shutil


def get_summary():
    return


def get_csv(test_list_file, fuzz_output_dir, repro_output_dir):
    # method_name, successfully_fuzzed, #_failures, failure_id, parent_result, parent_exception, failure_result, failure_exception, reproducibility
    title = "method_name,successfully_fuzzed,#_failures,failure_id,parent_result,parent_exception,failure_result,failure_exception,reproducibility"
    print(title)
    all_method_set = get_test_method_set(test_list_file)
    success_count, total_failure, method_failure, success_set, failed_method_mapping = get_num(fuzz_output_dir, False)
    repro_dict, _ = get_repro(repro_output_dir)
    for method in all_method_set:
        successfully_fuzzed = method in success_set
        num_failures = failed_method_mapping[method] if method in failed_method_mapping.keys() else 0
        
        if method not in repro_dict:
            csv_str = f"{method},{successfully_fuzzed},{num_failures},null,null,null,null,null,null"
            print(csv_str)
        else:
            for r in repro_dict[method]:
                csv_str = f"{method},{successfully_fuzzed},{num_failures},{r[0]},{r[1]},{r[2]},{r[3]},{r[4]},{r[5]}"
                print(csv_str)

    summary_str = f"Summary,{success_count},{total_failure},null,null,null,null,null,null"
    print(summary_str)


def get_repro(repro_output_dir):
    # failure_id, parent_result, parent_exception, failure_result, failure_exception, reproducibility
    exception_dict = {}  # key: exception_name; value: A set of method that trigger this exception
    repro_dict = {}      # key: method_name; value: A list of lists that stores each failure's detailed info
    for root, dirs, files in os.walk(repro_output_dir):
        for file in files:
            splited_path = root.split("/")
            if "log_id_" in file:
                method_name = "{}#{}".format(splited_path[-2], splited_path[-1])
                if method_name not in repro_dict:
                    repro_dict[method_name] = []
                log_file_path = os.path.join(root, file)
                repro_dict[method_name].append(parse_repro_log(method_name, log_file_path, exception_dict))
                
    return repro_dict, exception_dict


def parse_repro_log(method_name, log_file, exception_dict):
    parent_result = False
    parent_exception = "no_exception"
    failure_result = False
    failure_exception = "no_exception"
    reproducibility = True
    cur_id = -1
    with open(log_file, 'r') as f:
        for line in f:
            if "::=" in line:
                splited_line = line.strip().split(" ::= ")
                cycle = splited_line[0]
                round = cycle.split("_")[0]
                cur_id = cycle.split("_")[1] if len(cycle.split("_")) > 1 else cycle.split("_")[0]

                result = splited_line[1]
                splited_result = result.split(" ")
                exception_name = "no_exception"
                if (len(splited_result) > 1):
                    exception_name = splited_result[1]
                    if exception_name not in exception_dict:
                        exception_dict[exception_name] = set()
                    exception_dict[exception_name].add(method_name)
                if "parent" in round:
                    parent_result = True if splited_result[0] == "SUCCESS" else False
                    parent_exception = exception_name
                else:
                    failure_result = True if splited_result[0] == "SUCCESS" else False
                    failure_exception = exception_name
                    
    if failure_result:
       reproducibility = False

    return [cur_id, parent_result, parent_exception, failure_result, failure_exception, reproducibility]
                    

def get_num(fuzz_output_dir, to_print):
    success_method_count = 0   # How many tests have been successfully fuzzed
    total_failure = 0   # How many total failures has been countered (one method could have multiple failures)
    method_failure = 0  # How many method-level failures has been countered (multiple failures in one method counts as one)
    success_set = set()  # A set that stores all fuzzed test name
    failed_method_mapping = {}  # key: method name; value: number of failures
    
    for root, dirs, files in os.walk(fuzz_output_dir):
        for file in files:
            # at least has one parent input -- which means the fuzzing execution is correctly at least once
            splited_path = root.split("/")
            if "/corpus" in root and file == "id_000000":
                method_name = "{}#{}".format(splited_path[-3], splited_path[-2])
                success_set.add(method_name)
            if "/failures" in root:
                method_name = "{}#{}".format(splited_path[-3], splited_path[-2])
                success_set.add(method_name)
                if method_name not in failed_method_mapping:
                    failed_method_mapping[method_name] = 0
                if "id_" in file:
                    total_failure += 1
                    failed_method_mapping[method_name] += 1
                if file == "id_000000":
                    method_failure += 1

    success_method_count = len(success_set)
    for method in success_set:
        if method not in failed_method_mapping:
            failed_method_mapping[method] = 0

    if to_print:
        print(f"success_method_count = {success_method_count}\ntotal_failure = {total_failure}\nmethod_failure = {method_failure}")
        print(f"success_set len = {len(success_set)}, type = {type(success_set)}")
        print(f"failed_method_mapping len = {len(failed_method_mapping)}")
    return success_method_count, total_failure, method_failure, success_set, failed_method_mapping


def get_test_method_set(test_list_file):
    method_set = set()
    with open(test_list_file, 'r') as f:
        for line in f:
            method_set.add(line.strip())
    #print(f"total_method_number = {len(method_set)}")
    return method_set


def get_exception_group(repro_output_dir):
    _, exception_dict = get_repro(repro_output_dir)
    print("Exception_name, #_triggered, Method_list")
    for e, method_set in exception_dict.items():
        print(f"{e},{len(method_set)},{method_set}")

    
if __name__ == '__main__':
    fuzz_output_dir = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/fuzz_output_all/fuzz-results"
    test_list_file = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/test_input_all"
    repro_output_dir = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/repro_fuzz_all"
    
    get_csv(test_list_file, fuzz_output_dir, repro_output_dir)
    #get_exception_group(repro_output_dir)
    
