
public class Heap {
	int[] array;
	float[] arrayF;
	int arraySize;

	/**
	 * sets up the heapsorter with a given array (unsorted)
	 * 
	 * @param heapArray
	 *            unsorted array
	 */
	public Heap(int[] heapArray, float[] arrayF) {
		array = heapArray;
		arraySize = 512;
		arrayF = arrayF;
	}

	public boolean insert(int data) {
		array[arraySize] = data;
		arraySize++;
		sift();
		return true;
	}
	/**
	 * sorts the heap
	 */
	private void sift()
	{
	    //go to last element
	}

	/**
	 * gives you the position of the left child
	 * 
	 * @param position
	 *            is root position
	 * @return left position
	 */
	private int getLeftChild(int position) {
		return position * 2 + 1;
	}

	/**
	 * gives you the position of the right child
	 * 
	 * @param position
	 *            is root position
	 * @return right position
	 */
	private int getRightChild(int position) {
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
	private void switchPositions(int x, int y) {
		int temp = array[x];
		float tempF = arrayF[x];
		array[x] = array[y];
		array[y] = temp;
		arrayF[x] = arrayF[y];
        arrayF[y] = tempF;
	}

    public Integer deleteMin() {
        // TODO Auto-generated method stub
        //swaps the top of the heap with the
        return null;
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    public Integer[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }
}
