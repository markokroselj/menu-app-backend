package com.markokroselj;

public class Config {
    private static boolean verbose = false;
    private static boolean printStackTrace = false;

    private static final String DEFAULT_INPUT_FILE = "input.txt";
    private static final String DEFAULT_OUTPUT_FILE = "output.txt";
    private static String inputFile = DEFAULT_INPUT_FILE;
    private static String outputFile = DEFAULT_OUTPUT_FILE;

    public static String getDefaultInputFile() {
        return DEFAULT_INPUT_FILE;
    }

    public static String getDefaultOutputFile() {
        return DEFAULT_OUTPUT_FILE;
    }

    public static void setVerbose() {
        Config.verbose = true;
    }

    public static void setPrintStackTrace() {
        Config.printStackTrace = true;
    }

    public static boolean isPrintStackTrace() {
        return printStackTrace;
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static String getInputFile() {
        return inputFile;
    }

    public static void setInputFile(String inputFile) {
        Config.inputFile = inputFile;
    }

    public static String getOutputFile() {
        return outputFile;
    }

    public static void setOutputFile(String outputFile) {
        Config.outputFile = outputFile;
    }


}
