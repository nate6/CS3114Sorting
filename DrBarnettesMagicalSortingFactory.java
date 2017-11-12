import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 * Handles the Replacement Sort and Merge Sort for file.
 * 
 * @author Drew Bond <dbond07>
 *         Nate Axt <nate6>
 * @version 11.10.2017
 */
public class DrBarnettesMagicalSortingFactory {
    private String file;
    private long time;

    /**
     * sets up the class with a specific file
     * @param file is the file being read from
     * @param statFile is the file to write stats to
     */
    public DrBarnettesMagicalSortingFactory(String file, String statFile) {
        this.file = file;
        run();
        Parser.closeBIS();
        getStatistics(statFile);
    }
    
    /**
     * starts running the sorting process and stores
     *  the beginning and end time
     */
    public void run() {
        startTimer();
        //this will will make the temp file and have it delete when the
        //program closes, *write to this for runs
        File temp = null;
        try {
            temp = File.createTempFile("runs", ".bin");
            temp.deleteOnExit();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        replacementSort("runs.bin");
        stopTimer();
    }
    /**
     * runs the replacement selection sort
     * @param output runs output
     */
    public void replacementSort(String output) {
        
        Parser.setBIS(file);
        int length = Parser.getLength(file);
        Heap heap = heapify(Parser.readBlock());
        Boolean append = false;
        if (length == 512 * 8) {
            writeHeap(heap, file, append);
            return;
        }        
        
        int blockNum = 1;
        ByteBuffer bBuffer = Parser.readBlock();
        
        int[] runPos = new int[length / 512 / 8];
        int[] runLen = new int[length / 512 / 8];
        int runCount = 0;
        int runStart = 0;
        
        int[] list = new int[512 * 8];
        float[] listF = new float[512 * 8];
        int idxList = 0;
        
        do {
            int runEnd = 0;
            while (!heap.isEmpty()) {
                float[] minPack = heap.deleteMin();

                int next = bBuffer.getInt();
                float nextF = bBuffer.getFloat();
                if (nextF >= minPack[1]) {
                    if (nextF == minPack[1] && next <= (int) minPack[0]) {
                        Parser.writeRecord(output, next, nextF, append);
                        runEnd++;
                    }
                    else {
                        heap.insert(next, nextF);
                    }
                }
                else {
                    list[idxList] = next;
                    listF[idxList] = nextF;
                    idxList++;
                }

                Parser.writeRecord(output, (int) minPack[0], 
                        minPack[1], append);
                runEnd++;

                if (!bBuffer.hasRemaining()) {
                    bBuffer.clear();
                    blockNum++;
                    int[][] runs = incrementRuns(runPos, runLen);
                    runPos = runs[0];
                    runLen = runs[1];
                    if (blockNum * 512 * 8 > length) {
                        writeHeap(heap, output, append);
                        break;
                    }
                    bBuffer = Parser.readBlock();
                }
                append = true;
            }

            runPos[runCount] = runStart;
            runLen[runCount] = runEnd;
            runStart = runPos[runCount] + runLen[runCount];
            runCount++;

            heap = new Heap(list, listF);
            heap.sort();
            list = new int[512 * 8];
            listF = new float[512 * 8];
            idxList = 0;
        } while (blockNum * 512 * 8 > length);
        
        bBuffer.clear();
        
        Parser.closeBIS();
        Parser.setBIS(output);
        try {
            sortRuns(output, file, runPos, runLen);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sends buffer into heap.
     * @param bBuffer bytes
     * @return those bytes in a heap
     */
    public Heap heapify(ByteBuffer bBuffer) {
        int[] intHeap = new int[512 * 8];
        float[] floatHeap = new float[512 * 8];
        int i = 0;
        
        while (bBuffer.hasRemaining()) {
            intHeap[i] = bBuffer.getInt();
            floatHeap[i] = bBuffer.getFloat();            
            i++;
        }
        
        Heap heap = new Heap(intHeap, floatHeap);
        heap.sort();
        bBuffer.clear();
        return heap;
    }
    
    /**
     * Writes the heap values to a run.
     * @param heap of bytes
     * @param output file name for runs
     * @param append if we are appending to a file
     */
    public void writeHeap(Heap heap, String output, Boolean append) {
        int[] arrayI = heap.toArray();
        float[] arrayF = heap.toArrayF();
        Parser.writeRecord(output, arrayI[0], arrayF[0], append);
        append = true;
        for (int i = 1; i < heap.toArray().length; i++) {
            Parser.writeRecord(output, arrayI[i], 
                    arrayF[i], append);
        }
    }
    /**
     * Resizes runs arrays.
     * @param runPos run positions array
     * @param runLen run lengths array
     * @return runPos and runLen with new sizes, filled, 
     *         as a int[0][] and int[1][]
     */
    public int[][] incrementRuns(int[] runPos, int[] runLen)
    {
        int[][] runs = new int[2][runPos.length + 8];
        for (int i = 0; i < runPos.length; i++) {
            runs[0][i] = runPos[i];
            runs[1][i] = runLen[i];
        }
        return runs;
    }
    
    /**
     * starts the timer
     */
    private void startTimer() {
        time = System.currentTimeMillis();
    }
    
    /**
     * ends the timer and gives you the time between the run
     */
    private void stopTimer() {
        time = System.currentTimeMillis() - time;
    }
    
    /**
     * Writes statistics to file
     * @param statFile to write to
     */
    public void getStatistics(String statFile) {
        File stFile = new File(statFile);
        String out = this.file + " " + time + "\n";
        if (stFile.exists()) {
            try {
                FileWriter fw = new FileWriter(stFile, true);
                fw.write(out);
                fw.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(stFile);
            } 
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writer.println(out);
            writer.close();
        }
    }
    
    /**
     * sorts the runs after the runs are placed in the file
     * @param inFile bin file name
     * @param outFile store file name
     * @param runPositions start pos for runs
     * @param runLengths lengths of the runs
     * @throws IOException 
     */
    public void sortRuns(String inFile, String outFile, int[] runPositions
            , int[] runLengths) throws IOException {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp", ".bin");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        tempFile.deleteOnExit();
        
        int numRuns = runPositions.length;
        Boolean append = false;
        if (numRuns == 1) {
            //make outfile have contents of infile
            FileInputStream fin = new FileInputStream(inFile);
            RandomAccessFile fout = new RandomAccessFile(outFile, "rws");
            FileChannel in = fin.getChannel();
            FileChannel out = fout.getChannel();
            out.transferFrom(in, 0, in.size());
            fin.close();
            fout.close();
            in.close();
            out.close();
            return;
        }
        
        //i'm just going to merge 2 at a time
        int pos1 = 0;
        int pos2 = 0;
        int cap1 = 0;
        int cap2 = 0;
        ByteBuffer b1;
        ByteBuffer b2;
        //reads in the runs, if they are larger than an 8block then only
            //the 512 * 8 records are read in
        
        if (runLengths[0] < 512 * 8) {
            b1 = Parser.readRuns(runLengths[0]);
        }
        else {
            b1 = Parser.readRuns(512 * 8 * 8);
        }
        if (runLengths[1] < 512 * 8) {
            b2 = Parser.readRuns(runLengths[1]);
        }
        else {
            b2 = Parser.readRuns(512 * 8 * 8);
        }
        while (pos1 < runLengths[0] && pos2 < runLengths[1]) {
            //this is for if it hits the end of the block
            if (pos1 == 512 * 8) {
                if (runLengths[0] - cap1 < 512 * 8) {
                    b1 = Parser.readRuns(runLengths[0] - cap1);
                }
                else {
                    b1 = Parser.readRuns(512 * 8 * 8);
                }
                pos1 = 0;
            }
            if (pos2 == 512 * 8) {
                if (runLengths[1] - cap2 < 512 * 8) {
                    b2 = Parser.readRuns(runLengths[0] - cap2);
                }
                else {
                    b2 = Parser.readRuns(512 * 8 * 8);
                }
                pos2 = 0;
            }
            
            float f1 = b1.getFloat(pos1 * 2);
            float f2 = b2.getFloat(pos2 * 2);
            if (f1 > f2) {
                //output f2
                Parser.writeRecord(tempFile.getName(), b2.getInt(pos2 * 2 - 1),
                        f2, append);
                append = true;
                pos2++;
                cap2++;
            }
            if (f1 < f2) {
                //output f1
                Parser.writeRecord(tempFile.getName(), b1.getInt(pos1 * 2 - 1),
                        f1, append);
                append = true;
                pos1++;
                cap1++;
            }
            if (f1 == f2) {
                int i1 = b1.getInt(pos1 * 2 - 1);
                int i2 = b2.getInt(pos2 * 2 - 1);
                if (i1 > i2) {
                    //output i2
                    Parser.writeRecord(tempFile.getName(), i2, f2, append);
                    append = true;
                    pos2++;
                    cap2++;
                }
                else {
                    //output i1
                    Parser.writeRecord(tempFile.getName(), i1, f1, append);
                    append = true;
                    pos1++;
                    pos2++;
                }
            }
        }
        if (b1.hasRemaining()) {
            //need to output the rest of them
            Parser.writeRecord(tempFile.getName(), b1.getInt(), 
                    b1.getFloat(), true);
            pos1++;
        }
        while (b2.hasRemaining()) {
            //need to output the rest of them
            Parser.writeRecord(tempFile.getName(), b2.getInt(), 
                    b2.getFloat(), true);
            pos2++;
        }
        
        b1.clear();
        b2.clear();
        
        int[] newPositions = new int[runPositions.length - 1];
        int[] newLengths = new int[runLengths.length - 1];
        newPositions[0] = runPositions[0] + runPositions[1];
        newLengths[0] = runLengths[0] + runLengths[1];
        //write buffer to file
        if (newLengths.length == 1) {
            //output to outFile
            RandomAccessFile fout = new RandomAccessFile(outFile, "rws");
            FileInputStream fin = new FileInputStream(tempFile);
            FileChannel in = fin.getChannel();
            FileChannel out = fout.getChannel();
            out.transferFrom(in, 0, in.size());
            fin.close();
            fout.close();
            in.close();
            out.close();
        }
        else {
            //place the temp file's values into the inFile
            Scanner scan = new Scanner(tempFile);
            RandomAccessFile access = new RandomAccessFile(inFile, "rws");
            while (scan.hasNext()) {
                access.writeInt(scan.nextInt());
                access.writeFloat(scan.nextFloat());
            }
            scan.close();
            access.close();
            //out
            sortRuns(inFile, outFile, newPositions, newLengths);
        }
        
    }
    
}
