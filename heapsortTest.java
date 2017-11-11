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
        ByteBuffer b = Parser.readBlock(0, "8Blocks.bin");
        while (b.hasRemaining())
        {
            b.getInt();
            assertNotNull(b.getFloat());
        }
        
    }

}
