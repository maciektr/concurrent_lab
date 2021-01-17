from read_input import read_input

from solve import Solver
from dgraph import DependencyGraph

def main():
    problem = read_input(vocal=False)
    # print(problem)
    solver = Solver(problem)

    # dep = solver.dependency_relation
    # print(dep)
    # traces = solver.get_trace()
    # print(traces)
    # floata = solver.get_floata()
    # print(floata)

    solver.get_dependency_graph().plot()


if __name__ == '__main__':
    main()
