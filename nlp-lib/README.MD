# nlp-lib
This project is able to generate a CFG/PCFG for the NLTK python package.

This is done using the [CoreNLP Libraries](https://stanfordnlp.github.io/CoreNLP/) by stanford.

## Compiling nlp-lib
This project requires maven and java 11, and can be compiled using
```shell
mvn clean package
```

## Running nlp-lib
nlp-lib can be invoked using the following command:

The stanford CoreNLP libaries require a hefty amount of ram, we recommend running it with 4G:

```shell
java -Xmx4G -jar nlp-lib-0.1.0.jar
```

It can take the following optional arguments:

```shell
-corpus <path-to-corpus> 
specifies a corpus file, default is "corpus.txt"

-o | -out | -output <path-to-output>
specifies the output file for the generated grammar, default is "grammar.txt"

-pcfg
specifies that nlp-lib should generate a pcfg instead of the default cfg
```