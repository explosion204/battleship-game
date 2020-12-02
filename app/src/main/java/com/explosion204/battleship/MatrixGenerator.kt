package com.explosion204.battleship

class MatrixGenerator {
    companion object {
        private const val DIRECTION_RIGHT = 1
        private const val DIRECTION_DOWN = 2
        private const val DIRECTION_LEFT = 3

        fun generate(rowsCount: Int, rowCapacity: Int): Matrix {
            while (true) {
                val newMatrix = Array(rowsCount) { BooleanArray(rowCapacity) { false } }
                var result = true

                result = placeShip(newMatrix, rowsCount, rowCapacity, 4, 1)
                if (!result) continue

                result = placeShip(newMatrix, rowsCount, rowCapacity, 3, 2)
                if (!result) continue

                result = placeShip(newMatrix, rowsCount, rowCapacity, 2, 3)
                if (!result) continue

                result = placeShip(newMatrix, rowsCount, rowCapacity, 1, 4)
                if (!result) continue

                return Matrix(rowsCount, rowCapacity, newMatrix)
            }
        }

        fun placeShip(matrix: Array<BooleanArray>, rowsCount: Int, rowCapacity: Int, size: Int, count: Int): Boolean {
            var resultMatrix = deepCopy(matrix, rowsCount, rowCapacity)


            for (i in 0 until count) {
                var failuresAllowed = 10

                while (failuresAllowed > 0) {
                    val randRow = (0 until rowsCount).random()
                    val randPos = (0 until rowCapacity).random()
                    val randDirection = (1..4).random()
                    var free = true

                    if (randDirection == DIRECTION_RIGHT) {
                        for (k in 0 until size) {
                            free = checkArea(resultMatrix, randRow, randPos + k)

                            if (!free) break
                        }

                        if (free) {
                            try {
                                val buffer = deepCopy(resultMatrix, rowsCount, rowCapacity)
                                for (k in 0 until size) {
                                    buffer[randRow][randPos + k] = true
                                }
                                resultMatrix = deepCopy(buffer, rowsCount, rowCapacity)
                                break
                            } catch (e: Exception) {}
                        }

                        failuresAllowed--;
                        continue
                    }

                    else if (randDirection == DIRECTION_DOWN) {
                        for (k in 0 until size) {
                            free = checkArea(resultMatrix, randRow + k, randPos)

                            if (!free) break
                        }

                        if (free) {
                            try {
                                val buffer = deepCopy(resultMatrix, rowsCount, rowCapacity)
                                for (k in 0 until size) {
                                    buffer[randRow + k][randPos] = true
                                }
                                resultMatrix = deepCopy(buffer, rowsCount, rowCapacity)
                                break
                            } catch(e: Exception) {}
                        }

                        failuresAllowed--;
                        continue
                    }

                    else if (randDirection == DIRECTION_LEFT) {
                        for (k in 0 until size) {
                            free = checkArea(resultMatrix, randRow, randPos - k)

                            if (!free) break
                        }

                        if (free) {
                            try {
                                val buffer = deepCopy(resultMatrix, rowsCount, rowCapacity)
                                for (k in 0 until size) {
                                    buffer[randRow][randPos - k] = true
                                }
                                resultMatrix = deepCopy(buffer, rowsCount, rowCapacity)
                                break
                            } catch(e: Exception) {}
                        }

                        failuresAllowed--;
                        continue
                    }

                    else { // DIRECTION_UP
                        for (k in 0 until size) {
                            free = checkArea(resultMatrix, randRow - k, randPos)

                            if (!free) break
                        }

                        if (free) {
                            try {
                                val buffer = deepCopy(resultMatrix, rowsCount, rowCapacity)
                                for (k in 0 until size) {
                                    buffer[randRow - k][randPos] = true
                                }
                                resultMatrix = deepCopy(buffer, rowsCount, rowCapacity)
                                break
                            } catch(e: Exception) {}
                        }

                        failuresAllowed--;
                        continue
                    }
                }

                if (failuresAllowed < 0) return false;
            }

            for (i in 0 until rowsCount) {
                for (j in 0 until rowCapacity) {
                    matrix[i][j] = resultMatrix[i][j]
                }
            }

            return true;
        }

        private fun checkArea(matrix: Array<BooleanArray>, row: Int, pos: Int): Boolean {
            try {
                if (matrix[row][pos]) return false
            }
            catch (e: Exception) {}

            try {
                if (matrix[row - 1][pos - 1]) return false
            }
            catch (e: Exception) {}

            try {
                if (matrix[row - 1][pos]) return false
            }
            catch (e: Exception) {}

            try {
                if (matrix[row - 1][pos + 1]) return false
            }
            catch (e: Exception) {}

            try {
                if (matrix[row][pos - 1]) return false
            }
            catch (e: Exception) {}

            try {
                if (matrix[row][pos + 1]) return false
            }
            catch (e: Exception) {}

            try {
                if (matrix[row + 1][pos - 1]) return false
            }
            catch (e: Exception) {}

            try {
                if (matrix[row + 1][pos]) return false
            }
            catch (e: Exception) {}

            try {
                if (matrix[row + 1][pos + 1]) return false
            }
            catch (e: Exception) {}

            return true
        }

        private fun deepCopy(original: Array<BooleanArray>, rowsCount: Int, rowCapacity: Int): Array<BooleanArray> {
            val copy = Array(rowsCount) { BooleanArray(rowCapacity) { false } }

            for (i in 0 until rowsCount) {
                for (j in 0 until rowCapacity) {
                    copy[i][j] = original[i][j]
                }
            }

            return copy
        }
    }
}