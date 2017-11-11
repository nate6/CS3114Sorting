import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private int length;
    private long time;

    /**
     * sets up the class with a specific file
     * @param file is the file being read from
     */
    public DrBarnettesMagicalSortingFactory(String file) {
        this.file = file;
        run();
        System.out.println(file + " " + time);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        temp.deleteOnExit();
        replacementSort("runs.bin");
        stopTimer();
    }
    
    /**
     * runs the replacement selection sort
     * @param output runs output
     */
    public void replacementSort(String output) {
        
        Heap heap = heapify(Parser.readBlock(0, file));        
        if (length == 512 * 8) {
            writeHeap(heap, output);
            return;
        }

        int blockNum = 1;
        ByteBuffer bBuffer = Parser.readBlock(blockNum, file);
        
        int[] runPos = new int[8];
        int[] runLen = new int[8];
        int runCount = 0;
        int runStart = 0;
        
        int[] list = new int[512 * 8];
        float[] listF = new float[512 * 8];
        int idx_list = 0;
        
        do {
            int runEnd = 0;
            while (!heap.isEmpty()) {
                float[] minPack = heap.deleteMin();
                Parser.writeRecord(output, (int) minPack[0], minPack[1], true);
                runEnd++;
                
                int next = bBuffer.getInt();
                if (next >= 0) {
                    heap.insert(next, bBuffer.getFloat());
                }
                else {
                    list[idx_list] = next;
                    listF[idx_list] = bBuffer.getFloat();
                    idx_list++;
                }

                if (!bBuffer.hasRemaining()) {
                    blockNum++;
                    bBuffer = Parser.readBlock(blockNum, file);
                    int[][] runs = incrementRuns(runPos, runLen);
                    runPos = runs[0];
                    runLen = runs[1];
                    if (blockNum * 512 * 8 > length) {
                        writeHeap(heap, output);
                        break;
                    }
                }
            }
            
            runPos[runCount] = runStart;
            runLen[runCount] = runEnd;
            runCount++;

            heap = new Heap(list, listF);
            list = new int[512 * 8];
            listF = new float[512 * 8];
        } while (blockNum * 512 * 8 > length);
        
        try {
            sortRuns(output, file, runPos, runLen);
        } catch (IOException e) {
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
    public void writeHeap(Heap heap, String output) {
        for (int i = 0; i < heap.toArray().length; i++) {
            Parser.writeRecord(output, heap.toArray()[i], 
                    heap.toArrayF()[i], true);
        }
    }
    
    public int[][] incrementRuns(int[] runPos, int[] runLen) {
        int[][] runs = new int[2][runPos.length * 2];
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
     * sorts the runs after the runs are placed in the file
     * @throws IOException 
     */
    @SuppressWarnings("resource")
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
        if (numRuns == 1)
        {
            //make outfile have contents of infile
            FileChannel in = new FileInputStream(inFile).getChannel();
            FileChannel out = new FileInputStream(outFile).getChannel();
            out.transferFrom(in, 0, in.size());
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
                        f2, true);
                pos2++;
                cap2++;
            }
            if (f1 < f2)
            {
                //output f1
                Parser.writeRecord(tempFile.getName(), b1.getInt(pos1 * 2 - 1),
                        f1, true);
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
                    Parser.writeRecord(tempFile.getName(), i2, f2, true);
                    pos2++;
                    cap2++;
                }
                else
                {
                    //output i1
                    Parser.writeRecord(tempFile.getName(), i1, f1, true);
                    pos1++;
                    pos2++;
                }
            }
        }
        int[] newPositions = new int[runPositions.length - 1];
        int[] newLengths = new int[runLengths.length - 1];
        newPositions[0] = runPositions[0] + runPositions[1];
        newLengths[0] = runLengths[0] + runLengths[1];
        //write buffer to file
        if (newLengths.length == 1)
        {
            //output to outFile
            FileChannel in = new FileInputStream(tempFile).getChannel();
            FileChannel out = new FileInputStream(outFile).getChannel();
            out.transferFrom(in, 0, in.size());
        }
        else
        {
            //place the temp file's values into the inFile
            Scanner scan = new Scanner(tempFile);
            RandomAccessFile access = new RandomAccessFile(inFile, "rws");
            while(scan.hasNext())
            {
                access.writeInt(scan.nextInt());
                access.writeFloat(scan.nextFloat());
            }
            //out
            sortRuns(inFile, outFile, newPositions, newLengths);
        }
        
    }
    
}
