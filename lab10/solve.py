from read_input import Problem
from dgraph import DependencyGraph


class Solver:
    def __init__(self, problem: Problem):
        self.problem = problem
        self.__dependency_relation = None

    @property
    def dependency_relation(self):
        if self.__dependency_relation:
            return self.__dependency_relation

        dependency = {}
        for a in self.problem.alphabet:
            for b in self.problem.alphabet:
                if (a not in self.problem.relation) \
                    or (b not in self.problem.relation[a]):
                    if a not in dependency:
                        dependency[a] = set([b])
                    else:
                        dependency[a].add(b)

        self.__dependency_relation = dependency
        return dependency

    def get_trace(self, word=None, traces=None):
        # print(word, traces, problem, sep='\n')
        if not word:
            word = self.problem.word
        if not traces:
            traces = set([word])
        traces.add(word)
        i = 0
        while i < len(word) - 1:
            char = word[i]
            changed = word[:i] + word[i+1] + word[i] + word[i+2:]
            if char in self.problem.relation \
                and word[i+1] in self.problem.relation[char] \
                and changed not in traces:
                traces |= self.get_trace(word=changed, traces=traces)
                i+=1
            i+=1
        return traces

    def get_floata(self):
        result = []
        dependency = self.dependency_relation

        stacks = {char: [] for char in self.problem.alphabet}
        for i in range(len(self.problem.word)-1, -1, -1):
            char = self.problem.word[i]
            stacks[char].append(char)
            for char2 in self.problem.alphabet:
                if char != char2 \
                    and (char not in self.problem.relation
                        or char2 not in self.problem.relation[char]
                    ):
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
                for char in dependency[popped]:
                    if char != popped \
                        and len(stacks[char]) != 0:
                        stacks[char].pop()
                        popped_markers[char]+=1

            block+=')'
            if not not_empty_stack:
                break
            result.append(block)

        return result

    def get_dependency_graph(self):
        dgraph = DependencyGraph(self.problem, self.dependency_relation)
        return dgraph
