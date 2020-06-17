

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExtMergeSort {

    class Record {
        private String line;
        private int fileIndex;

        public Record(String line) {
            this.line = line;
        }

        public Record(String line, int fileIndex) {
            this.line = line;
            this.fileIndex = fileIndex;
        }

        public int getFileIndex() {
            return this.fileIndex;
        }
    }

    class SortRecord implements Comparator<Record> {
        @Override
        public int compare(Record record1, Record record2) {
            String[] split1 = record1.line.split(" "), split2 = record2.line.split(" ");
            String w1 = split1[3], w2 = split2[3];
            int n, i;
            n = 10 - w1.length();
            for (i = 0; i < n; i++) w1 += " ";
            n = 10 - w2.length();
            for (i = 0; i < n; i++) w2 += " ";
            int c = w1.compareTo(w2);
            if (c == 0) {
                w1 = split1[3 + n + 1 + 1];
                w2 = split2[3 + n + 1 + 1];
                n = 10 - w1.length();
                for (i = 0; i < n; i++) w1 += " ";
                n = 10 - w2.length();
                for (i = 0; i < n; i++) w2 += " ";
                c = w1.compareTo(w2);
            }
            return c;
        }
    }

    private String inputFile, outputFile;
    private int runNumber;

    public ExtMergeSort(String inputFile, String outputFile, int memory) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.runNumber = memory * 1024 * 1024 / 37;
    }

    private File createTempFile(int index) {
        File file = new File("tmp" + index);
        file.deleteOnExit();
        return file;
    }

    public void sort() throws IOException {
        FileReader fileReader;
        BufferedReader bufReader;
        ArrayList<FileReader> fileReaders;

        FileWriter fileWriter;
        BufferedWriter bufWriter;
        ArrayList<BufferedReader> bufReaders;

        List<Record> lines = new ArrayList<Record>();
        Record sortedLine;
        String line;
        int n = 0, i, fileNumber = 0;

        fileReader = new FileReader(this.inputFile);
        bufReader = new BufferedReader(fileReader);
        while ((line = bufReader.readLine()) != null) {
            if ("".equals(line.trim())) continue;
            n++;
            lines.add(new Record(line));
            if (n == this.runNumber) {
                Collections.sort(lines, new SortRecord());

                fileWriter = new FileWriter(createTempFile(fileNumber++));
                bufWriter = new BufferedWriter(fileWriter);
                for (i = 0; i < lines.size(); i++) bufWriter.append(lines.get(i).line + "\n");
                bufWriter.close();
                fileWriter.close();

                n = 0;
                lines.clear();
            }
        }

        bufReader.close();
        fileReader.close();

        if (n > 0) {
            Collections.sort(lines, new SortRecord());

            fileWriter = new FileWriter(createTempFile(fileNumber));
            bufWriter = new BufferedWriter(fileWriter);
            for (i = 0; i < lines.size(); i++) bufWriter.append(lines.get(i).line + "\n");
            bufWriter.close();
            fileWriter.close();

            lines.clear();
        }

        fileReaders = new ArrayList<FileReader>();
        bufReaders = new ArrayList<BufferedReader>();
        for (i = 0; i <= fileNumber; i++) {
            fileReaders.add(new FileReader("tmp" + i));
            bufReaders.add(new BufferedReader(fileReaders.get(i)));
        }

        fileWriter = new FileWriter(this.outputFile);
        bufWriter = new BufferedWriter(fileWriter);

        for (i = 0; i < bufReaders.size(); i++) {
            if ((line = bufReaders.get(i).readLine()) != null) {
                lines.add(new Record(line, i));
            }
        }
        do {
            if (lines.size() == 0) break;
            Collections.sort(lines, new SortRecord());

            sortedLine = lines.get(0);
            lines.remove(0);
            bufWriter.append(sortedLine.line + "\n");
            n = sortedLine.getFileIndex();

            if ((line = bufReaders.get(n).readLine()) != null ) {
                lines.add(new Record(line, n));
            }
        } while(true);

        bufWriter.close();
        fileWriter.close();

        for (i = 0; i < bufReaders.size(); i++) {
            bufReaders.get(i).close();
            fileReaders.get(i).close();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("usage: extMergeSort [inputfile] [outputfile] [MB of mem to use]");

        }
        ExtMergeSort extmergesort = new ExtMergeSort(args[0], args[1], Integer.parseInt(args[2]));
        extmergesort.sort();
    }
}
