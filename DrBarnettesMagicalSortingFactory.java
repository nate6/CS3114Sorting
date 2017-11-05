import java.util.LinkedList;
import java.util.Scanner;

public class DrBarnettesMagicalSortingFactory {
    private LinkedList<Record[]> blocks;
    private final int MAX_RECORDS = 512;
    private long time;

    public DrBarnettesMagicalSortingFactory(String file) {
        blocks = null;
        parse(file);
        run();
        System.out.println(file + " " + time);
    }
    
    public void run() {
        if (blocks != null && blocks.size() > 0) {
            startTimer();
            replacementSelection(blocks.get(0), 1);
            stopTimer();
        }
    }

    private void parse(String file) {
        Scanner sc = new Scanner(file);
        LinkedList<Record[]> blocks = Parser.parse(sc);
        if (blocks.size() == 0) {
            System.out.println("No records found in file.");
            System.exit(1);
        }
        this.blocks = blocks;
        sc.close();
    }

    private Record[] replacementSelection(Record[] block, int blockIdx) {
        
        Heap heap = new Heap(block);
        if (blocks.size() == blockIdx) {
            return heap.toArray();
        }
        
        Record[] records = blocks.get(blockIdx);
        
        
        LinkedList<Record> outBuffer = new LinkedList<Record>();
        Record[] list = new Record[MAX_RECORDS];
        int idx = 0;
        do {
            while (!heap.isEmpty()) {
                Record min = heap.deleteMin();
                outBuffer.add(min);
                if (records.length != 0) {
                    Record next = records[0];
                    // TODO shift records array by 1
                    if (next.key() >= outBuffer.getLast().key()) {
                        heap.insert(next);
                    }
                    else {
                        list[idx] = next;
                        idx++;
                    }
                }
            }
        } while (!heap.isEmpty());

        return replacementSelection((Record[]) outBuffer.toArray(),
                blockIdx + 1);
    }
    
    private void startTimer() {
        time = System.currentTimeMillis();
    }
    
    private void stopTimer() {
        time = System.currentTimeMillis() - time;
    }
}
