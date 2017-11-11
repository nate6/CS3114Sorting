
/**
 * Creates a heap for int float array pairs.
 * 
 * @author Drew Bond <dbond07> Nate Axt <nate6>
 * @version 11.10.2017
 */
public class Heap {
    private int[] array;
    private float[] arrayF;
    private int arraySize;

    /**
     * sets up the heapsorter with a given array (unsorted) each array should be
     * holding 8 blocks
     * 
     * @param heapArray
     *            unsorted array
     * @param arrayFloat
     *            unsorted array paired to heapArray
     */
    public Heap(int[] heapArray, float[] arrayFloat)
    {
        array = heapArray;
        arraySize = 512 * 8;
        arrayF = arrayFloat;
    }

    /**
     * inserts into the heap
     * 
     * @param data
     *            is the int data
     * @param dataF
     *            is the float data
     * @return if it was successfull
     */
    public boolean insert(int data, float dataF)
    {
        if (arraySize >= 512 * 8)
        {
            return false;
        }
        arraySize++;
        array[arraySize - 1] = data;
        arrayF[arraySize - 1] = dataF;
        sort();
        return true;
    }

    /**
     * Sorts the heap by min
     */
    public void sort()
    {
        for (int i = arraySize / 2 - 1; i >= 0; i--)
        {
            sift(arraySize, i);
        }
        for (int j = arraySize - 1; j >= 0; j--)
        {
            switchPositions(0, j);
            sift(j, 0);
        }
    }

    /**
     * sorts the heap
     * 
     * @param n
     *            is the size of the heap
     * @param i
     *            is the root position
     */
    private void sift(int n, int i)
    {
        // go to last element
        int big = i;
        int l = getLeftChild(i);
        int r = getRightChild(i);

        if (l < n && arrayF[l] > arrayF[big])
        {
            big = l;
        }
        if (r < n && arrayF[r] > arrayF[big])
        {
            big = r;
        }
        if (big != i)
        {
            switchPositions(big, i);
            sift(n, big);
        }
    }

    /**
     * gives you the position of the left child
     * 
     * @param position
     *            is root position
     * @return left position
     */
    private int getLeftChild(int position)
    {
        return position * 2 + 1;
    }

    /**
     * gives you the position of the right child
     * 
     * @param position
     *            is root position
     * @return right position
     */
    private int getRightChild(int position)
    {
        return position * 2 + 2;
    }

    /**
     * switches the positions of array[x] and array[y]
     * 
     * @param x
     *            first position
     * @param y
     *            2nd position
     */
    private void switchPositions(int x, int y)
    {
        int temp = array[x];
        float tempF = arrayF[x];
        array[x] = array[y];
        array[y] = temp;
        arrayF[x] = arrayF[y];
        arrayF[y] = tempF;
    }

    /**
     * deletes the min value and returns it to you
     * 
     * @return a float array with 0 being the int, 1 being the float
     */
    public float[] deleteMin()
    {
        float[] r = { array[0], arrayF[0] };
        switchPositions(0, arraySize - 1);
        arraySize--;
        sift(arraySize, 0);
        return r;
    }

    /**
     * tells you if its empty or not
     * 
     * @return if its empty
     */
    public boolean isEmpty()
    {
        return array[0] == 0;
    }

    /**
     * gives you the int array
     * 
     * @return the int array
     */
    public int[] toArray()
    {
        return array;
    }

    /**
     * gives you the float array
     * 
     * @return the float array
     */
    public float[] toArrayF()
    {
        return arrayF;
    }
}
