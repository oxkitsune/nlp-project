import nltk
from nltk import word_tokenize
from collections import Counter

corpus = ''

total_lines = 0
total_letters = 0

with open('corpus.txt', 'r')  as file:
    lines = file.readlines()
    total_lines = len(lines)
    corpus = "".join([line for line in lines if not line.startswith('#')])


tokens = word_tokenize(corpus.strip())
for token in tokens:
    total_letters += len(token)

counter = Counter(tokens)

# print(tokens)

print(f'Total Lines: {total_lines}')
print(f'Total Word Count: {len(tokens)}')
print(f'Total Letters: {total_letters}')
print(f'Avg. Words per Line:  {len(tokens)/total_lines}')
print(f'Avg. Letters per Word: {total_letters/len(tokens)}')
print(f'Most Common Token: "{counter.most_common(1)[0][0]}" with {counter.most_common(1)[0][1]} occurences')