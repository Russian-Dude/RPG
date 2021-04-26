package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.map.*;
import ru.rdude.rpg.game.mapVisual.MapTilesFactory;
import ru.rdude.rpg.game.mapVisual.SmallMapVisual;
import ru.rdude.rpg.game.mapVisual.VisualConstants;

public class MapGeneratorStage extends Stage implements MapGenerationObserver {

    private final Image mapImage = new Image(new SpriteDrawable(new Sprite(MapTilesFactory.getEmpty().getTextureRegion())));
    private final Image mapHighlightsImage = new Image(new SpriteDrawable(new Sprite(MapTilesFactory.getEmpty().getTextureRegion())));

    private final ProgressBar mainProgressBar = new ProgressBar(0f, 12f, 1f, false, UiData.DEFAULT_SKIN);
    private final ProgressBar currentProgressBar = new ProgressBar(0f, 10f, 0.1f, false, UiData.DEFAULT_SKIN);
    private final Label currentProgressLabel = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Table progressTable = new Table();

    private String currentProgressText = "";
    private float currentProgress = 0f;
    private float currentProgressMax = 0f;
    private float fullProgress = 0f;
    private float fullProgressMax = 0f;
    private boolean startCreatingTiles = false;
    private GenerationProcess generationProcess = GenerationProcess.NO;

