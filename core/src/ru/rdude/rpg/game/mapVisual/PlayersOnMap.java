package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.settings.GameSettings;

import java.util.LinkedList;
import java.util.Queue;

public class PlayersOnMap extends Group {

    // TODO: 20.04.2021 sprite should depend on player's party size
    private Sprite playersSprite = new Sprite(MapTilesFactory.getAvatar(Game.getCurrentGame().getCurrentPlayers().getBeings().size()));
    private boolean moving;
    private Cell position;
    private Cell destination = null;
    private int destinationEndPointX = 0;
    private int destinationEndPointY = 0;
    private Queue<Cell> path = new LinkedList<>();


    public PlayersOnMap(Cell position) {
        super();
        this.position = position;
        this.destination = position;
        addActor(new Image(playersSprite));
        findCellEndPointX(position);
        findCellEndPointY(position);
        moveBy(destinationEndPointX, destinationEndPointY);
    }

    public Sprite getPlayersSprite() {
        return playersSprite;
    }

    public boolean isMoving() {
        return moving;
    }

    public void moveByPath(Queue<Cell> path) {
        this.path = path;
        if (path != null && !path.isEmpty()){
            destination = path.poll();
            findCellEndPointX(destination);
            findCellEndPointY(destination);
        }
    }

    private void findCellEndPointX(Cell cell) {
        destinationEndPointX = (int) (cell.getX() * VisualConstants.TILE_WIDTH_0_75);
    }

    private void findCellEndPointY(Cell cell) {
        int offset = cell.getX() % 2 == 0 ? (int) VisualConstants.TILE_HEIGHT_HALF : 0;
        destinationEndPointY = cell.getY() * VisualConstants.TILE_HEIGHT + offset;
    }

    private void move() {
        int SPEED = GameSettings.getPlayersMovingSpeedOnMap().value;
        int moveX = destinationEndPointX < getX() ? SPEED * (-1) : SPEED;
        int moveY = destinationEndPointY < getY() ? SPEED * (-1) : SPEED;
        moveBy(moveX, moveY);
        if ((int) getX() == destinationEndPointX && (int) getY() == destinationEndPointY) {
            Game.getCurrentGame().getGameMap().setPlayerPosition(destination);
            position = destination;
            if (!path.isEmpty()) {
                destination = path.poll();
                findCellEndPointX(destination);
                findCellEndPointY(destination);
            }
            else {
                path = null;
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        moving = !destination.equals(position) && path != null;
        if (moving) {
            move();
        }
    }
}
