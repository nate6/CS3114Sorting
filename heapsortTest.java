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
        String[] args = {"8Blocks.bin", "stat.txt"};
        heapsort.main(args);
        int k = 0;
        //for (int i = 0; i < 1; i++) {
            System.out.println("/---------------/: " + k);
            ByteBuffer b = Parser.readBlock(k, "8Blocks.bin");
            while (b.hasRemaining())
            {
                System.out.println(b.getInt() + " " + b.getFloat());
                //b.getInt();
                //assertNotNull(b.getFloat());
            }
            //k++;
        //}
        
    }

}
