from read_input import read_input

from solve import get_dependency_relation, get_trace, get_floata
from dgraph import DependencyGraph

def main():
    problem = read_input(vocal=False)
    print(problem)
    # dep = get_dependency_relation(problem)
    # print(dep)
    # traces = get_trace(problem)
    # print(traces)
    # floata = get_floata(problem)
    # print(floata)
    depGraph = DependencyGraph(problem)
    depGraph.plot()


if __name__ == '__main__':
    main()
