package common

fun IntArray.swap(i: Int, j: Int) {
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}

fun BooleanArray.swap(i: Int, j: Int) {
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}

/**
 * Iterator that generates all permutations of a set of elements.
 * The implementation is based on the Steinhaus–Johnson–Trotter algorithm.
 */
class PermutationIterator<T>(private val original: Array<T>) : AbstractIterator<Array<T>>() {
    private var nextReturn: Array<T>? = original.copyOf()
    private var returnArray: Array<T> = original.copyOf()

    /**
     * Indices of the elements in the original array.
     */
    private val indices = IntArray(original.size) { it }

    /**
     * Directions of each index. True means the index is pointing to the right, false means it's pointing to the left.
     */
    private val directions = BooleanArray(original.size) { false }

    override fun computeNext() {
        if (nextReturn == null) {
            done()
            return
        }

        val oldReturn = returnArray
        returnArray = nextReturn!!
        nextReturn = oldReturn


        var indexOfLargestMobileIndex = -1
        var largestKey = -1
        for (i in indices.indices) {
            if (directions[i] && i < indices.size - 1 && indices[i] > indices[i + 1] ||
                !directions[i] && i > 0 && indices[i] > indices[i - 1]
            ) {
                if (indices[i] > largestKey) {
                    largestKey = indices[i]
                    indexOfLargestMobileIndex = i
                }
            }
        }

        if (indexOfLargestMobileIndex == -1) {
            setNext(nextReturn!!)
            nextReturn = null
            return
        }

        // Swap the largest mobile index with the index it's pointing to
        val swapIndex = if (directions[indexOfLargestMobileIndex]) {
            indexOfLargestMobileIndex + 1
        } else {
            indexOfLargestMobileIndex - 1
        }

        indices.swap(indexOfLargestMobileIndex, swapIndex)
        directions.swap(indexOfLargestMobileIndex, swapIndex)

        // Reverse the direction of all indices larger than the largest mobile index
        for (i in indices.indices) {
            if (indices[i] > largestKey) {
                directions[i] = !directions[i]
            }
        }

        // Generate the next permutation
        setNext(nextReturn!!)

        for (i in indices.indices) {
            // The nextReturn array is reused and never null
            (nextReturn as Array<T>)[i] = original[indices[i]]
        }
    }

}


inline fun <reified T> Set<T>.permutations(): Iterator<Array<T>> {
    return PermutationIterator(toTypedArray())
}
