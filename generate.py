import random
from nltk import CFG


def main():
    grammar = generate_grammar('grammar.txt')
    for i in range(100):
        frags = []
        generate_sample(grammar, grammar.start(), frags)
        while len(frags) > 12 or len(frags) < 6:
            frags = []
            generate_sample(grammar, grammar.start(), frags)
        sentence = ' '.join(frags)
        print('SENTENCE:', sentence)


def generate_grammar(file):
    """
    Generates an NLTK CFG from production rules stored in a text file.

    :param file: the file from which the grammar should be read
    :return: the grammar as an NLTK CFG class
    """
    corpus = []
    with open(file, 'r+') as file:
        for line in file.readlines():
            if line == '\n':
                continue
            corpus.append(line)

    string_corpus = ''.join(corpus)
    return CFG.fromstring(string_corpus)


def check_sentence(parser, sentence):
    """
    Checks the sentence using the specified parse.
    Taken from the TTTV lab homework.

    :param parser: the parser to use when checking the sentence
    :param sentence: the sentence to check
    :return: the tree that was found when parsing the sentence, can be None!
    """

    print("--------------------------------------------------")
    print("Checking if provided sentence matches the grammar:")
    print(sentence)
    if isinstance(sentence, str):
        sentence = sentence.split()
    tree_found = False
    results = parser.parse(sentence)
    for tree in results:
        tree_found = True
        print(tree)
        break
    if not tree_found:
        print(sentence, "Does not match the provided grammar.")
    print("--------------------------------------------------")
    return tree_found


def generate_sample(grammar, prod, frags):
    """
    Generate a random sample sentence recursively, using the specified grammar.
    This method will directly modified the supplied frags list, which will contain the generated sample.
    Taken from https://stackoverflow.com/a/47143831, thank you @acimutal!

    :param grammar: the grammar to use when generating a sentence
    :param prod: the start symbol grammar
    :param frags: a list containing all the words of the generated sentence
    """
    if prod in grammar._lhs_index: # Derivation
        derivations = grammar._lhs_index[prod]
        derivation = random.choice(derivations)
        for d in derivation._rhs:
            generate_sample(grammar, d, frags)
    elif prod in grammar._rhs_index:
        # terminal
        frags.append(str(prod))


if __name__ == '__main__':
    main()
