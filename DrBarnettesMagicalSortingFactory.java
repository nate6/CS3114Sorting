import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class DrBarnettesMagicalSortingFactory {
    private String file;
    private int length;
    private long time;

    /**
     * sets up the class with a specific file
     * @param file is the file being red from
     * @throws IOException 
     */
    public DrBarnettesMagicalSortingFactory(String file) {
        this.file = file;
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
        }
        temp.deleteOnExit();
        replacementSort("runs.bin");
        stopTimer();
    }
    
    /**
     * runs the replacement selection sort
     */
    private void replacementSort(String output) {
        
        Heap heap = heapify(Parser.readBlock(0, file));        
        if (length == 512 * 8) {
            writeHeap(heap, output);
            return;
        }

        int blockNum = 1;
        ByteBuffer bBuffer = Parser.readBlock(blockNum, file);
        
        int[] runPos = new int[8];
        int[] runLen = new int[8];
        int[] list = new int[512 * 8];
        float[] listF = new float[512 * 8];
        int idx_list = 0;
        
        do {
            while (!heap.isEmpty()) {
                float[] minPack = heap.deleteMin();
                Parser.writeRecord(output, (int) minPack[0], minPack[1], true);
                
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
                    if (blockNum * 512 * 8 > length) {
                        writeHeap(heap, output);
                        break;
                    }
                }
            }

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
    
    private void writeHeap(Heap heap, String output) {
        for (int i = 0; i < heap.toArray().length; i++) {
            Parser.writeRecord(output, heap.toArray()[i], 
                    heap.toArrayF()[i], true);
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
