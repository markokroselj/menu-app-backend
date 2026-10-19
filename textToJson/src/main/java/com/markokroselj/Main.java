package com.markokroselj;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        handleArgs(args);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Data data = null;
        try {
            data = new Data(getWeeks(Config.getInputFile()));
        } catch (FileNotFoundException | ParseException fileNotFoundException) {
            if (Config.isPrintStackTrace()) fileNotFoundException.printStackTrace();
            else System.err.println(fileNotFoundException.getMessage());
            System.exit(1);
        }
        try (FileWriter writer = new FileWriter(Config.getOutputFile())) {
            gson.toJson(data, writer);
            System.out.println("Converted " + Config.getInputFile() + " to " + Config.getOutputFile());
        } catch (IOException e) {
            if (Config.isPrintStackTrace()) e.printStackTrace();
            else System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    public static void handleArgs(String[] args) {
        if (args.length < 1) return;

        int numOfParams = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].strip().startsWith("-")) {
                numOfParams++;
                switch (args[i]) {
                    case "-v", "--verbose" -> Config.setVerbose();
                    case "-t", "--print-stack-trace" -> Config.setPrintStackTrace();
                    case "-o", "--output" -> {
                        if (i < args.length - 1 && !args[i+1].startsWith("-")) {
                            Config.setOutputFile(args[i+1].strip());
                            numOfParams++;
                        } else {
                            System.err.println("Error: Output filename expected!");
                            System.exit(2);
                        }
                    }
                    case "-i", "--input" -> {
                        if (i < args.length - 1 && !args[i+1].startsWith("-")) {
                            Config.setInputFile(args[i+1].strip());
                            numOfParams++;
                        } else {
                            System.err.println("Error: Input filename expected!");
                            System.exit(2);
                        }
                    }
                    case "-h", "--help" -> {
                        printHelp(); System.exit(0);
                    }
                    default -> {
                        System.err.println("Error: Unexpected argument '" + args[i] + "' found!");
                        System.exit(2);
                    }
                }
            }
        }

        if (args.length > numOfParams) Config.setInputFile(args[numOfParams]);
        if (args.length > 1 + numOfParams) Config.setOutputFile(args[1 + numOfParams]);
    }

    private static void printHelp() {
        System.out.println("Usage: textToJson.jar [OPTION]... [INPUT_FILE] [OUTPUT_FILE]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.printf("  %-25s %s%s%s\n", "INPUT_FILE", "Input file (default: ", Config.getDefaultInputFile(), ")");
        System.out.printf("  %-25s %s%s%s\n", "OUTPUT_FILE", "Output file (default: ", Config.getDefaultOutputFile(), ")");
        System.out.println();
        System.out.println("Options:");
        System.out.printf("  %-25s %s\n", "-v, --verbose", "Display additional debug information.");
        System.out.printf("  %-25s %s\n", "-i, --input", "Specify input filename.");
        System.out.printf("  %-25s %s\n", "-o, --output", "Specify output filename.");
        System.out.printf("  %-25s %s\n", "-t, --print-stack-trace", "Display error stack traces.");
        System.out.printf("  %-25s %s\n", "-h, --help", "Print help.");
    }

    public static int getWeekIndex(String dateString) throws ParseException {
        // Parse the date string
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd.MM.yyyy");
        Date date = sdf.parse(dateString);

        // Create a calendar instance and set the parsed date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Get the day of week and day of month
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Adjust Sunday (which is 1 in Calendar) to be considered as part of the previous week
        if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, -1); // Move back one day
            dayOfWeek = Calendar.SATURDAY; // Adjust day of week to Saturday
        }

        // Calculate the week index starting from 0
        int weekIndex = calendar.get(Calendar.WEEK_OF_MONTH) - 1;

        return weekIndex;
    }


    public static ArrayList<Week> getWeeks(String path) throws FileNotFoundException, ParseException {
        Scanner scanner = new Scanner(new File(path));
        ArrayList<ArrayList<String>> blocksOfText = new ArrayList<>();
        ArrayList<String> linesInBlock = new ArrayList<>();

        ArrayList<Week> weeks = new ArrayList<>();

        while (scanner.hasNext()) {
            while (true) {

                String line = scanner.nextLine();

                if ((line.equals("") || line.equals(" ") || line.equals("\f") || line.equals("\f ")) || !scanner.hasNext()) {
                    break;
                }
                linesInBlock.add(line.strip().replaceAll(" +", " ").replaceFirst("^-?(.*)", "$1"));
            }
            if (!linesInBlock.isEmpty()) {
                blocksOfText.add(new ArrayList<>(linesInBlock));
                linesInBlock.clear();
            }
        }

        //remove MENI ZA MESEC...
        blocksOfText.remove(0);


        int index = 0;
        String maxDay = getMaxDay(new File(path));
        boolean firstIteration = true;
        if (Config.isVerbose()) System.out.println("maxDay --->" + maxDay);
        String fromToDate = "";

        LinkedHashMap<String, ArrayList<String>> menuInAWeek = new LinkedHashMap<>();


        for (int i = 0; i < blocksOfText.size(); i++) {

            //get first day of a week
            if (index == 0) {
                fromToDate = translateDate(blocksOfText.get(i).get(0));
                if (Config.isVerbose()) System.out.println(blocksOfText.get(i).get(0));
            }
            
            if (Config.isVerbose()) System.out.println("fromToDate: " + fromToDate);
            int weekIndex = getWeekIndex(fromToDate + java.time.Year.now().getValue());
            if (Config.isVerbose()) System.out.println("Week index -> " + weekIndex);
            if (Config.isVerbose()) System.out.println(blocksOfText.get(i));

            ArrayList<String> tmp = new ArrayList<>();
            //start with 1 to skip days
            for (int j = 1; j < blocksOfText.get(i).size(); j++) {
                tmp.add(blocksOfText.get(i).get(j));
            }
            if(Config.isVerbose()) System.out.println("\033[43mdblocksOfText.get(i).get(0): \033[0m " + blocksOfText.get(i).get(0));
            String[] dateTmp = blocksOfText.get(i).get(0).strip().split("\\s+",2)[1].split("\\.\\s*");
            if(Config.isVerbose()) System.out.println("\033[43mdateTmp: \033[0m " + Arrays.toString(dateTmp));

            String date = "";

            if (dateTmp.length > 1) {
                if (dateTmp[1].length() == 1) dateTmp[1] = "0" + dateTmp[1];
                if (dateTmp[0].length() == 1) dateTmp[0] = "0" + dateTmp[0];
                date = java.time.Year.now().getValue() + "-" + dateTmp[1] + "-" + dateTmp[0];
                if(Config.isVerbose()) System.out.println("\033[43mdate: \033[0m " + date);
            }


            menuInAWeek.put(date, tmp);

            //how many day has a week
            int value = 0;
            //if first week of a month
            if (firstIteration)
                value = 4 - getOffset(blocksOfText.get(0).get(0).split("\\s+")[0]);
            else value = 4;


            if (fromToDate.contains("Invalid day")) break;
            //if it was iterated over one week

            if (index == value || Integer.parseInt(fromToDate.split(" ")[1].split("\\.")[0]) + index == Integer.parseInt(maxDay.split("\\s+")[1].split("\\.")[0])) {
                firstIteration = false;

                //get last date of a week and concatenate with the first day of a week
                fromToDate += " - " + translateDate(blocksOfText.get(i).get(0));

                if (Config.isVerbose()) {
                    System.out.println(blocksOfText.get(i).get(0));
                    System.out.println("--------------------------");
                    System.out.println(fromToDate);
                }

                weeks.add(new Week(fromToDate, new LinkedHashMap<>(menuInAWeek)).setWeekIndex(weekIndex));
                menuInAWeek.clear();
                index = 0;
            } else index++;

        }

        return weeks;
    }

    public static String getMaxDay(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String maxDay = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (Pattern.compile("\\d+\\.\\s*\\d+\\.*").matcher(line).find()) {
                maxDay = line;
            }
        }
        return maxDay;
    }

    public static int getOffset(String day) {
        switch (day) {
            case "PONEDELJEK":
                return 0;
            case "TOREK":
                return 1;
            case "SREDA":
                return 2;
            case "ČETRTEK":
                return 3;
            case "PETEK":
                return 4;
        }
        return -1;
    }

    public static String translateDay(String day) {
        switch (day) {
            case "PONEDELJEK":
                return "Monday";
            case "TOREK":
                return "Tuesday";
            case "SREDA":
                return "Wednesday";
            case "ČETRTEK":
                return "Thursday";
            case "PETEK":
                return "Friday";
        }
        return "Invalid day";
    }

    public static String translateDate(String date) {
        String[] tmp = date.split("\\s+");
        String output = "";
        if(tmp.length == 2) output = translateDay(tmp[0]) + " " + tmp[1];
        else if (tmp.length == 3) output = translateDay(tmp[0]) + " " + tmp[1]+tmp[2];
        else {
            System.err.println("Invalid day format in " + Config.getDefaultInputFile());
            System.exit(1);
        }
        if(!output.endsWith(".")) output = output.strip()+".";
        return output;
    }
}

