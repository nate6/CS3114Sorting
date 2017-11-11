import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class DrBarnettesMagicalSortingFactory {
    private String file;
    private Parser ps;
    private int length;
    private long time;

    /**
     * sets up the class with a specific file
     * @param file is the file being red from
     * @throws IOException 
     */
    public DrBarnettesMagicalSortingFactory(String file) {
        this.file = file;
        ps = new Parser(file);
        parse();
        run();
        System.out.println(file + " " + time);
    }
    
    /**
     * starts running the sorting process and stores
     *  the beginning and end time
     * @throws IOException 
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
            //TODO exit
        }
        temp.deleteOnExit();
        Heap heap = heapify(ps.readBlock(0, file));
        ByteBuffer bBuffer = ps.readBlock(1, file);
        replacementSelection(heap, bBuffer, 1, "runs.bin");
        stopTimer();
    }
    
    /**
     * parses the file
     * @param file is the string form of the file
     * @throws IOException 
     */
    private void parse() {
        Scanner sc = new Scanner(file);
        ps.parse(sc);
        
        int[][] ids = ps.getID();
        float[][] keys = ps.getKey();
        if (ids.length == 0 || keys.length == 0) {
            System.out.println("No records found in file.");
            System.exit(1);
        }
        sc.close();
        
        length = ((int) new File(file).length()) / 8;
    }
    
    /**
     * runs the replacement selection sort
     * @param ids are the ID values
     * @param keys are the key values
     * @param blockIdx the index of the block
     * @return an array of ints
     * @throws IOException 
     */
    private void replacementSort(Heap heap, ByteBuffer bBuffer, int pos, String output) {
        if (length == 512 * 8) {
            heap.toArray(); //TODO
            heap.toArrayF();
            return;
        }
        if (!bBuffer.hasRemaining()) {
            pos++;
            bBuffer = ps.readBlock(pos, file);
            if (bBuffer == null) {
                // TODO 
            }
        }
        
        int[] runPositions = new int[8];
        int[] runLengths = new int[8];
        
        int[] list = new int[512 * 8];
        float[] listF = new float[512 * 8];
        int idx_in = 0;
        int idx_list = 0;
        
        do {
            while (!heap.isEmpty()) {
                float[] minPack = heap.deleteMin();
                ps.writeRecord(output, (int) minPack[0], minPack[1], true);
                
                int next = idsIn[idx_in];
                idx_in++;
                if (next >= 0) {
                    heap.insert(next, keysIn[idx_in]);
                }
                else {
                    list[idx_list] = next;
                    listF[idx_list] = keysIn[idx_in];
                    idx_list++;
                }
            }

            heap = new Heap(list, listF);
            outBuffer = new int[size];
            outBufferF = new float[size];
        } while (idx_in < size);
        
        sortRuns(inFile, outFile, runPositions, runLengths);
    }
    
    private Heap heapify(ByteBuffer bBuffer) {
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
    private void sortRuns(String inFile, String outFile, int[] runPositions
            , int[] runLengths) throws IOException
    {
        Parser p = new Parser(inFile);
        
        int numRuns = runPositions.length;
        if (numRuns == 1)
        {
            //make outfile have contents of infile
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
            b1 = p.readRuns(runPositions[0], inFile, runLengths[0]);
        }
        else
        {
            b1 = p.readRuns(runPositions[0], inFile, 512 * 8 * 8);
        }
        if (runLengths[1] < 512 * 8)
        {
            b2 = p.readRuns(runPositions[1], inFile, runLengths[1]);
        }
        else
        {
            b2 = p.readRuns(runPositions[1], inFile, 512 * 8 * 8);
        }
        while (pos1 < runLengths[0] && pos2 < runLengths[1])
        {
            //this is for if it hits the end of the block
            if (pos1 == 512 * 8)
            {
                if (runLengths[0] - cap1 < 512 * 8)
                {
                    b1 = p.readRuns(cap1, inFile, runLengths[0] - cap1);
                }
                else
                {
                    b1 = p.readRuns(cap1, inFile, 512 * 8 * 8);
                }
                pos1 = 0;
            }
            if (pos2 == 512 * 8)
            {
                if (runLengths[1] - cap2 < 512 * 8)
                {
                    b2 = p.readRuns(cap2, inFile, runLengths[0] - cap2);
                }
                else
                {
                    b2 = p.readRuns(cap2, inFile, 512 * 8 * 8);
                }
                pos2 = 0;
            }
            float f1 = b1.getFloat(pos1 * 2);
            float f2 = b2.getFloat(pos2 * 2);
            if (f1 > f2)
            {
                //output f2
                pos2++;
                cap2++;
            }
            if (f1 < f2)
            {
                //output f1
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
                    pos2++;
                    cap2++;
                }
                else
                {
                    //output i1
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
        }
        else
        {
            sortRuns(inFile, outFile, newPositions, newLengths);
        }
        
    }
    
}
