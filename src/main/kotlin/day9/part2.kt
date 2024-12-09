package day9

import common.readResource

private object Part2 {
    sealed class Cell {
        data class FreeSpace(val freeSpaceSize: Int) : Cell()

        data class File(val fileId: Long, val fileSize: Int) : Cell()
    }
}

fun main() {
    val inputDiskMap = readResource("day9/input").trim()

    val denseDiskMap = inputDiskMap.mapIndexed { index, size ->
        if (index % 2 == 0) {
            listOf(Part2.Cell.File((index / 2).toLong(), size.digitToInt()))
        } else {
            listOf(Part2.Cell.FreeSpace(size.digitToInt()))
        }
    }.flatten().toMutableList()

    // Move files from the back to free space in the front
    for (i in denseDiskMap.indices.reversed()) {
        val file = denseDiskMap[i]
        if (file is Part2.Cell.File) {

            // Find a free space cell that can hold the file
            for (j in 0 until i) {
                val cell = denseDiskMap[j]
                if (cell is Part2.Cell.FreeSpace && cell.freeSpaceSize >= file.fileSize) {
                    denseDiskMap[j] = file

                    // Update free space cell
                    // If there is still free space left, add a new free space cell
                    // If the free space cell is empty, leave it out
                    val newFreeSpace = Part2.Cell.FreeSpace(cell.freeSpaceSize - file.fileSize)
                    if (newFreeSpace.freeSpaceSize > 0) {
                        denseDiskMap.add(j + 1, newFreeSpace)
                        denseDiskMap[i + 1] = Part2.Cell.FreeSpace(file.fileSize)
                    } else {
                        // Update the space that was occupied by the file
                        denseDiskMap[i] = Part2.Cell.FreeSpace(file.fileSize)
                    }
                    break
                }
            }

            // Merge free space cells
            for (k in denseDiskMap.lastIndex downTo 1) {
                // Get the two cells to merge
                val cellToMerge = denseDiskMap[k]
                val previousCell = denseDiskMap[k - 1]

                if (cellToMerge is Part2.Cell.FreeSpace &&
                    previousCell is Part2.Cell.FreeSpace
                ) {
                    denseDiskMap[k - 1] =
                        Part2.Cell.FreeSpace(previousCell.freeSpaceSize + cellToMerge.freeSpaceSize)
                    denseDiskMap.removeAt(k)
                }
            }
        }
    }

    // Calculate checksum
    var checksum = 0L
    var currentBlock = 0
    for (cell in denseDiskMap) {
        when (cell) {
            is Part2.Cell.FreeSpace -> {
                currentBlock += cell.freeSpaceSize
            }

            is Part2.Cell.File -> {
                repeat(cell.fileSize) { fileBlock ->
                    checksum += (currentBlock + fileBlock) * cell.fileId
                }
                currentBlock += cell.fileSize
            }
        }
    }

    println(checksum)
}
