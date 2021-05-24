package com.kitsune.nlplib;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class nlpLib {

    private static boolean PCFG;
    private static String CORPUS_PATH = "corpus.txt";
    private static String GRAMMAR_PATH = "grammar.txt";

    public static void main(String[] args) throws IOException {

        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equalsIgnoreCase("-pcfg")) {
                    PCFG = true;
                } else if (arg.equalsIgnoreCase("-corpus")) {
                    if (args.length < i + 1) {
                        System.out.println("No path specified for argument \"corpus\"");
                        System.exit(0);
                    }

                    CORPUS_PATH = args[i + 1];
                    File file = new File(CORPUS_PATH);
                    if (!file.exists()) {
                        System.out.println("Invalid corpus specified: File at \"" + CORPUS_PATH + "\" does not exist!");
                        System.exit(2);
                    }
                } else if (arg.equals("-output") || arg.equalsIgnoreCase("-out") || arg.equals("-o")) {
                    if (args.length < i + 1) {
                        System.out.println("No path specified for argument \"output\"");
                        System.exit(0);
                    }

                    GRAMMAR_PATH = args[i + 1];
                    File file = new File(GRAMMAR_PATH);
                    if (file.exists()) {
                        System.out.println("Invalid output file specified: File at \"" + GRAMMAR_PATH + "\" already exist!");
                        System.exit(2);
                    }
                } else {
                    System.out.println("Unknown argument: " + arg);
                    System.exit(3);
                }
            }
        }

        System.out.println("Generating a " + (PCFG ? "PCFG" : "CFG") + " using " + CORPUS_PATH);

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
        props.setProperty("parse.maxlen", "100");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        List<String> lines = Files.readAllLines(Path.of(CORPUS_PATH)).stream().filter(line -> !line.equals("")).collect(Collectors.toList());
        List<String> rules = new ArrayList<>();
        Map<String, List<String>> wordList = new HashMap<>();
        for (String line : lines) {
            Annotation annotation = new Annotation(line);
            pipeline.annotate(annotation);

            Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(TreeCoreAnnotations.TreeAnnotation.class);

            for (Tree child : tree.getChildrenAsList()) {

                if (child.label().toString().equals("S")) {
                    generateRules(rules, wordList, tree);
                }
            }
        }

        File grammarFile = new File(GRAMMAR_PATH);
        if (!grammarFile.exists()) {
            grammarFile.createNewFile();
        }

        try (PrintStream stream = new PrintStream(grammarFile)) {

            stream.println("# " + (PCFG ? "PCFG" : "CFG") + " created by nlp-lib");
            stream.println("# Source: https://github.com/oxkitsune/nlp-project/");
            stream.println("");
            stream.println("# Built on CoreNLP by stanford");
            stream.println("# https://stanfordnlp.github.io/CoreNLP/");
            stream.println("");
            stream.println("");

            // Write grammar rules
            for (String rule : rules) {
                stream.println(rule);
                System.out.println(rule);
            }

            // Empty line as spacer
            stream.println("");
            System.out.println("");

            // Write word constants
            for (String left : wordList.keySet()) {
                StringBuilder builder = new StringBuilder(left + " -> '");
                for (String ruleItem : wordList.get(left)) {
                    builder.append(ruleItem).append("' | '");
                }

                String rule = builder.substring(0, builder.toString().length() - 4);
                stream.println(rule);
                System.out.println(rule);
            }
        }
    }

    private static void generateRules(List<String> rules, Map<String, List<String>> wordList, Tree tree) {
        for (Tree child : tree.getChildrenAsList()) {

            if (child.numChildren() == 1 && child.getChild(0).numChildren() == 0) {

                String label = child.label().toString().replace("$", "S");

                if (!wordList.containsKey(label)) {
                    wordList.put(label, new ArrayList<>());
                }

                if (PCFG) {
                    wordList.get(label).add(child.getChild(0).label().value());
                } else if (!wordList.get(label).contains(child.getChild(0).label().value())) {
                    wordList.get(label).add(child.getChild(0).label().value());
                }

                continue;
            }

            String rule = generateRule(child);

            if (PCFG) {
                rules.add(rule);
            } else if(!rules.contains(rule)) {
                rules.add(rule);
            }

            generateRules(rules, wordList, child);
        }
    }

    private static String generateRule(Tree tree) {
        StringBuilder value = new StringBuilder(tree.label().toString() + " -> ");
        for (Tree child : tree.getChildrenAsList()) {
            value.append(child.label().toString().replace("$", "S")).append(" ");
        }

        return value.toString();
    }
}
