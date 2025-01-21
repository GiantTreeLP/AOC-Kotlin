package common

/**
 * Iterator that generates all permutations of integers from in the range `[0,size)`.
 * The implementation is based on the Steinhaus–Johnson–Trotter algorithm improved by Shimon Even.
 * See <a href="https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm#Even's_speedup">the Wikipedia article</a> for details.
 */
class IndexPermutationIterator(val size: Int) : Iterator<IntArray> {
    /**
     * Indices of the elements in the original array.
     */
    private val indices = IntArray(this.size) { it }

    /**
     * Directions of each index.
     * Initialized to -1 for all indices except the first one which is initialized to 0.
     */
    private val directions = IntArray(this.size) { -1 }

    init {
        this.directions[0] = 0
    }

    private var ended = false

    override fun next(): IntArray {
        var indexOfLargestMobileIndex = -1
        var largestMobileIndex = -1
        var directionOfLargestMobileIndex = 0
        val indices = this.indices
        val directions = this.directions

        for (i in 0 until this.size) {
            val index = indices[i]
            val direction = directions[i]
            if (index > largestMobileIndex && direction != 0) {
                largestMobileIndex = index
                indexOfLargestMobileIndex = i
                directionOfLargestMobileIndex = direction
            }
        }

        if (indexOfLargestMobileIndex == -1) {
            this.ended = true
            for (i in 0 until this.size) {
                indices[i] = i
            }
            return indices
        }

        // Find the index to swap with
        val swapIndex = indexOfLargestMobileIndex + directionOfLargestMobileIndex
        val nextIndex = swapIndex + directionOfLargestMobileIndex

        // If the largest mobile index is at the beginning or end of the array or the index it's pointing to is larger than the largest mobile index
        // then the direction of the largest mobile index is 0
        if (swapIndex == 0 || swapIndex == this.size - 1 || indices[nextIndex] > largestMobileIndex) {
            directions[indexOfLargestMobileIndex] = 0
        }

        // Swap the largest mobile index with the index it's pointing to
        indices.swap(swapIndex, indexOfLargestMobileIndex)
        directions.swap(swapIndex, indexOfLargestMobileIndex)

        // Update motion of all indices larger than the largest mobile index towards the largest mobile index
        for (i in 0 until swapIndex) {
            if (indices[i] > largestMobileIndex) {
                directions[i] = 1
            }
        }

        for (i in swapIndex + 1 until this.size) {
            if (indices[i] > largestMobileIndex) {
                directions[i] = -1
            }
        }

        return indices
    }

    override fun hasNext(): Boolean {
        return !this.ended
    }

}

@Suppress("NOTHING_TO_INLINE")
private inline fun IntArray.swap(i: Int, j: Int) {
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}
