package ru.rdude.rpg.game.battleVisual;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreType
public class BattleVisual extends Stage {

    //private final Cell cell;

    private Image sky = new Image();
    //private Image ground = UiData.ItemBorder.BRONZE;
    private Image background = new Image();
    private List<Image> farDecorations = new ArrayList<>();
    private List<Image> midDecorations = new ArrayList<>();
    private List<Image> closeDecorations = new ArrayList<>();

    public BattleVisual() {
        //this.cell = cell;
/*        ground.setSize(500f, 500f);
        addActor(ground);*/
    }
}
