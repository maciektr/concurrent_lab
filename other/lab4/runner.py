import os
from os import system

JAR_PATH = 'lab4.jar'
CSV_PATH = 'output.csv'


def parse_bool(x):
    return 'true' if x else 'false'


def rm_file(path):
    try:
        os.remove(path)
    except OSError:
        pass
    print(f'File removed: {path}')


def test(buffer_size, prod_cons_count, fair, uniform, print_header=False):
    print('Testing for: ', buffer_size, prod_cons_count, fair, uniform, print_header)
    cmd = f'java -jar {JAR_PATH} --buffer_size {buffer_size} --prod_cons_count {prod_cons_count} --fair {parse_bool(fair)} --uniform {parse_bool(uniform)} --print_header {parse_bool(print_header)} >> {CSV_PATH}'
    print(cmd)
    system(cmd)



if __name__ == '__main__':
    rm_file(CSV_PATH)

    # Enumerate all configurations
    # Namely buffer_size in [10k, 100k]; prod_cons_count in [100,1000]
    print_header = True
    buffer_size = 10000
    while buffer_size <= 100000:
        prod_cons_count = 100
        while prod_cons_count <= 1000:
            test(buffer_size, prod_cons_count, True, True, print_header)
            test(buffer_size, prod_cons_count, True, False)
            test(buffer_size, prod_cons_count, False, True)
            test(buffer_size, prod_cons_count, False, False)
            prod_cons_count *= 10
            print_header=False
        buffer_size *= 10
