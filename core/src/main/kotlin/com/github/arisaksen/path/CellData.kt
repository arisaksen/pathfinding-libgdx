package com.github.arisaksen.path

data class CellData(val x: Int, val y: Int, val pathLength: Int = 0){

    override fun equals(other: Any?): Boolean {
        if (other is CellData) return this.x == other.x && this.y == other.y
        return super.equals(other)
    }
}
