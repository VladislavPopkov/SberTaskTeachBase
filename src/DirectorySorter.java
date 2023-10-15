import java.io.*;
import java.util.*;

public class DirectorySorter {
    private static final String SEPARATOR = " ";

    public static void main(String[] args) {
        String inputFilePath = "resource/input.txt";
        String outputFilePath = "resource/output.txt";

        try {
            sortDirectory(inputFilePath, outputFilePath);
            System.out.println("Справочник успешно отсортирован.");
        } catch (IOException e) {
            System.out.println("Ошибка при сортировке справочника: " + e.getMessage());
        }
    }

    public static void sortDirectory(String inputFilePath, String outputFilePath) throws IOException {
        List<String> lines = readDirectory(inputFilePath);
        Collections.sort(lines, Comparator.comparing(DirectorySorter::getTerm));
        saveDirectory(lines, outputFilePath);
    }

    private static List<String> readDirectory(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
    private static String getTerm(String line) {
        return line.split(SEPARATOR)[0];
    }

    private static void saveDirectory(List<String> lines, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}