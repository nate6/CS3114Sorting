import java.util.LinkedList;
import java.util.Scanner;

public class Parser {
    public LinkedList<Integer[]> id_list;
    public LinkedList<Float[]> key_list;

    public void parse(Scanner sc) {
        id_list = new LinkedList<Integer[]>();
        key_list = new LinkedList<Float[]>();
        
        
        Integer[] ids = new Integer[512];
        Float[] keys = new Float[512];
        
        // TODO parse arrays
        
        
        id_list.add(ids);
        key_list.add(keys);
    }
    
    public LinkedList<Integer[]> getIDs() {
        return id_list;
    }
    
    public LinkedList<Float[]> getKeys() {
        return key_list;
    }

}