class Week {
    private int weekIndex;
    private String fromToDate;
    private LinkedHashMap<String, ArrayList<String>> menuInAWeek;

    public int getWeekIndex() {
        return weekIndex;
    }

    public Week setWeekIndex(int weekIndex) {
        this.weekIndex = weekIndex;
        return this;
    }

    public String getFromToDate() {
        return fromToDate;
    }

    public Week setFromToDate(String fromToDate) {
        this.fromToDate = fromToDate;
        return this;
    }

    public LinkedHashMap<String, ArrayList<String>> getMenuInAWeek() {
        return menuInAWeek;
    }

    public Week setMenuInAWeek(LinkedHashMap<String, ArrayList<String>> menuInAWeek) {
        this.menuInAWeek = menuInAWeek;
        return this;
    }

    public Week(String fromToDate, LinkedHashMap<String, ArrayList<String>> menuInAWeek) {
        this.fromToDate = fromToDate;
        this.menuInAWeek = menuInAWeek;
    }
}

class Data {
    private ArrayList weeks;

    public ArrayList getWeeks() {
        return weeks;
    }

    public Data setWeeks(ArrayList weeks) {
        this.weeks = weeks;
        return this;
    }

    public Data(ArrayList weeks) {
        this.weeks = weeks;
    }
}