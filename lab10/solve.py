from read_input import Problem
from dgraph import DependencyGraph


class Solver:
    def __init__(self, problem: Problem):
        self.problem = problem
        self.__dependency_relation = None
        self.__dgraph = None

    @staticmethod
    def __not_in(key, value, rel):
        return (key not in rel) or (value not in rel[key])

    @staticmethod
    def __safe_add(key, val, rel):
        if key not in rel:
            rel[key] = set([val])
        else:
            rel[key].add(val)

    @property
    def dependency_relation(self):
        if self.__dependency_relation:
            return self.__dependency_relation

        def all_pairs(arr):
            for a in arr:
                for b in arr:
                    yield (a,b)

        dependency = {}
        for (a,b) in all_pairs(self.problem.alphabet):
            if self.__not_in(a,b,self.problem.relation):
                self.__safe_add(a,b,dependency)

        self.__dependency_relation = dependency
        return dependency

    def get_trace(self, word=None, traces=None):
        word = word if word else self.problem.word
        traces = traces if traces else set()
        traces.add(word)
        i = 0
        while i < len(word) - 1:
            char = word[i]
            changed = word[:i] + word[i+1] + word[i] + word[i+2:]
            if not self.__not_in(char, word[i+1], self.problem.relation) \
                and changed not in traces:
                traces |= self.get_trace(word=changed, traces=traces)
                i+=1
            i+=1
        return traces

    def get_foata(self):
        result = []
        stacks = {char: [] for char in self.problem.alphabet}

        for i in range(len(self.problem.word)-1, -1, -1):
            char = self.problem.word[i]
            stacks[char].append(char)
            for char2 in self.problem.alphabet:
                if char != char2 \
                    and self.__not_in(char, char2, self.problem.relation):
                    stacks[char2].append('#')

        while True:
            block = '('
            not_empty_stack = False
            popped_markers = {char: 0 for char in self.problem.alphabet}
            for key in stacks.keys():
                if len(stacks[key]) == 0:
                    continue
                not_empty_stack = True
                if stacks[key][-1] == '#' or popped_markers[key] != 0:
                    continue
                popped = stacks[key].pop()
                block+=popped
                for char in self.dependency_relation[popped]:
                    if char != popped \
                        and len(stacks[char]) != 0:
                        stacks[char].pop()
                        popped_markers[char]+=1

            block+=')'
            if not not_empty_stack:
                break
            result.append(block)

        return result

    @property
    def dependency_graph(self):
        if not self.__dgraph:
            self.__dgraph = DependencyGraph(self.problem, self.dependency_relation)
        return self.__dgraph

    def get_foata_from_graph(self):
        return self.dependency_graph.get_foata()
