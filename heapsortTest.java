import student.TestCase;

import java.nio.ByteBuffer;

/**
 * tests the program
 * 
 * @author Drew Bond <dbond07>
 *         Nate Axt <nate6>
 * @version 11.10.2017
 */
public class heapsortTest extends TestCase
{

    public void testProgram()
    {
        String[] args = {"1024Blocks.bin", "stat.txt"};
        heapsort.main(args);
        int k = 0;
        //for (int i = 0; i < 1; i++) {
            System.out.println("/---------------/: " + k);
            ByteBuffer b = Parser.readBlock(k, "1024Blocks.bin");
            while (b.hasRemaining())
            {
                float f = b.getFloat();
                System.out.println(b.getInt() + " " + f);
                //b.getInt();
                assertNotNull(f);
            }
            //k++;
        //}
        
    }

}
