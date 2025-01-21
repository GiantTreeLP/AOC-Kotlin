package common

/**
 * Iterator that generates all permutations of a set of elements.
 * The implementation is based on the Steinhaus–Johnson–Trotter algorithm.
 */
class PermutationIterator<T>(private val original: Array<T>) : Iterator<Array<T>> {
    private val indexPermutation = IndexPermutationIterator(original.size)

    private var permutation: Array<T> = original.copyOf()

    override fun next(): Array<T> {
        val indices = this.indexPermutation.next()

        // Generate the next permutation
        for (i in 0 until indices.size) {
            this.permutation[i] = this.original[indices[i]]
        }

        return this.permutation
    }

    override fun hasNext(): Boolean {
        return this.indexPermutation.hasNext()
    }

}


inline fun <reified T> Set<T>.permutations(): Iterator<Array<T>> {
    return PermutationIterator(toTypedArray())
}
