
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
	public Heap(Record[] heapArray) {
		//array = heapArray;
		arraySize = 512;
		//arrayF = arrayFloat;
	}

    public void insert(Record next) {
        // TODO Auto-generated method stub
        
    }

	public boolean insert(int data) {
		// if this then you need to make heap bigger
		if (arraySize == array.length) {
			// increase size of array
			int[] newArray = new int[array.length * 2];
			for (int i = 0; i < array.length; i++) {
				newArray[i] = array[i];
			}
			newArray[array.length] = data;
			array = newArray;
			sift();
			arraySize++;
			return true;
		} // dont need to will always be 512
		else {
			array[arraySize] = data;
			arraySize++;
			sift();
			return true;
		}
	}

	private void sift() {

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
		array[x] = array[y];
		array[y] = temp;
	}

    public Record deleteMin() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    public Record[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }
}
