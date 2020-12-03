package com.explosion204.battleship

import com.explosion204.battleship.Constants.Companion.MATRIX_FREE_CELL
import java.lang.IndexOutOfBoundsException

class Matrix(rowsCount: Int, rowCapacity: Int) {
    private var innerMatrix = Array(rowsCount) { ByteArray(rowCapacity) { MATRIX_FREE_CELL } }

    operator fun get(i: Int, j: Int) = innerMatrix[i][j]
    operator fun set(i: Int, j: Int, value: Byte) {
        innerMatrix[i][j] = value
    }

    constructor(rowsCount: Int, rowCapacity: Int, array: Array<ByteArray>) : this(
        rowsCount,
        rowCapacity
    ) {
        innerMatrix = array
    }

    fun rowsCount(): Int {
        return innerMatrix.size
    }

    fun rowCapacity(): Int {
        return try {
            innerMatrix[0].size
        } catch (e: IndexOutOfBoundsException) {
            0
        }
    }
}