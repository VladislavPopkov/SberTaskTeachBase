import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DictionarySorter {
    public static void main(String[] args) throws IOException {
        AtomicInteger counterMergeSorter = new AtomicInteger(0);
        int chunkSize = 8;
        String inputFileName = args[0];
        String outputFileName = args[1];
        ArrayList<String> pathToTempFileName = readAndSortChunkInputFile(inputFileName, chunkSize);
        mergeFiles(pathToTempFileName, outputFileName, 0, pathToTempFileName.size() - 1, counterMergeSorter);
        System.out.println("Sorted file was saved to " + "/home/vladislav/IntelejIdeaProject/SberTaskTeachBase/resource/output.txt");
    }

    public static void mergeFiles(List<String> inputFile, String outputFileName, int firstPointer, int secondPointer, AtomicInteger counter) throws IOException {
        if (inputFile.size() == 1) {
            saveToOutputFile(inputFile, outputFileName);
            return;
        }
        List<String> mergesList = new ArrayList<>();
        while (firstPointer <= secondPointer) {
            File tempFile = new File("resource/mergeTempFile" + counter.incrementAndGet() + ".txt");
            if (firstPointer == secondPointer) {
                mergesList.add(inputFile.get(firstPointer));
                break;
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile, true))) {
                try (BufferedReader brFirst = new BufferedReader(new FileReader(inputFile.get(firstPointer)))) {
                    try (BufferedReader brSecond = new BufferedReader(new FileReader(inputFile.get(secondPointer)))) {
                        String firstLine = brFirst.readLine();
                        String secondLine = brSecond.readLine();
                        while (firstLine != null || secondLine != null) {
                            if (firstLine != null && secondLine != null) {
                                if (firstLine.compareTo(secondLine) <= 0) {
                                    bw.write(firstLine);
                                    firstLine = brFirst.readLine();
                                } else {
                                    bw.write(secondLine);
                                    secondLine = brSecond.readLine();
                                }
                            } else if (firstLine != null) {
                                bw.write(firstLine);
                                firstLine = brFirst.readLine();
                            } else if (secondLine != null) {
                                bw.write(secondLine);
                                secondLine = brSecond.readLine();
                            }
                            bw.newLine();
                        }
                    }
                }
            }
            mergesList.add(tempFile.getAbsolutePath());
            deleteFile(inputFile.get(firstPointer++));
            deleteFile(inputFile.get(secondPointer--));
        }
        mergeFiles(mergesList, outputFileName, 0, mergesList.size() - 1, counter);
    }

    public static void saveToOutputFile(List<String> inputFile, String outputFileName) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileName))) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile.get(0)))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            }
            deleteFile(inputFile.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static ArrayList<String> readAndSortChunkInputFile(String fileName, int chunkSize) throws IOException {
        ArrayList<String> pathToTempFileName = new ArrayList<>();
        int indexTempFile = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                if (lines.size() == chunkSize) {
                    File tempFile = createTempFile(indexTempFile++);
                    Collections.sort(lines);
                    Files.write(tempFile.toPath(), lines);
                    lines.clear();
                    pathToTempFileName.add(tempFile.getAbsolutePath());
                }
            }
            if (!lines.isEmpty()) {
                File tempFile = createTempFile(indexTempFile++);
                Collections.sort(lines);
                Files.write(tempFile.toPath(), lines);
                pathToTempFileName.add(tempFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathToTempFileName;
    }

    public static File createTempFile(int index) {
        String tempFileName = "resource/tempFile" + index + ".txt";
        File tempFile = new File(tempFileName);
        return tempFile;
    }
}