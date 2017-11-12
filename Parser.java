import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Parses the bin file and outputs files
 * 
 * @author Drew Bond <dbond07>
 *         Nate Axt <nate6>
 * @version 11.10.2017
 */
public class Parser {
    private static BufferedInputStream bIS;
    
    /**
     * Sets the buffer stream for our file.
     * @param fileLocation of our binary file
     */
    public static void setBIS(String fileLocation) {
        FileInputStream inFile = null;
        try
        {
            inFile = new FileInputStream(fileLocation);
        } 
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        bIS = new BufferedInputStream(inFile);
        inFile.close();
    }
    
    /**
     * reads in a block based on the block number
     * @return a bytebuffer containing that block
     */
    public static ByteBuffer readBlock()
    {        
        byte[] bytes = new byte[512 * 8 * 8];
        try
        {
            bIS.read(bytes, 0, 512 * 8 * 8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(bytes);
    }
    
    /**
     * reads in a block based on the block number
     * @param length of byte array
     * @return a bytebuffer containing that block
     */
    public static ByteBuffer readRuns(int length)
    {        
        byte[] bytes = new byte[length];
        try
        {
            bIS.read(bytes, 0, length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(bytes);
    }
    
    /**
     * writes a bytebuffer to a given file
     * @param fileName is the name of the file
     * @param b is the bytebuffer needed to be wrote
     * @param append if it will append or nots
     */
    public static void writeToFile(String fileName, ByteBuffer b, 
            boolean append)
    {
        File file = new File(fileName);
        FileChannel wChannel = null;
        try
        {
            FileOutputStream fstream = new FileOutputStream(file, append);
            wChannel = fstream.getChannel();
            wChannel.write(b);
            b.clear();
            fstream.close();
            wChannel.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * outputs a record to a given file
     * @param fileName is the given file
     * @param i is the id value
     * @param f is the key value
     * @param append is if it is appending or not
     */
    public static void writeRecord(String fileName, int i, float f, 
            boolean append)
    {
        FileOutputStream output = null;
        try
        {
            output = new FileOutputStream(fileName, append);
        }
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putInt(i);
        b.putFloat(f);
        byte[] outBytes = b.array();
        b.clear();
        try
        {
            output.write(outBytes);
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the length of the given file
     * @param file to check
     * @return byte length of that file
     */
    public static int getLength(String file) {
        File f = new File(file);
        return (int) f.length() / 8;
    }
}

