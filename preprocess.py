import nltk
from nltk import word_tokenize

corpus = ''

with open('corpus.txt', 'r')  as file:
    corpus = "".join([line for line in file.readlines()])


tokens = word_tokenize(corpus)
print(tokens)

# print([line.lower() for line in corpus.split('.') if line != ''])