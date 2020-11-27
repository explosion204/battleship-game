package com.explosion204.battleship

class Matrix(private val rowsCount: Int, private val rowCapacity: Int) {
    private val innerMatrix = Array(rowsCount) { BooleanArray(rowCapacity) { false } }

    fun generate() {
        MatrixGenerator.generate(innerMatrix, rowsCount, rowCapacity)
    }
}