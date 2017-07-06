/*
 * Project: Linguistic Analysis
 * Program: Ngrams
 * Date: May 2017
 * Written by Bryan Battles
 * Purdue University
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.BreakIterator;
import java.util.*;

public class Ngrams {
    private int n; //max n-gram to go to
    private int t; //number of top n-gram to output
    private ArrayList<HashMap<String, Integer>> maps = new ArrayList<>(); //holds hashmaps of n-grams
    private ArrayList<ArrayList<Map.Entry<String, Integer>>> sortedLists; //sorted lists of n-gram hashmaps
    private static final String[] ABBREVIATIONS = {"Dr." , "Prof." , "Mr." , "Mrs." , "Ms." , "Jr." , "Ph.D."};

    /**
     * Constructor
     * @param n Find n! grams e.g. if n = 3, find all 1-grams, 2-grams, and 3-grams
     */
    public Ngrams(int n, int t) {
        this.n = n;
        this.t = t;
        maps = new ArrayList<>(n);
        sortedLists = new ArrayList<>(n);
        for (int x = 0; x < n; x++) {
            maps.add(new HashMap<>());
            sortedLists.add(new ArrayList<>());
        }
    }

    /**
     * Handles input from text files
     * Reads file from input
     * Splits the file into sentences
     * Forms ideas from the sentences, maps the n-grams, and sorts the map
     * @param filename file name (e.g. "test.txt")
     */
    private void fromFile(String filename) {
        try {
            mapIdeas(toSentences(readFile(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles input from a database
     * @param url url of the database
     * @param user username
     * @param pass password
     * @param sql command to execute
     */
    private void fromDatabase(String url, String user, String pass, String sql) {
        try {
            String URL = "jdbc:mysql://" + url;
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to database...");
            Connection con = DriverManager.getConnection(URL, user, pass);
            System.out.println("Creating statement...");
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(sql);
            String s;
            System.out.println("Reading Entries...");
            while(rs.next()) {
                s = rs.getString("message");
                mapIdeas(toSentences(s));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Takes all sentences and splits them by commas and semicolons into ideas
     * Maps all ideas to HashMap
     * Sorts map to sortedList
     * @param sentences ArrayList holding strings of each sentence
     */
    private void mapIdeas(ArrayList<String> sentences) {
        for (String s : sentences) {
            if (s.contains(",") || s.contains(";")) {
                String[] ideas = s.split("[,;]");
                for (String string : ideas) map(splitString(string));
            } else {
                map(splitString(s));
            }
        }
    }

    /**
     * Creates sorted lists out of maps
     */
    private void sortLists() {
        for (int x = 0; x < n; x++) {
            sortedLists.set(x, mapToSortedList(maps.get(x)));
        }
    }

    /**
     * Splits a string into an array of its words
     * @param s string to split into words
     * @return input split into words
     */
    private String[] splitString(String s) {
        return s.replaceAll("[^a-zA-Z,; ]", "").toLowerCase().trim().split("\\s+");
    }

    /**
     * Splits an input string into an ArrayList of its sentences
     */
    private static ArrayList<String> toSentences(String input) {
        ArrayList<String> result = new ArrayList<>();
        BreakIterator bi = BreakIterator.getSentenceInstance(Locale.US);
        bi.setText(input);
        int start = bi.first();
        int end = bi.next();
        int tempStart = start;
        while (end != BreakIterator.DONE) {
            String sentence = input.substring(start, end);
            if (!hasAbbreviation(sentence)) {
                sentence = input.substring(tempStart, end);
                tempStart = end;
                result.add(sentence);
            }
            start = end;
            end = bi.next();
        }
        return result;
    }

    /**
     * Check if an input sentence contains abbreviations (e.g. Mr. Ms. Dr.)
     * @return true if the input sentence contains abbreviations
     */
    private static boolean hasAbbreviation(String sentence) {
        if (sentence == null || sentence.isEmpty()) return false;
        for (String w : ABBREVIATIONS) if (sentence.contains(w)) return true;
        return false;
    }

    /**
     * Takes an array of words and maps the n-grams present
     * @param s An array of words to map n-grams
     */
    private void map(String[] s) {
        for (int i = 1; i <= n; i++) {
            HashMap<String, Integer> map = maps.get(i - 1);
            for (int j = 0; j < s.length - i + 1; j++) {
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < i; k++) {
                    if (k > 0) sb.append(' ');
                    sb.append(s[j+k]);
                }
                String gram = sb.toString();
                if (!map.containsKey(gram)) {
                    map.put(sb.toString(), 1);
                } else {
                    int freq = map.get(gram);
                    map.replace(gram, freq + 1);
                }
            }
            maps.set(i - 1, map);
        }
    }

    /**
     * @return an ArrayList of sorted entries from the HashMap in descending order
     */
    private ArrayList<Map.Entry<String, Integer>> mapToSortedList(HashMap<String, Integer> map) {
        ArrayList<Map.Entry<String, Integer>> sorted = new ArrayList<>(map.entrySet());
        sorted.sort(Collections.reverseOrder(Comparator.comparingInt(Map.Entry::getValue)));
        return sorted;
    }

    /**
     * Prints the top entries from sortedLists
     */
    private void printTop() {
        sortLists();
        for (int x = 0; x < n; x++) {
            System.out.println("The top " + t + " " + (x+1) + "-grams:");
            ArrayList<Map.Entry<String, Integer>> sortedList = sortedLists.get(x);
            int count = 0;
            for (Map.Entry<String, Integer> e : sortedList) count += e.getValue();
            for (int y = 0; y < t && y < sortedList.size(); y++) {
                Map.Entry entry = sortedList.get(y);
                String key = (String)entry.getKey();
                int freq = ((Integer)entry.getValue()).intValue();
                double percent = ((double)freq / count) * 100.0;
                System.out.println("\"" + key + "\" with frequency = " + freq + " (" + percent + "%)");
            }
            System.out.println();
        }
    }

    /**
     * Reads all text from a file and encodes it as a String
     * @param path filepath (String)
     * @return the text file as as string
     * @throws IOException if there is an error reading the file
     */
    private String readFile(String path) throws IOException {
        File file = new File(path);
        StringBuilder text = new StringBuilder((int)file.length());
        String lineSeparator = System.getProperty("line.separator");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) text.append(scanner.nextLine()).append(lineSeparator);
        String result = text.toString();
        result = result.replace("\n", " ").replace("\r", " ");
        return result;
    }

    public static void main(String[] args) {
        try {
            BufferedReader stan = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("What max N-Gram do you want to find? ");
            int N = Integer.parseInt(stan.readLine());
            System.out.print("How many top results do you want to receive? ");
            int T = Integer.parseInt(stan.readLine());
            Ngrams g = new Ngrams(N, T);
            System.out.print("Do you want to read from a database? (y/n)");
            String choice = stan.readLine().toLowerCase();
            long startTime;
            switch (choice) {
                case "y":
                    String url, user, pass, sql;
                    System.out.print("Enter the URL you want to connect to: jdbc:mysql://");
                    url = stan.readLine();
                    System.out.print("Enter your username: ");
                    user = stan.readLine();
                    System.out.print("Enter your password: ");
                    pass = stan.readLine();
                    System.out.print("Enter the SQL query you would like to execute: ");
                    sql = stan.readLine();
                    System.out.println("Starting Timer...");
                    startTime = System.currentTimeMillis();
                    g.fromDatabase(url, user, pass, sql);
                    g.printTop();
                    break;
                case "n":
                    System.out.print("Please enter your file name: ");
                    String filename = stan.readLine();
                    System.out.print("Your file name is: " + filename + "\n");
                    System.out.println("Starting Timer...");
                    startTime = System.currentTimeMillis();

                    g.fromFile(filename);
                    g.printTop();
                    break;
                default:
                    startTime = 0;
                    System.out.println("Not a valid choice.");
                    System.exit(1);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Execution time = " + (endTime - startTime) + " milliseconds.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
