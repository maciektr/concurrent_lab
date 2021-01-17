from read_input import read_input

from solve import Solver
from dgraph import DependencyGraph

def main():
    problem = read_input(vocal=False)
    # print('Problem', problem)
    solver = Solver(problem)

    dep = solver.dependency_relation
    print('Dependency graph', dep)
    traces = solver.get_trace()
    print('Traces', traces)
    foata = solver.get_foata()
    print('Foata', foata)
    foata = solver.get_foata_from_graph()
    print('Foata', foata)
    solver.dependency_graph.plot()

if __name__ == '__main__':
    main()
