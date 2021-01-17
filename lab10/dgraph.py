import networkx as nx
from matplotlib import pyplot as plt
from read_input import Problem


class DependencyGraph(nx.DiGraph):
    def __init__(self, problem: Problem, *args, **kwargs):
        super().__init__(directed=True, *args, **kwargs)
        self.__init_from_problem(problem)


    def __init_from_problem(self, problem: Problem):
        for i in range(len(problem.word)):
            self.add_node(i, char=problem.word[i])



    def plot(self):
        pos = nx.spring_layout(self)
        nc = ['c' for _ in range(self.number_of_nodes())]
        plt.figure("Dependency Graph", figsize=(10, 10))
        nx.draw_networkx_nodes(self, pos, node_color=nc, cmap=plt.get_cmap('jet'), node_size=900)
        # nx.draw_networkx_labels(self, pos, dict(
            # map(lambda x: (x[0], str(round(x[1], 2)) + 'V'), nx.get_node_attributes(self, 'voltage').items())))
        nx.draw_networkx_edges(self, pos, edgelist=self.edges(), edge_cmap=plt.cm.Wistia,
                               width=2, arrows=True, arrowstyle='-|>', arrowsize=20, node_size=1000)
        # nx.draw_networkx_edge_labels(self, pos, dict(
            # map(lambda x: (x[0], str(round(x[1], 2)) + 'A'), nx.get_edge_attributes(self, 'current').items())))
        plt.draw()
        plt.show()

