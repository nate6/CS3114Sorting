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

    /**
     * Tests the sorting
     */
    public void testSorting()
    {
        DrBarnettesMagicalSortingFactory b = new 
                DrBarnettesMagicalSortingFactory("8Blocks.bin", "Stat.txt");
        
        Parser.setBIS("8Blocks.bin");
        ByteBuffer bB = Parser.readBlock();
        Heap h = b.heapify(bB);
        float[] f = h.toArrayF();
        for (int i = 0; i < 512 * 8 - 1; i++)
        {
            assertTrue(f[i] <= f[i + 1]);
        }
        //so its sorting correctly then
        //tests writing a buffer to the file
        b.writeHeap(h, "test.bin", false);
        bB = Parser.readBlock();
        while (bB.hasRemaining())
        {
            assertNotNull(bB.getInt());
        }
        bB.clear();
    }
    /**
     * tests the parser methods
     */
    public void testOutput()
    {
        Parser.setBIS("test.bin");
        ByteBuffer bB;
        Parser.writeRecord("test.bin", 0, 0, false);
        for (int i = 1; i < 50; i++)
        {
            Parser.writeRecord("test.bin", i, 4, true);
        }
        bB = Parser.readBlock();
        int i = 0;
        while (bB.hasRemaining() && i < 50)
        {
            assertEquals(i, bB.getInt());
            bB.getFloat();
            i++;
        }
        bB.clear();
    }
    /**
     * Test inputs
     */
    public void testInput()
    {
        Parser.setBIS("16Blocks.bin");
        ByteBuffer bB = Parser.readBlock();
        int i = 0;
        while (bB.hasRemaining())
        {
            assertNotNull(bB.getInt());
            i++;
            bB.getFloat();
        }
        System.out.println(i);
        bB.clear();
    }

}
