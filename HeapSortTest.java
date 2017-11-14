import student.TestCase;

import java.nio.ByteBuffer;

/**
 * tests the program
 * 
 * @author Drew Bond <dbond07>
 *         Nate Axt <nate6>
 * @version 11.10.2017
 */
public class HeapSortTest extends TestCase
{

    /**
     * Test prog
     */
    public void testProgram()
    {
        String[] args = {"16Blocks.bin", "stat.txt"};
        heapsort.main(args);
        int k = 0;
        ByteBuffer b = Parser.readBlock(k, "16Blocks.bin");
        while (b.hasRemaining())
        {
            float f = b.getFloat();
            //System.out.println(b.getInt() + " " + f);
            b.getInt();
            assertNotNull(f);
        }
            //k++;
        //}
        
        String[] argss = {"8Blocks.bin", "stat.txt"};
        heapsort.main(argss);
        k = 0;
        b = Parser.readBlock(k, "8Blocks.bin");
        while (b.hasRemaining())
        {
            float f = b.getFloat();
            //System.out.println(b.getInt() + " " + f);
            b.getInt();
            assertNotNull(f);
        }
        
    }

}
