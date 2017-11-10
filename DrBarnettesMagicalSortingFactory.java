import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class DrBarnettesMagicalSortingFactory {
    private int[] runI;
    private float[] runK;
    private long time;

    /**
     * sets up the class with a specific file
     * @param file is the file being red from
     * @throws IOException 
     */
    public DrBarnettesMagicalSortingFactory(String file) {
        Parser ps = new Parser(file);
        parse(ps, file);
        run(ps);
        System.out.println(file + " " + time);
    }
    
    /**
     * starts running the sorting process and stores
     *  the beginning and end time
     * @throws IOException 
     */
    public void run(Parser ps) {
        startTimer();
        replacementSelection(ps.getID()[0], ps.getKey()[0], ps.getID()[1], ps.getKey()[1]);
        stopTimer();
    }
    
    /**
     * parses the file
     * @param file is the string form of the file
     * @throws IOException 
     */
    private void parse(Parser ps, String file) {
        Scanner sc = new Scanner(file);
        ps.parse(sc);
        
        int[][] ids = ps.getID();
        float[][] keys = ps.getKey();
        if (ids.length == 0 || keys.length == 0) {
            System.out.println("No records found in file.");
            System.exit(1);
        }
        sc.close();
    }
    
    /**
     * runs the replacement selection sort
     * @param ids are the ID values
     * @param keys are the key values
     * @param blockIdx the index of the block
     * @return an array of ints
     * @throws IOException 
     */
    private void replacementSelection(int[] idsOut, float[] keysOut, int[] idsIn, float[] keysIn) {
        Heap heap = new Heap(idsOut, keysOut);
        if (idsIn == null) {
            runI = heap.toArray();
            runK = heap.toArrayF();
            return;
        }
        
        int size = idsOut.length + idsIn.length;
        int[] outBuffer = new int[size];
        float[] outBufferF = new float[size];
        int[] list = new int[size];
        float[] listF = new float[size];
        int idx_in = 0;
        int idx_out = 0;
        int idx_list = 0;
        
        while (!heap.isEmpty()) {
            float[] minPack = heap.deleteMin();
            outBuffer[idx_out] = (int) minPack[0];
            outBufferF[idx_out] = minPack[1];
            idx_out++;
            
            if (idx_in < idsIn.length) {
                int next = idsIn[idx_in];
                idx_in++;
                if (next >= outBuffer[idx_out - 1]) {
                    heap.insert(next, keysIn[idx_in]);
                }
                else {
                    list[idx_list] = next;
                    listF[idx_list] = keysIn[idx_in];
                    idx_list++;
                }
            }
            // TODO put buffer in a run
            heap = new Heap(list, listF);
            outBuffer = new int[size];
            outBufferF = new float[size];
            
            //this will will make the temp file and have it delete when the
                //program closes, *write to this for runs
            File temp = null;
            try {
                temp = File.createTempFile("runs", ".bin");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            temp.deleteOnExit();
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
        
        ByteBuffer b1 = p.readRuns(runPositions[0], inFile, runLengths[0]);
        ByteBuffer b2 = p.readRuns(runPositions[1], inFile, runLengths[0]);
        //does not account for a run being a different size yet
        while (pos1 < runLengths[0] && pos2 < runLengths[1])
        {
            float f1 = b1.getFloat(pos1 * 2);
            float f2 = b2.getFloat(pos2 * 2);
            if (f1 > f2)
            {
                //output f2
                pos2++;
            }
            if (f1 < f2)
            {
                //output f1
                pos1++;
            }
            if (f1 == f2)
            {
                int i1 = b1.getInt(pos1 * 2 - 1);
                int i2 = b2.getInt(pos2 * 2 - 1);
                if (i1 > i2)
                {
                    //output i2
                    pos2++;
                }
                else
                {
                    //output i1
                    pos1++;
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