    private final SelectBox<GameMapSize> gameMapSizeSelectBox = new SelectBox<>(UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final SelectBox<String> waterAlgorithmSelectBox = new SelectBox<>(UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label biomeFrequencyLabel = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label waterAmountLabel = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label riversAmountLabel = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Slider biomeFrequencySlider = new Slider(0.001f, 0.1f, 0.001f, false, UiData.DEFAULT_SKIN);
    private final Slider waterAmountSlider = new Slider(0.01f, 0.99f, 0.01f, false, UiData.DEFAULT_SKIN);
    private final Slider riversAmountSlider = new Slider(0f, 25f, 1f, false, UiData.DEFAULT_SKIN);
    private final CheckBox equalBiomesCheckBox = new CheckBox("", UiData.DEFAULT_SKIN, UiData.RED_GREEN_CHECKBOX);

    private GameMap gameMap;

    public MapGeneratorStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        mapImage.setFillParent(true);
        mapHighlightsImage.setFillParent(true);

        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.addActor(new Label("MAP GENERATOR", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));

        Table mapTable = new Table();
        mapTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        Group mapVisualGroup = new Group();
        mapVisualGroup.addActor(mapImage);
        mapVisualGroup.addActor(mapHighlightsImage);

        progressTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        progressTable.align(Align.bottom);
        progressTable.setFillParent(true);
        progressTable.add(currentProgressLabel).padBottom(20f);
        progressTable.row();
        progressTable.add(mainProgressBar).width(Gdx.graphics.getHeight() - 50f);
        progressTable.row();
        progressTable.add(currentProgressBar).width(Gdx.graphics.getHeight() - 50f);
        progressTable.pack();
        progressTable.setVisible(false);
        mapVisualGroup.addActor(progressTable);
        float mapHeight = Gdx.graphics.getHeight() / 2f;
        mapTable.add(mapVisualGroup).width(mapHeight * 2f).height(mapHeight);
        mapTable.pack();
        verticalGroup.addActor(mapTable);


        // controls
        Table controls = new Table();

        // map size
        gameMapSizeSelectBox.setItems(GameMapSize.values());
        gameMapSizeSelectBox.setSelected(GameMapSize.L);
        controls.add(new Label("Size", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        controls.add(gameMapSizeSelectBox).fill().padLeft(25f);

        // biome frequency
        biomeFrequencySlider.setValue(0.004f);
        VerticalGroup biomeFrequencyGroup = new VerticalGroup();
        biomeFrequencyGroup.addActor(biomeFrequencyLabel);
        biomeFrequencyGroup.addActor(biomeFrequencySlider);
        biomeFrequencyGroup.align(Align.center);
        controls.add(new Label("Biomes frequency", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f);
        controls.add(biomeFrequencyGroup).fill().padLeft(25f);

        // equal biomes
        equalBiomesCheckBox.setChecked(true);
        controls.add(new Label("Equal biomes", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f);
        controls.add(equalBiomesCheckBox).padLeft(25f);
        controls.row();

        // water algorithm
        waterAlgorithmSelectBox.setItems(GeneratorWaterAlgorithm.descriptions());
        waterAlgorithmSelectBox.setSelected(GeneratorWaterAlgorithm.MIXED.description);
        controls.add(new Label("Water algorithm", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        controls.add(waterAlgorithmSelectBox).fill().padLeft(25f);

        // water amount
        waterAmountSlider.setValue(0.33f);
        VerticalGroup waterAmountGroup = new VerticalGroup();
        waterAmountGroup.addActor(waterAmountLabel);
        waterAmountGroup.addActor(waterAmountSlider);
        waterAmountGroup.align(Align.center);
        controls.add(new Label("Water amount", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f);
        controls.add(waterAmountGroup).fill().padLeft(25f);

        // rivers amount
        riversAmountSlider.setValue(10);
        VerticalGroup riversAmountGroup = new VerticalGroup();
        riversAmountGroup.addActor(riversAmountLabel);
        riversAmountGroup.addActor(riversAmountSlider);
        riversAmountGroup.align(Align.center);
        controls.add(new Label("Rivers amount", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f);
        controls.add(riversAmountGroup).fill().padLeft(25f);
        controls.row();

        // default settings button
        Button defaultButton = new TextButton("Default", UiData.DEFAULT_SKIN);
        defaultButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waterAlgorithmSelectBox.setSelected(GeneratorWaterAlgorithm.MIXED.description);
                gameMapSizeSelectBox.setSelected(GameMapSize.L);
                biomeFrequencySlider.setValue(0.004f);
                waterAmountSlider.setValue(0.33f);
                equalBiomesCheckBox.setChecked(true);
                riversAmountSlider.setValue(10);
            }
        });
        controls.add(defaultButton);

        // generate button
        Button generateButton = new TextButton("Generate", UiData.DEFAULT_SKIN);
        generateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                generateMap();
            }
        });
        controls.add(generateButton);
        controls.row();


        controls.pack();
        verticalGroup.addActor(controls);
        mainTable.add(verticalGroup);
        mainTable.pack();
        addActor(mainTable);

        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    private void generateMap() {
        progressTable.setVisible(true);
        Generator generator = new Generator(gameMapSizeSelectBox.getSelected());
        generator.setNewBiomCoefficient(biomeFrequencySlider.getValue());
        generator.setWaterAlgorithm(GeneratorWaterAlgorithm.byDescription(waterAlgorithmSelectBox.getSelected()));
        generator.setWaterAmount(waterAmountSlider.getValue());
        generator.setEqualBioms(equalBiomesCheckBox.isChecked());
        generator.setRiversAmount((int) riversAmountSlider.getValue());
        generator.subscribe(this);
        Thread generationThread = new Thread(() -> {
            gameMap = generator.createMap();
            startCreatingTiles = true;
            generator.unsubscribe(this);
        });
        generationThread.start();
    }

    @Override
    public void update(GenerationProcess process, float current, float max) {
        currentProgressText = process.description;
        currentProgressBar.setRange(0f, max);
        currentProgressBar.setValue(current);
        if (process == GenerationProcess.FINISH) {
            currentProgressText = "";
            generationProcess = GenerationProcess.NO;
            mainProgressBar.setValue(0f);
            progressTable.setVisible(false);
        }
        else if (generationProcess != process) {
            if (generationProcess != GenerationProcess.START) {
                mainProgressBar.setValue(mainProgressBar.getValue() + 1);
            }
            generationProcess = process;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

    }

    @Override
    public void draw() {
        super.draw();
        currentProgressLabel.setText(currentProgressText);
        biomeFrequencyLabel.setText(String.format("%.3f", biomeFrequencySlider.getValue()));
        waterAmountLabel.setText(String.format("%.2f", waterAmountSlider.getValue()));
        riversAmountLabel.setText((int) riversAmountSlider.getValue());
        if (startCreatingTiles) {
            startCreatingTiles = false;
            final OrthographicCamera camera = new OrthographicCamera();
            float w = gameMap.getWidth() * VisualConstants.MINI_TILE_WIDTH_0_75;
            float h = gameMap.getHeight() * VisualConstants.MINI_TILE_HEIGHT;
            camera.setToOrtho(false, w, h);
            camera.update();

            SmallMapVisual smallMapVisual = new SmallMapVisual(camera, gameMap);

            ((SpriteDrawable) mapImage.getDrawable()).getSprite().setRegion(smallMapVisual.getMapTexture());
            ((SpriteDrawable) mapHighlightsImage.getDrawable()).getSprite().setRegion(smallMapVisual.getRoadsAndObjectsTexture());
        }
    }
}
