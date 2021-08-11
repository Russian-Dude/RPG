package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import ru.rdude.rpg.game.logic.game.Game;

public class MapRenderer extends BatchTiledMapRenderer {

    /**
     * true for X-Axis, false for Y-Axis
     */
    private boolean staggerAxisX = true;
    /**
     * true for even StaggerIndex, false for odd
     */
    private boolean staggerIndexEven = false;
    /**
     * the parameter defining the shape of the hexagon from tiled. more specifically it represents the length of the sides that
     * are parallel to the stagger axis. e.g. with respect to the stagger axis a value of 0 results in a rhombus shape, while a
     * value equal to the tile length/height represents a square shape and a value of 0.5 represents a regular hexagon if tile
     * length equals tile height
     */
    private float hexSideLength = 0f;

    public MapRenderer(TiledMap map) {
        super(map);
        init(map);
    }

    public MapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
        init(map);
    }

    public MapRenderer(TiledMap map, Batch batch) {
        super(map, batch);
        init(map);
    }

    public MapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
        init(map);
    }

    private void init(TiledMap map) {
        String axis = map.getProperties().get("staggeraxis", String.class);
        if (axis != null) {
            if (axis.equals("x")) {
                staggerAxisX = true;
            } else {
                staggerAxisX = false;
            }
        }

        String index = map.getProperties().get("staggerindex", String.class);
        if (index != null) {
            if (index.equals("even")) {
                staggerIndexEven = true;
            } else {
                staggerIndexEven = false;
            }
        }

        Integer length = map.getProperties().get("hexsidelength", Integer.class);
        if (length != null) {
            hexSideLength = length.intValue();
        } else {
            if (staggerAxisX) {
                length = map.getProperties().get("tilewidth", Integer.class);
                if (length != null) {
                    hexSideLength = 0.5f * length.intValue();
                } else {
                    TiledMapTileLayer tmtl = (TiledMapTileLayer) map.getLayers().get(0);
                    hexSideLength = 0.5f * tmtl.getTileWidth();
                }
            } else {
                length = map.getProperties().get("tileheight", Integer.class);
                if (length != null) {
                    hexSideLength = 0.5f * length.intValue();
                } else {
                    TiledMapTileLayer tmtl = (TiledMapTileLayer) map.getLayers().get(0);
                    hexSideLength = 0.5f * tmtl.getTileHeight();
                }
            }
        }
    }

    @Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        final Color batchColor = batch.getColor();
        MapVisual mapVisual = Game.getCurrentGame().getGameMap().getStage().getMapVisual();

        final int layerWidth = layer.getWidth();
        final int layerHeight = layer.getHeight();

        final float layerTileWidth = layer.getTileWidth() * unitScale;
        final float layerTileHeight = layer.getTileHeight() * unitScale;

        final float layerOffsetX = layer.getRenderOffsetX() * unitScale;
        // offset in tiled is y down, so we flip it
        final float layerOffsetY = -layer.getRenderOffsetY() * unitScale;

        final float layerHexLength = hexSideLength * unitScale;

        if (staggerAxisX) {
            final float tileWidthLowerCorner = (layerTileWidth - layerHexLength) / 2;
            final float tileWidthUpperCorner = (layerTileWidth + layerHexLength) / 2;
            final float layerTileHeight50 = layerTileHeight * 0.50f;

            final int row1 = Math.max(0, (int) ((viewBounds.y - layerTileHeight50 - layerOffsetX) / layerTileHeight));
            final int row2 = Math.min(layerHeight,
                    (int) ((viewBounds.y + viewBounds.height + layerTileHeight - layerOffsetX) / layerTileHeight));

            final int col1 = Math.max(0, (int) (((viewBounds.x - tileWidthLowerCorner - layerOffsetY) / tileWidthUpperCorner)));
            final int col2 = Math.min(layerWidth,
                    (int) ((viewBounds.x + viewBounds.width + tileWidthUpperCorner - layerOffsetY) / tileWidthUpperCorner));

            // depending on the stagger index either draw all even before the odd or vice versa
            final int colA = (staggerIndexEven == (col1 % 2 == 0)) ? col1 + 1 : col1;
            final int colB = (staggerIndexEven == (col1 % 2 == 0)) ? col1 : col1 + 1;

            for (int row = row2 - 1; row >= row1; row--) {
                for (int col = colA; col < col2; col += 2) {
                    renderCell(layer.getCell(col, row), tileWidthUpperCorner * col + layerOffsetX,
                            layerTileHeight50 + (layerTileHeight * row) + layerOffsetY, batchColor, layer);
                }
                for (int col = colB; col < col2; col += 2) {
                    renderCell(layer.getCell(col, row), tileWidthUpperCorner * col + layerOffsetX,
                            layerTileHeight * row + layerOffsetY, batchColor, layer);
                }
            }
        } else {
            final float tileHeightLowerCorner = (layerTileHeight - layerHexLength) / 2;
            final float tileHeightUpperCorner = (layerTileHeight + layerHexLength) / 2;
            final float layerTileWidth50 = layerTileWidth * 0.50f;

            final int row1 = Math.max(0, (int) (((viewBounds.y - tileHeightLowerCorner - layerOffsetX) / tileHeightUpperCorner)));
            final int row2 = Math.min(layerHeight,
                    (int) ((viewBounds.y + viewBounds.height + tileHeightUpperCorner - layerOffsetX) / tileHeightUpperCorner));

            final int col1 = Math.max(0, (int) (((viewBounds.x - layerTileWidth50 - layerOffsetY) / layerTileWidth)));
            final int col2 = Math.min(layerWidth,
                    (int) ((viewBounds.x + viewBounds.width + layerTileWidth - layerOffsetY) / layerTileWidth));

            float shiftX = 0;
            for (int row = row2 - 1; row >= row1; row--) {
                // depending on the stagger index either shift for even or uneven indexes
                if ((row % 2 == 0) == staggerIndexEven)
                    shiftX = layerTileWidth50;
                else
                    shiftX = 0;
                for (int col = col1; col < col2; col++) {
                    renderCell(layer.getCell(col, row), layerTileWidth * col + shiftX + layerOffsetX,
                            tileHeightUpperCorner * row + layerOffsetY, batchColor, layer);
                }
            }
        }
    }

    /**
     * render a single cell
     */
    private void renderCell(final TiledMapTileLayer.Cell cell, float x, float y, final Color batchColor, MapLayer layer) {
        if (cell instanceof MapTileCell) {
            final float opacity = Game.getCurrentGame().getGameMap().getStage().getMapVisual().getCellsOpacity(layer, ((MapTileCell) cell).x, ((MapTileCell) cell).y);
            final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, opacity);
            final TiledMapTile tile = cell.getTile();
            if (tile != null) {
                if (tile instanceof AnimatedTiledMapTile) return;

                final boolean flipX = cell.getFlipHorizontally();
                final boolean flipY = cell.getFlipVertically();
                final int rotations = cell.getRotation();

                TextureRegion region = tile.getTextureRegion();

                float x1 = x + tile.getOffsetX() * unitScale;
                float y1 = y + tile.getOffsetY() * unitScale;
                float x2 = x1 + region.getRegionWidth() * unitScale;
                float y2 = y1 + region.getRegionHeight() * unitScale;

                float u1 = region.getU();
                float v1 = region.getV2();
                float u2 = region.getU2();
                float v2 = region.getV();

                vertices[0] = x1;
                vertices[1] = y1;
                vertices[2] = color;
                vertices[3] = u1;
                vertices[4] = v1;
                vertices[5] = x1;
                vertices[6] = y2;
                vertices[7] = color;
                vertices[8] = u1;
                vertices[9] = v2;

                vertices[10] = x2;
                vertices[11] = y2;
                vertices[12] = color;
                vertices[13] = u2;
                vertices[14] = v2;
                vertices[15] = x2;
                vertices[16] = y1;
                vertices[17] = color;
                vertices[18] = u2;
                vertices[19] = v1;

                if (flipX) {
                    float temp = vertices[3];
                    vertices[3] = vertices[13];
                    vertices[13] = temp;
                    temp = vertices[8];
                    vertices[8] = vertices[18];
                    vertices[18] = temp;
                }
                if (flipY) {
                    float temp = vertices[4];
                    vertices[4] = vertices[14];
                    vertices[14] = temp;
                    temp = vertices[9];
                    vertices[9] = vertices[19];
                    vertices[19] = temp;
                }
                if (rotations == 2) {
                    float tempU = vertices[3];
                    vertices[3] = vertices[13];
                    vertices[13] = tempU;
                    tempU = vertices[8];
                    vertices[8] = vertices[18];
                    vertices[18] = tempU;
                    float tempV = vertices[4];
                    vertices[4] = vertices[14];
                    vertices[14] = tempV;
                    tempV = vertices[9];
                    vertices[9] = vertices[19];
                    vertices[19] = tempV;
                }
                batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
            }
        }
    }

}

