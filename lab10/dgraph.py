import networkx as nx
from matplotlib import pyplot as plt
from read_input import Problem


class DependencyGraph(nx.DiGraph):
    def __init__(self, problem: Problem, dependency_relation, *args, **kwargs):
        super().__init__(directed=True, *args, **kwargs)
        self.problem = problem
        for i in range(len(problem.word)):
            self.add_node(i, char=problem.word[i])
            for j in range(i):
                t1 = self.node_char(j)
                t2 = self.node_char(i)
                if t1 in dependency_relation and t2 in dependency_relation[t1]:
                    self.add_edge(j,i)
        self.minimize()

    def minimize(self):
        for node in self.nodes:
            if not self.out_edges(node):
                continue
            delete = set()
            for neighbour in map(lambda x: x[1],self.out_edges(node)):
                if neighbour in delete:
                    continue
                visited = set()
                self.dfs(neighbour, visited)
                visited.remove(neighbour)
                delete |= set(visited_node for visited_node in visited)
            delete = filter(lambda e: self.contains_edge(e), map(lambda n: (node, n), delete))
            self.remove_edges_from(delete)

    def dfs(self, node, visited, topological=None):
        if node in visited:
            return
        visited.add(node)
        for out_edge in self.out_edges(node):
            neighbour = out_edge[1]
            if neighbour not in visited:
                self.dfs(neighbour, visited, topological=topological)
        if topological is not None:
            topological.append(node)

    def contains_edge(self, edge):
        return edge in self.edges

    def node_char(self, node):
        return nx.get_node_attributes(self, 'char')[node]

    def plot(self):
        pos = nx.planar_layout(self)
        nc = ['c' for _ in range(self.number_of_nodes())]
        plt.figure("Dependency Graph", figsize=(10, 10))
        nx.draw_networkx_nodes(self, pos, node_color=nc, cmap=plt.get_cmap('jet'), node_size=900)
        nx.draw_networkx_labels(self, pos, dict(
            map(lambda x: (x[0],x[1]), nx.get_node_attributes(self, 'char').items())))
        nx.draw_networkx_edges(self, pos, edgelist=self.edges(), edge_cmap=plt.cm.Wistia,
                               width=2, arrows=True, arrowstyle='-|>', arrowsize=20, node_size=1000)
        plt.draw()
        plt.show()

    def get_foata(self):
        result = []
        topo = self.topological_sort()
        i = 0
        while i < len(self.problem.word):
            block = '('
            block += topo[i]

            k = i
            i += 1
            while i < len(self.problem.word):
                is_independent = True
                for j in range(k,i):
                    if (topo[i] not in self.problem.relation) \
                        or (topo[j] not in self.problem.relation[topo[i]]):
                        is_independent = False
                        break
                if is_independent:
                    block += topo[i]
                    i+=1
                else:
                    break
            block += ')'
            result.append(block)
        return result

    def topological_sort(self):
        visited = set()
        topo = []
        for node in self.nodes:
            self.dfs(node, visited, topological=topo)
        topo = list(map(lambda x: self.node_char(x), reversed(topo)))
        return topo
