import student.TestCase;
import java.util.Random;
/**
 * tests the 
 * @author Drew Bond dbond07, Nate Axt <>
 *
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
        for (int i = 0; i < 512*8; i++)
        {
            array[i] = rand.nextInt();
            arrayF[i] = rand.nextFloat();
        }
        Heap h = new Heap(array, arrayF);
        h.sort();
        float[] sorted = h.toArrayF();
        for (int i = 0; i < 512 * 8 - 1; i++)
        {
            assertTrue(sorted[i] <= sorted[i+1]);
        }
    }

}
