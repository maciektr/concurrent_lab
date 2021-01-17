from collections import namedtuple


class Problem:
    def __init__(self, word=None, relation=None, alphabet=None):
        self.word = word
        self.relation = relation
        self.alphabet = alphabet

    @staticmethod
    def __read_alphabet(curly=True):
        chars = input().strip()
        if curly:
            chars = chars[1:]
            chars = chars[:-1]
        return set(map(lambda c:c.strip(), chars.split(',')))

    @staticmethod
    def __read_word():
        return input().strip()

    @staticmethod
    def __read_independence(curly=True):
        pairs = input().strip().split('), (')

        def drop_one_char(pairs):
            pairs[0] = pairs[0][1:]
            pairs[-1] = pairs[-1][:-1]
        if curly:
            drop_one_char(pairs)
        drop_one_char(pairs)

        def parse_pair(pair):
            a, b = pair.split(',')
            return a.strip(), b.strip()
        pairs = list(map(lambda p: parse_pair(p), pairs))

        rel = {p[0]: set() for p in pairs}
        for key, val in pairs:
            rel[key].add(val)

        return rel

    def read(self, vocal=True):
        if vocal:
            print('Insert alphabet')
        self.alphabet = self.__read_alphabet()
        if vocal:
            print('Insert relation')
        self.relation = self.__read_independence()
        if vocal:
            print('Insert input word')
        self.word = self.__read_word()


def read_input(vocal=True):
    res = Problem()
    res.read(vocal=vocal)
    return res


if __name__ == '__main__':
    pass
