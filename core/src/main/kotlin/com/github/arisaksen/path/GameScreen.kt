package com.github.arisaksen.path

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import java.io.File

class GameScreen : KtxScreen {
    private val aoc12Map: List<List<Char>> = File("assets", "Day12.txt").readLines().map { it.toList() }
//    private val aoc12Map: List<List<Char>> = File("assets", "Day12_test.txt").readLines().map { it.toList() }
    private val camera: OrthographicCamera = OrthographicCamera()
    private lateinit var tiledMap: TiledMap
    private lateinit var tiledMapRenderer: TiledMapRenderer

    override fun show() {
        val worldWidth = aoc12Map[0].size
        val worldHeight = aoc12Map.size

        camera.setToOrtho(false, worldWidth.toFloat(), worldHeight.toFloat())

        tiledMap = setMap(aoc12Map, worldWidth, worldHeight)
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
    }

    override fun render(delta: Float) {
        camera.update()
        with(tiledMapRenderer) {
            setView(camera)
            render()
        }

        breadthFirstSearch(aoc12Map)
    }

    override fun dispose() {
        tiledMap.disposeSafely()
    }

    private fun breadthFirstSearch(aoc12Map: List<List<Char>>) {

    }

    // practising kotlin DSL. Kotlin-dsl-examples:
    // https://github.com/antonarhipov/kotlin-dsl-examples/blob/master/src/main/kotlin/org/arhan/dslExtensions.kt
    private fun setMap(aoc12Map: List<List<Char>>, worldWidth: Int, worldHeight: Int): TiledMap =
        tiledMap {
            layers.add(
                tiledMapTileLayer(TiledMapTileLayer(worldWidth, worldHeight, 1, 1)) {
                    aoc12Map.reversed().mapIndexed { y, row ->
                        row.mapIndexed { x, char ->
                            setCell(x, y, Cell().setTile(StaticTiledMapTile(TextureRegion(char.toTexture()))))
                        }
                    }
                }
            )
        }

    // https://libgdx.com/wiki/graphics/2d/pixmaps
    private fun Char.toTexture(): Texture {
        val pixmap = Pixmap(1, 1, RGBA8888)
        when (this) {
            'S' -> pixmap.apply {
                setColor(Color.RED)
                fill()
            }

            'E' -> pixmap.apply {
                setColor(Color.BLUE)
                fill()
            }

            else -> pixmap.apply {
                setColor(
                    0.2F + 0.6F * ((this@toTexture.code - 'a'.code) * 1F / 25F),
                    1F,
                    0.4F + 0.6F * ((this@toTexture.code - 'a'.code) * 1F / 25F),
                    1F
                )
                fill()
            }
        }
        val texture = Texture(pixmap)
        pixmap.disposeSafely()
        return texture
    }

    private fun TiledMapTileLayer.forEachCell(
        startX: Int,
        startY: Int,
        size: Int,
        action: (TiledMapTileLayer.Cell, Int, Int) -> Unit
    ) {
        for (x in startX - size..startX + size) {
            for (y in startY - size until startY + size) {
                this.getCell(x, y)?.let { action(it, x, y) }
            }
        }
    }

    private fun tiledMap(lambda: TiledMap.() -> Unit) = TiledMap().apply(lambda)
    private fun tiledMapTileLayer(
        tiledMapTileLayer: TiledMapTileLayer,
        lambda: TiledMapTileLayer.() -> Unit
    ): TiledMapTileLayer = tiledMapTileLayer.apply(lambda)

}