import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Parser {
    /**
     * reads in a block based on the block number
     * @param blockNumber is the block number
     * @return a bytebuffer containing that block
     * @throws IOException 
     */
    public static ByteBuffer readBlock(int blockNumber, String fileLocation)
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
        try
        {
            bIS.read(bytes, blockNumber * 512 * 8 * 8, 512 * 8 * 8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(bytes);
    }
    /**
     * reads in a block based on the block number
     * @param blockPosition is the block position
     * @return a bytebuffer containing that block
     * @throws IOException 
     */
    public static ByteBuffer readRuns(int blockPosition, String fileLocation, int length)
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
        
        byte[] bytes = new byte[length];
        try
        {
            bIS.read(bytes, blockPosition, length);
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
    @SuppressWarnings("resource")
    public static void writeToFile(String fileName, ByteBuffer b, 
            boolean append)
    {
        File file = new File(fileName);
        FileChannel wChannel = null;
        try
        {
            wChannel = new FileOutputStream(file, append).getChannel();
            wChannel.write(b);
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
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putInt(i);
        b.putFloat(f);
        Parser.writeToFile(fileName, b, append);
    }
}
