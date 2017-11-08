import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class DrBarnettesMagicalSortingFactory {
    private LinkedList<Integer[]> id_list;
    private LinkedList<Float[]> key_list;
    private final int MAX_RECORDS = 512;
    private long time;

    public DrBarnettesMagicalSortingFactory(String file) {
        id_list = null;
        parse(file);
        run();
        System.out.println(file + " " + time);
    }
    
    public void run() {
        if (id_list != null && id_list.size() > 0) {
            startTimer();
            replacementSelection(id_list.get(0), key_list.get(0), 1);
            stopTimer();
        }
    }

    private void parse(String file) {
        Scanner sc = new Scanner(file);
        Parser ps = new Parser();
        ps.parse(sc);
        
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

    private Integer[] replacementSelection(Integer[] ids, Float[] keys, int blockIdx) {
        
        Heap heap = new Heap(new int[10], new float[10]);
        if (id_list.size() == blockIdx) {
            //return heap.toArray();
        }
        
        Stack<Integer> records = new Stack<Integer>();
        records.addAll(Arrays.asList(id_list.get(blockIdx)));
        
        
        LinkedList<Integer> outBuffer = new LinkedList<Integer>();
        LinkedList<Float> outKeyBuffer = new LinkedList<Float>();
        int[] list = new int[MAX_RECORDS];
        int idx = 0;
        while (!heap.isEmpty()) {
            float[] minPack = heap.deleteMin();
            int min = (int) minPack[0];
            outBuffer.add(min);
            if (records.size() != 0) {
                int next = records.pop();
                if (next >= outBuffer.getLast()) {
                    heap.insert(next, 0.0f);
                }
                else {
                    list[idx] = next;
                    idx++;
                }
            }
            Integer[] k = (Integer[]) outBuffer.toArray();
            //heap = new Heap(, (float[]) outKeyBuffer.toArray());
            outBuffer = new LinkedList<Integer>();
        }

        return replacementSelection((Integer[]) outBuffer.toArray(),
                (Float[]) outKeyBuffer.toArray(), blockIdx + 1);
    }
    
    private void startTimer() {
        time = System.currentTimeMillis();
    }
    
    private void stopTimer() {
        time = System.currentTimeMillis() - time;
    }
}
