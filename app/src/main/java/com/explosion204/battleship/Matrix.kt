package com.explosion204.battleship

import java.lang.IndexOutOfBoundsException

class Matrix(private val rowsCount: Int, private val rowCapacity: Int) {
    private val innerMatrix = Array(rowsCount) { BooleanArray(rowCapacity) { false } }

    fun generate() {
        MatrixGenerator.generate(innerMatrix, rowsCount, rowCapacity)
    }

    operator fun get(i: Int, j: Int) = innerMatrix[i][j]

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