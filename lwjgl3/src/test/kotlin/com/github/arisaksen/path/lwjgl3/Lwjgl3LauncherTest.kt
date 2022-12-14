@file:JvmName("Lwjgl3LauncherTest")

package com.github.arisaksen.path.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.arisaksen.path.Path

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(Path("Day12_test"), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Path")
        setWindowedMode(8 * 8, 5 * 8)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
/**
 * Should be 29 distance instead of 31. +2 steps was added after reading of startCell was added to change :

}.also {
aoc12Map.mapIndexed { y, row ->
row.mapIndexed { x, char ->
if (char == 'S') startCell = CellData(x, y)
if (char == 'E') endCell = CellData(x, y)

 */
