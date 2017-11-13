import student.TestCase;

import java.util.Arrays;
import java.util.Random;

/**
 * tests the heap
 * 
 * @author Drew Bond <dbond07>
 *         Nate Axt <nate6>
 * @version 11.10.2017
 */
public class HeapTest extends TestCase
{
    /**
     * tests the heapsorting with random ints and floats
     */
    public void testHeap()
    {
        int[] array = new int[512 * 8];
        float[] arrayF = new float[512 * 8];
        Random rand = new Random();
        for (int i = 0; i < 512 * 8; i++)
        {
            array[i] = rand.nextInt();
            arrayF[i] = rand.nextFloat();
        }
        Heap h = new Heap(array, arrayF);
        h.sort();
        
        assertFalse(h.insert(1, 1));
        assertFalse(h.isEmpty());
        
        float mLast = 0.0f;
        while(!h.isEmpty())
        {
            float[] m = h.deleteMin();
            assertTrue(m[1] >= mLast);
            mLast = m[1];
        }
        
        int i = 0;
        while(!h.isEmpty())
        {
            h.deleteMin();
            i++;
        }
        assertTrue(h.isEmpty());
        assertEquals(0, i);
        assertTrue(h.insert(5, 9));
    }

}
