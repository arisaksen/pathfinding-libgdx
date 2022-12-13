@file:JvmName("Lwjgl3Launcher")

package com.github.arisaksen.path.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.arisaksen.path.Path

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(Path(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("GladiatorSandArena")
        setWindowedMode(1220, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
