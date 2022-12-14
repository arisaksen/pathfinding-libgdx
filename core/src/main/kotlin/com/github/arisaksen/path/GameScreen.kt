package com.github.arisaksen.path

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import java.io.File
import ktx.log.logger

class GameScreen(file: String) : KtxScreen {
    private val aoc12Map: List<List<Char>> = File("assets", "$file.txt").readLines().map { it.toList() }
    private var worldWidth: Int = 0
    private var worldHeight: Int = 0
    private lateinit var startCell: CellData
    private lateinit var endCell: CellData
    private val searchFrontier = ArrayDeque<Search>()
    private val cellsToVisitEachRender: Int = 20
    private val cellsVisited = mutableSetOf<CellData>()
    private val camera: OrthographicCamera = OrthographicCamera()
    private lateinit var tiledMap: TiledMap
    private lateinit var tiledMapRenderer: TiledMapRenderer
    private val batch: Batch by lazy { SpriteBatch() }

    override fun show() {
        worldWidth = aoc12Map[0].size
        worldHeight = aoc12Map.size

        camera.setToOrtho(false, worldWidth.toFloat(), worldHeight.toFloat())

        tiledMap = setMap(aoc12Map, worldWidth, worldHeight)
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
        searchFrontier.addFirst(Search(startCell))
    }

    override fun render(delta: Float) {
        camera.update()
        with(tiledMapRenderer) {
            setView(camera)
            render()
        }

        breadthFirstSearch(cellsToVisitEachRender, cellsVisited.size)
        drawVisitedCells(cellsVisited)
    }

    override fun dispose() {
        tiledMap.disposeSafely()
        batch.disposeSafely()
    }

    private fun breadthFirstSearch(cellsToVisitEachRender: Int, cellsVisitedSizeAtRenderStart: Int) {

        while (searchFrontier.isNotEmpty()) {
            val currentSearch: Search = searchFrontier.removeFirst()
            if (cellsVisited.contains(currentSearch.cellData)) {
                break
            } else if (currentSearch.cellData == endCell) {
                log.debug { "The End is reached $currentSearch" }
                searchFrontier.clear()
                break
            } else if (cellsVisited.size >= cellsVisitedSizeAtRenderStart + cellsToVisitEachRender) return

            listOf(0 to 1, -1 to 0, 0 to -1, 1 to 0)
                .map {
                    Search(
                        cellData = CellData(currentSearch.cellData.x + it.first, currentSearch.cellData.y + it.second),
                        distance = currentSearch.distance + 1
                    )
                }
                .filter { it.isInMap(aoc12Map) }
                .filter { currentSearch.isValidRoute(it, aoc12Map) }
                .map { searchFrontier.add(it) }

            cellsVisited.add(currentSearch.cellData)
                .also { log.debug { "Visted: ${cellsVisited.size}. Added $currentSearch to visited" } }
        }
    }

    private fun Search.isInMap(map: List<List<Char>>): Boolean {
        return this.cellData.y in map.indices && this.cellData.x in map[0].indices
    }

    private fun Search.isValidRoute(nextSearch: Search, map: List<List<Char>>): Boolean =
        measureHeight(nextSearch.cellData, map) - measureHeight(this.cellData, map) <= 1

    private fun measureHeight(cellData: CellData, map: List<List<Char>>): Int {
        return when (val elevation = map[cellData.y][cellData.x]) {
            'S' -> 'a'.code
            'E' -> 'z'.code
            else -> elevation.code
        } - 'a'.code
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
                            if (char == 'S') startCell = CellData(x, y)
                            if (char == 'E') endCell = CellData(x, y)
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
                setColor(Color.BLACK)
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

    private fun tiledMap(lambda: TiledMap.() -> Unit) = TiledMap().apply(lambda)
    private fun tiledMapTileLayer(
        tiledMapTileLayer: TiledMapTileLayer,
        lambda: TiledMapTileLayer.() -> Unit
    ): TiledMapTileLayer = tiledMapTileLayer.apply(lambda)

    private fun drawVisitedCells(cellsVisited: MutableSet<CellData>) {
        val texture = Texture(Pixmap(1 * 8, 1 * 8, RGBA8888).apply { setColor(0f, 0f, 1f, 0.2f); fill() })
        val sprite = Sprite(texture)

        cellsVisited.reversed().forEach {
            sprite.setPosition(it.x.toFloat() * 8, it.y.toFloat() * 8)
            with(sprite) {
                batch.use {
                    draw(batch)
                }
            }
        }
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}