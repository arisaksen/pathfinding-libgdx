package com.github.arisaksen.path

data class CellData(val x: Int, val y: Int)

data class Search(val cellData: CellData, val distance: Int = 0)
