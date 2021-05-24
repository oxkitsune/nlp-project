import os

corpus = []
replacements = {
    "you're": "you are",
    "i'm": "i am",
    "i've": "i have",
    "you've": "you have",
    "don't": "do not",
    "can't": "cannot",
    "we'll": "we will",
    "ain't": "isn't",
    "gotta": "got to",
    "gonna": "going to",
    "what cha": "what have you",
    ",": "",
    "?": "",
    "!": ""
}

with open('processed_corpus.bak', 'r+') as file:
    for line in file.readlines():
        if line == '\n':
            continue

        corpus.append(line)

    processed_corpus = []

    for line in corpus:
        line = line.lower()

        # Apply all replacements to the line
        for key in replacements:
            line = line.replace(key, replacements[key])

        # Append the line to the processed_corpus list
        processed_corpus.append(line)

# Make sure the file doesn't already exist >:(
if os.path.exists('processed_corpus.txt'):
    os.remove('processed_corpus.txt')

with open('processed_corpus.txt', 'x+') as file:
    file.writelines(processed_corpus)
    print('Saved processed corpus in "processed_corpus.txt"!')

