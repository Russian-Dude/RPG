package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class HexagonalTiledMapRendererWithObjectsLayer extends HexagonalTiledMapRenderer {

    public HexagonalTiledMapRendererWithObjectsLayer(TiledMap map) {
        super(map);
    }

    public HexagonalTiledMapRendererWithObjectsLayer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public HexagonalTiledMapRendererWithObjectsLayer(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public HexagonalTiledMapRendererWithObjectsLayer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }


    @Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        super.renderTileLayer(layer);
        renderObjects(layer);
    }

    @Override
    public void renderObjects(MapLayer layer) {
        super.renderObjects(layer);
/*        if (layer instanceof TiledMapTileLayer) {
            for (int x = 0; x < ((TiledMapTileLayer) layer).getWidth() - 1; x++) {
                for (int y = 0; y < ((TiledMapTileLayer) layer).getHeight() - 1; y++) {
                    ((TiledMapTileLayer) layer).getCell(x, y).getTile().getObjects().forEach(this::renderObject);
                }
            }
        }*/
    }


    @Override
    public void renderObject(MapObject object) {
        if(object instanceof TextureMapObject) {
            TextureMapObject textureObj = (TextureMapObject) object;
            batch.draw(
                    textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY(),
                    textureObj.getOriginX(), textureObj.getOriginY(),
                    textureObj.getTextureRegion().getRegionWidth(), textureObj.getTextureRegion().getRegionHeight(),
                    textureObj.getScaleX(), textureObj.getScaleY(), textureObj.getRotation());
            if(textureObj.getProperties().containsKey("this")) System.out.println(textureObj.getRotation());
        } else if(object instanceof RectangleMapObject){
            RectangleMapObject rectObject = (RectangleMapObject) object;
            Rectangle rect = rectObject.getRectangle();
            ShapeRenderer sr = new ShapeRenderer();
            sr.setProjectionMatrix(batch.getProjectionMatrix());
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.rect(rect.x, rect.y, rect.width, rect.height);
            sr.end();
        }
    }
}
