package com.explosion204.battleship

import java.lang.IndexOutOfBoundsException

class Matrix(rowsCount: Int, rowCapacity: Int) {
    private var innerMatrix = Array(rowsCount) { BooleanArray(rowCapacity) { false } }

    operator fun get(i: Int, j: Int) = innerMatrix[i][j]

    constructor(rowsCount: Int, rowCapacity: Int, array: Array<BooleanArray>) : this(rowsCount, rowCapacity) {
        innerMatrix = array
    }

    fun rowsCount(): Int {
        return innerMatrix.size
    }

    fun rowCapacity(): Int {
        return try {
            innerMatrix[0].size
        }
        catch (e: IndexOutOfBoundsException) {
            0
        }
    }
}