package com.github.arisaksen.path

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import java.io.File

class GameScreen : KtxScreen {
    private lateinit var tiledMap: TiledMap
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport
    private lateinit var tiledMapRenderer: TiledMapRenderer

    override fun show() {
        val aoc12Map = File("assets", "Day12.txt").readLines().map { it.toList() }
        val worldWidth = aoc12Map[0].size
        val worldHeight = aoc12Map.size

        camera = OrthographicCamera()
        camera.setToOrtho(false, worldWidth.toFloat(), worldHeight.toFloat())
        tiledMap = setMap(aoc12Map, worldWidth, worldHeight)
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
    }

    override fun render(delta: Float) {
        camera.update()
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()
    }

    override fun dispose() {
        tiledMap.disposeSafely()
    }

    private fun setMap(aoc12Map: List<List<Char>>, worldWidth: Int, worldHeight: Int): TiledMap {
        val map = TiledMap()
        val layers = map.layers

        val mapLayer = TiledMapTileLayer(worldWidth, worldHeight, 1, 1)
        aoc12Map.flatMapIndexed { y, row ->
            row.mapIndexed { x, char ->
                val cell = Cell().setTile(StaticTiledMapTile(TextureRegion(char.toTexture())))
                mapLayer.setCell(x, y, cell)
            }
        }
        layers.add(mapLayer)

        return map
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

}