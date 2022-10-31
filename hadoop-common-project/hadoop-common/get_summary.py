import parse_result as p




if __name__ == '__main__':
    fuzz_output_dir = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/fuzz_output_all/fuzz-results"
    test_list_file = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/test_input_all"
    repro_output_dir = "/home/swang516/xlab/cfuzz/nostring-hadoop/hadoop-common-project/hadoop-common/repro_fuzz_all"
    p.get_num(fuzz_output_dir, True)
