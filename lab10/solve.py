from read_input import Problem


class Solver:
    def __init__(self, problem: Problem):
        self.problem = problem

def get_dependency_relation(problem: Problem):
    dependency = {}
    for a in problem.alphabet:
        for b in problem.alphabet:
            if (a not in problem.relation) \
                or (b not in problem.relation[a]):
                if a not in dependency:
                    dependency[a] = set([b])
                else:
                    dependency[a].add(b)
    return dependency

def get_trace(problem: Problem, word=None, traces=None):
    # print(word, traces, problem, sep='\n')
    if not word:
        word = problem.word
    if not traces:
        traces = set([word])
    traces.add(word)
    i = 0
    while i < len(word) - 1:
        char = word[i]
        changed = word[:i] + word[i+1] + word[i] + word[i+2:]
        if char in problem.relation \
            and word[i+1] in problem.relation[char] \
            and changed not in traces:
            traces |= get_trace(problem, changed, traces)
            i+=1
        i+=1
    return traces

def get_floata(problem: Problem):
    result = []
    dependency = get_dependency_relation(problem)

    stacks = {char: [] for char in problem.alphabet}
    for i in range(len(problem.word)-1, -1, -1):
        char = problem.word[i]
        stacks[char].append(char)
        for char2 in problem.alphabet:
            if char != char2 \
                and (char not in problem.relation
                    or char2 not in problem.relation[char]
                ):
                stacks[char2].append('#')

    while True:
        block = '('
        not_empty_stack = False
        popped_markers = {char: 0 for char in problem.alphabet}

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

