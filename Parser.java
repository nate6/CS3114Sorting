import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Parser {
    public LinkedList<Integer[]> id_list;
    public LinkedList<Float[]> key_list;
    private int[] ids;
    private float[] keys;
    private ByteBuffer bB;
    /**
     * sets up the parser
     * @param sc
     */
    public void parse(Scanner sc)
    {
        id_list = new LinkedList<Integer[]>();
        key_list = new LinkedList<Float[]>();
        
        
        Integer[] ids = new Integer[512 * 8];
        Float[] keys = new Float[512 * 8];
        
        // TODO parse arrays
        
        
        id_list.add(ids);
        key_list.add(keys);
    }
    /**
     * sets up the parser with a given file
     * @param fileLocation is the location of the file
     * @throws IOException 
     */
    public Parser(String fileLocation) throws IOException
    {
        FileInputStream inFile = null;
        try
        {
            inFile = new FileInputStream(fileLocation);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        
        FileChannel inChannel = inFile.getChannel();
        int length = ((int) new File(fileLocation).length()) / 8;
        bB = ByteBuffer.allocate(length * 8);
        ids = new int[length];
        keys = new float[length];
        while (inChannel.read(bB) != -1)
        {
            ((ByteBuffer) (bB.flip())).asIntBuffer().get(ids);
            ((ByteBuffer) (bB.flip())).asFloatBuffer().get(keys);
            bB.clear();
        }
        inFile.close();

        id_list.add(Arrays.stream(ids).boxed().toArray( Integer[]::new ));
        //key_list.add(Arrays.stream(keys).boxed().toArray( Float[]::new ));********where i cant get it
        //can do this but its waaaaaaayyyyy inefficient
        Float[] keyFloat = new Float[keys.length];
        for (int i = 0; i < keys.length; i++)
        {
            keyFloat[i] = new Float(keys[i]);
        }
        key_list.add(keyFloat);
    }
    /**
     * gives you the list of ids
     * @return the array of ids
     */
    public int[] getID()
    {
        return ids;
    }
    /**
     * gives you the array of keys
     * @return the array of keys
     */
    public float[] getKey()
    {
        return keys;
    }
    public LinkedList<Integer[]> getIDs() {
        return id_list;
    }
    
    public LinkedList<Float[]> getKeys() {
        return key_list;
    }
    /**
     * reads in a block based on the block number
     * @param blockNumber is the block number
     * @return a bytebuffer containing that block
     * @throws IOException 
     */
    public ByteBuffer readBlock(int blockNumber, String fileLocation)
            throws IOException
    {
        FileInputStream inFile = null;
        try
        {
            inFile = new FileInputStream(fileLocation);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        BufferedInputStream bIS = new BufferedInputStream(inFile);
        
        byte[] bytes = new byte[512 * 8 * 8];
        bIS.read(bytes, blockNumber * 512 * 8 * 8, 512 * 8 * 8);
        return ByteBuffer.wrap(bytes);
    }
}
