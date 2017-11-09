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
    private LinkedList<Integer[]> id_list;
    private LinkedList<Float[]> key_list;
    private int[] ids;
    private float[] keys;
    private final int MAX_RECORDS = 512;
    private long time;
    private String file;

    /**
     * sets up the class with a specific file
     * @param file is the file being red from
     * @throws IOException 
     */
    public DrBarnettesMagicalSortingFactory(String file) throws IOException {
        id_list = null;
        this.file = file;
        //parse(file);
        run();
        System.out.println(file + " " + time);
    }
    /**
     * starts running the sorting process and stores
     *  the beginning and end time
     * @throws IOException 
     */
    public void run() throws IOException
    {
        if (id_list != null && id_list.size() > 0)
        {
            startTimer();
            replacementSelection(id_list.get(0), key_list.get(0), 1);
            stopTimer();
        }
    }
    /**
     * parses the file
     * @param file is the string form of the file
     * @throws IOException 
     */
    private void parse(String file) throws IOException
    {
        Scanner sc = new Scanner(file);
        Parser ps = new Parser(file);
        ps.parse(sc);
        ids = ps.getID();
        keys = ps.getKey();
        LinkedList<Integer[]> ids = ps.getIDs();
        LinkedList<Float[]> keys = ps.getKeys();
        if (ids.size() == 0 || keys.size() == 0) {
            System.out.println("No records found in file.");
            System.exit(1);
        }
        this.id_list = ids;
        this.key_list = keys;
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
    private Integer[] replacementSelection(Integer[] ids, Float[] keys, int blockIdx) throws IOException
    {    
        Heap heap = new Heap(new int[512 * 8], new float[512 * 8]);
        if (id_list.size() == blockIdx)
        {
            //return heap.toArray();
        }
        Stack<Integer> records = new Stack<Integer>();
        records.addAll(Arrays.asList(id_list.get(blockIdx)));
        
        
        LinkedList<Integer> outBuffer = new LinkedList<Integer>();
        LinkedList<Float> outKeyBuffer = new LinkedList<Float>();
        int[] list = new int[MAX_RECORDS];
        int idx = 0;
        while (!heap.isEmpty())
        {
            float[] minPack = heap.deleteMin();
            int min = (int) minPack[0];
            outBuffer.add(min);
            if (records.size() != 0) {
                int next = records.pop();
                if (next >= outBuffer.getLast())
                {
                    heap.insert(next, 0.0f);
                }
                else
                {
                    list[idx] = next;
                    idx++;
                }
            }
            Integer[] k = (Integer[]) outBuffer.toArray();
            //heap = new Heap(, (float[]) outKeyBuffer.toArray());
            outBuffer = new LinkedList<Integer>();
            //this will will make the temp file and have it delete when the
                //program closes, *write to this for runs
            File temp = File.createTempFile("runs", ".bin");
            temp.deleteOnExit();
        }

        return replacementSelection((Integer[]) outBuffer.toArray(),
                (Float[]) outKeyBuffer.toArray(), blockIdx + 1);
    }
    /**
     * starts the timer
     */
    private void startTimer()
    {
        time = System.currentTimeMillis();
    }
    /**
     * ends the timer and gives you the time between the run
     */
    private void stopTimer()
    {
        time = System.currentTimeMillis() - time;
    }
    /**
     * sorts the runs after the runs are placed in the file
     */
    private void sortRuns()
    {
        //pick k runs lets say 4
        //int[] runA = new int[length of run A]
        //int[] runB = new int[length of run B]
        //int[] runC = new int[length of run C]
        //int[] runD = new int[length of run D]
            //also have to do the float ones
        //make buffer
        int[] bufferInt = new int[512 * 8];
        float[] bufferFloat = new float[512 * 8];
        int bufferPosition = 0;
        int posA = 0;
        int posB = 0;
        int posC = 0;
        int posD = 0;
        while(bufferPosition < 512 * 8)
        {
        //find the smallest value of each and place it in buffer
            //if (runA[posA] is biggest)
                //bufferInt[bufferPosition] = runA[posA];
                //posA++
            //...
            bufferPosition++;
        }
        //write buffer to file
        
        
        
    }
}
