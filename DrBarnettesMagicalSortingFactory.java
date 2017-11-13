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
        } catch (IOException e) {
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
        
        int length = Parser.getLength(file);
        Heap heap = heapify(Parser.readBlock(0, file));
        Boolean append = false;
        if (length == 512 * 8) {
            writeHeap(heap, file, append);
            return;
        }

        int blockNum = 1;
        ByteBuffer bBuffer = Parser.readBlock(blockNum, file);
        
        int[] runPos = new int[length/512];
        int[] runLen = new int[length/512];
        int runCount = 0;
        int runStart = 0;
        
        int[] list = new int[512 * 8];
        float[] listF = new float[512 * 8];
        int idxList = 0;
        
        do {
            idxList = 0;
            int runEnd = 0;
            while (!heap.isEmpty()) {
                float[] minPack = heap.deleteMin();

                int next = bBuffer.getInt();
                float nextF = bBuffer.getFloat();
                if (nextF >= minPack[1]) {
                    if (nextF == minPack[1] && next < (int) minPack[0]) {
                        Parser.writeRecord(output, next, nextF, append);
                        System.out.println(nextF);
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
                System.out.println(minPack[1]);
                runEnd++;

                if (!bBuffer.hasRemaining()) {
                    bBuffer.clear();
                    if ((blockNum * 2) * 512 * 8 >= length) {
                        runEnd += 512 * 8 - idxList;
                        writeHeap(heap, output, append);
                        break;
                    }
                    else {
                        blockNum++;
                        bBuffer = Parser.readBlock(blockNum, file);
                    }
                }
                append = true;
            }
            
            runPos[runCount] = runStart;
            runLen[runCount] = runEnd;
            runStart = runPos[runCount] + runLen[runCount];
            runCount++;

            heap = new Heap(list, listF, idxList);
            heap.sort();
            list = new int[512 * 8];
            listF = new float[512 * 8];
        } while (bBuffer.position() != 0);

        int runEnd = idxList;
        runPos[runCount] = runStart;
        runLen[runCount] = runEnd;
        writeHeap(heap, output, append);
        
        System.out.println(System.currentTimeMillis() - time); //TODO delete before submit
        
        Parser.fileClose();
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
     */
    public void writeHeap(Heap heap, String output, Boolean append) {
        float[] array = heap.deleteMin();
        Parser.writeRecord(output, (int) array[0], array[1], append);
        append = true;
        while (!heap.isEmpty())
        {
            array = heap.deleteMin();
            Parser.writeRecord(output, (int) array[0], array[1], append);
            System.out.println(array[1]);
        }
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
            , int[] runLengths) throws IOException
    {
        File tempFile = null;
        try
        {
            tempFile = File.createTempFile("temp", ".bin");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        tempFile.deleteOnExit();
        int numRuns = runPositions.length;
        Boolean append = false;
        if (numRuns == 1)
        {
            //make outfile have contents of infile
            File file = new File(inFile);
            copyFileToFile(file, outFile);
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
        if (runLengths[0] < 512 * 8)
        {
            b1 = Parser.readRuns(runPositions[0], inFile, runLengths[0]);
        }
        else
        {
            b1 = Parser.readRuns(runPositions[0], inFile, 512 * 8 * 8);
        }
        if (runLengths[1] < 512 * 8)
        {
            b2 = Parser.readRuns(runPositions[1], inFile, runLengths[1]);
        }
        else
        {
            b2 = Parser.readRuns(runPositions[1], inFile, 512 * 8 * 8);
        }
        while (pos1 < runLengths[0] && pos2 < runLengths[1])
        {
            //this is for if it hits the end of the block
            if (pos1 == 512 * 8)
            {
                if (runLengths[0] - cap1 < 512 * 8)
                {
                    b1 = Parser.readRuns(cap1, inFile, runLengths[0] - cap1);
                }
                else
                {
                    b1 = Parser.readRuns(cap1, inFile, 512 * 8 * 8);
                }
                pos1 = 0;
            }
            if (pos2 == 512 * 8)
            {
                if (runLengths[1] - cap2 < 512 * 8)
                {
                    b2 = Parser.readRuns(cap2, inFile, runLengths[0] - cap2);
                }
                else
                {
                    b2 = Parser.readRuns(cap2, inFile, 512 * 8 * 8);
                }
                pos2 = 0;
            }
            float f1 = b1.getFloat(pos1 * 2);
            float f2 = b2.getFloat(pos2 * 2);
            if (f1 > f2)
            {
                //output f2
                Parser.writeRecord(tempFile.getName(), b2.getInt(pos2 * 2 - 1),
                        f2, append);
                append = true;
                pos2++;
                cap2++;
            }
            if (f1 < f2)
            {
                //output f1
                System.out.println(pos1*2-1); //TODO
                Parser.writeRecord(tempFile.getName(), b1.getInt(pos1 * 2 - 1),
                        f1, append);
                append = true;
                pos1++;
                cap1++;
            }
            if (f1 == f2)
            {
                int i1 = b1.getInt(pos1 * 2 - 1);
                int i2 = b2.getInt(pos2 * 2 - 1);
                if (i1 > i2)
                {
                    //output i2
                    Parser.writeRecord(tempFile.getName(), i2, f2, append);
                    append = true;
                    pos2++;
                    cap2++;
                }
                else
                {
                    //output i1
                    Parser.writeRecord(tempFile.getName(), i1, f1, append);
                    append = true;
                    cap1++;
                    pos1++;
                }
            }
        }
        writeLastOfValues(tempFile, b1, cap1, runLengths[0]);
        writeLastOfValues(tempFile, b2, cap2, runLengths[1]);
        int[] newPositions = arrayThings(runPositions);
        int[] newLengths = arrayThings(runLengths);
        //write buffer to file
        if (newLengths.length == 1)
        {
            //output to outFile
            copyFileToFile(tempFile, outFile);
        }
        else
        {
            //place the temp file's values into the inFile
            writeInRun(tempFile, inFile);
            //out
            sortRuns(inFile, outFile, newPositions, newLengths);
        }
        Parser.fileClose();
        Parser.closeOutput();
    }
    /**
     * writes the end of a buffer
     * @param file file to be written to
     * @param b bytebuffer containing the values
     */
    private void writeLastOfValues(File file, ByteBuffer b, int length, int max)
    {
        while(length < max || b.hasRemaining())
        {
            if (!b.hasRemaining())
            {
                //read in next part of run
                if (max - length < 512 * 8)
                {
                    b = Parser.readRuns(length, file.getName(), max - length);
                }
                else
                {
                    b = Parser.readRuns(length, file.getName(), 512 * 8 * 8);
                }
            }
            length++;
            Parser.writeRecordOutput(file.getName(), b.getInt(), b.getFloat(), true);
        }
    }
    /**
     * 
     * @param array
     * @return
     */
    private int[] arrayThings(int[] array)
    {
        int[] rArray = new int[array.length - 1];
        for (int i = 1; i < array.length; i++)
        {
            rArray[i - 1] = array[i];
        }
        rArray[0] = array[0] + array[1];
        return rArray;
    }
    /**
     * copies a file to a different file
     * @param inFile is the input file
     * @param outFile is the output file
     * @throws IOException
     */
    private void copyFileToFile(File inFile, String outFile) throws IOException
    {
        RandomAccessFile fout = new RandomAccessFile(outFile, "rws");
        FileInputStream fin = new FileInputStream(inFile);
        FileChannel in = fin.getChannel();
        FileChannel out = fout.getChannel();
        out.transferFrom(in, 0, in.size());
        fin.close();
        fout.close();
    }
    /**
     * writes the run to a file
     * @param tempFile is the file holding run 1
     * @param outFile is where it needs to go
     * @throws IOException
     */
    private void writeInRun(File tempFile, String outFile) throws IOException
    {
        Scanner scan = new Scanner(tempFile);
        RandomAccessFile access = new RandomAccessFile(outFile, "rws");
        while (scan.hasNext())
        {
            access.writeInt(scan.nextInt());
            access.writeFloat(scan.nextFloat());
        }
        scan.close();
        access.close();
    }
    
}
