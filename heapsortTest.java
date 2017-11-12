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

    /**
     * Tests the entire program
     */
    public void testProgram()
    {
        String[] args = {"8Blocks.bin", "stat.txt"};
        heapsort.main(args);
        
        Parser.setBIS("8Blocks.bin");
        ByteBuffer b = Parser.readBlock();
        while (b.hasRemaining()) {
            float f = b.getFloat();
            b.getInt();
            assertNotNull(f);
        }
        b.clear();
    }

}
