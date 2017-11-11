import java.nio.ByteBuffer;

import student.TestCase;
/**
 * tests the factory and parser
 * 
 * @author Drew Bond <dbond07>
 *         Nate Axt <nate6>
 * @version 11.10.2017
 */
public class DrBarnettesMagicalSortingFactoryTest extends TestCase
{

    public void testSorting()
    {
        DrBarnettesMagicalSortingFactory b = new 
                DrBarnettesMagicalSortingFactory("8Blocks.bin", "Stat.txt");
        
        ByteBuffer bB = Parser.readBlock(0, "8Blocks.bin");
        Heap h = b.heapify(bB);
        float[] f = h.toArrayF();
        for (int i = 0; i < 512 * 8 - 1; i++)
        {
            assertTrue(f[i] <= f[i+1]);
        }
        //so its sorting correctly then
        //tests writing a buffer to the file
        b.writeHeap(h, "out.bin", false);
        bB = Parser.readBlock(0, "out.bin");
        while(bB.hasRemaining())
        {
            assertNotNull(bB.getInt());
        }
    }
    /**
     * tests the parser methods
     */
    public void testOutput()
    {
        ByteBuffer bB;
        Parser.writeRecord("test.bin", 0, 0, false);
        for (int i = 1; i < 50; i++)
        {
            Parser.writeRecord("test.bin", i, 4, true);
        }
        bB = Parser.readBlock(0, "test.bin");
        int i = 0;
        while(bB.hasRemaining() && i < 50)
        {
            assertEquals(i, bB.getInt());
            bB.getFloat();
            i++;
        }
        ByteBuffer bB2 = Parser.readRuns(0, "8Blocks.bin", 
                Parser.getLength("8Blocks.bin"));
        while (bB2.hasRemaining())
        {
            assertNotNull(bB2.getInt());
        }
        
    }

}
