package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.data.io.GameMapFileSaver;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.*;
import ru.rdude.rpg.game.mapVisual.MapTilesFactory;
import ru.rdude.rpg.game.mapVisual.SmallMapVisual;
import ru.rdude.rpg.game.mapVisual.VisualConstants;
import ru.rdude.rpg.game.utils.Functions;

public class MapGeneratorStage extends Stage implements MapGenerationObserver {

    public static MapGeneratorStage instance = new MapGeneratorStage();

    private Pixmap pixmap;
    private final Image mapImage = new Image(new SpriteDrawable(new Sprite(MapTilesFactory.getEmpty().getTextureRegion())));
    private final Image mapHighlightsImage = new Image(new SpriteDrawable(new Sprite(MapTilesFactory.getEmpty().getTextureRegion())));

    private final Button backButton = new TextButton("<< Back", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);

    private final Button generateButton = new TextButton("Generate", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
    private final Button cancelButton = new TextButton(" Cancel ", UiData.DEFAULT_SKIN, UiData.NO_BUTTON_STYLE);

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
    private Generator currentGenerator;

    private final SelectBox<GameMapSize> gameMapSizeSelectBox = new SelectBox<>(UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final SelectBox<String> waterAlgorithmSelectBox = new SelectBox<>(UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label biomeFrequencyLabel = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label waterAmountLabel = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Label riversAmountLabel = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
    private final Slider biomeFrequencySlider = new Slider(0.001f, 0.1f, 0.001f, false, UiData.DEFAULT_SKIN);
    private final Slider waterAmountSlider = new Slider(0.01f, 0.99f, 0.01f, false, UiData.DEFAULT_SKIN);
    private final Slider riversAmountSlider = new Slider(0f, 25f, 1f, false, UiData.DEFAULT_SKIN);
    private final CheckBox equalBiomesCheckBox = new CheckBox("", UiData.DEFAULT_SKIN, UiData.RED_GREEN_CHECKBOX);

    private final CheckBox showRoadsCheckBox = new CheckBox("", UiData.DEFAULT_SKIN, UiData.RED_GREEN_CHECKBOX);


    private GameMap gameMap;

    public MapGeneratorStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        mapImage.setFillParent(true);
        mapHighlightsImage.setFillParent(true);

        Table mainTable = new Table();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.space(20f);
        verticalGroup.addActor(new Label("MAP GENERATOR", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE));

        HorizontalGroup topHorizontalGroup = new HorizontalGroup();
        topHorizontalGroup.space(15f);

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
        topHorizontalGroup.addActor(mapTable);
        verticalGroup.addActor(topHorizontalGroup);


        // controls
        Table controls = new Table();

        // map size
        gameMapSizeSelectBox.setItems(GameMapSize.values());
        gameMapSizeSelectBox.setSelected(GameMapSize.L);
        controls.add(new Label("Size", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f).align(Align.left);
        controls.add(gameMapSizeSelectBox).fill();
        controls.row().space(20f);

        // water algorithm
        waterAlgorithmSelectBox.setItems(GeneratorWaterAlgorithm.descriptions());
        waterAlgorithmSelectBox.setSelected(GeneratorWaterAlgorithm.MIXED.description);
        controls.add(new Label("Water algorithm", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f).align(Align.left);
        controls.add(waterAlgorithmSelectBox).fill();
        controls.row().space(20f);

        // equal biomes
        equalBiomesCheckBox.setChecked(true);
        controls.add(new Label("Equal biomes", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f).align(Align.left);
        controls.add(equalBiomesCheckBox);
        controls.row().space(20f);

        // biome frequency
        biomeFrequencySlider.setValue(0.004f);
        VerticalGroup biomeFrequencyGroup = new VerticalGroup();
        biomeFrequencyGroup.addActor(biomeFrequencyLabel);
        biomeFrequencyGroup.addActor(biomeFrequencySlider);
        biomeFrequencyGroup.align(Align.center);
        controls.add(new Label("Biomes frequency", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f).align(Align.left);
        controls.add(biomeFrequencyGroup).fill();
        controls.row().space(20f);

        // water amount
        waterAmountSlider.setValue(0.33f);
        VerticalGroup waterAmountGroup = new VerticalGroup();
        waterAmountGroup.addActor(waterAmountLabel);
        waterAmountGroup.addActor(waterAmountSlider);
        waterAmountGroup.align(Align.center);
        controls.add(new Label("Water amount", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f).align(Align.left);
        controls.add(waterAmountGroup).fill();
        controls.row().space(20f);

        // rivers amount
        riversAmountSlider.setValue(10);
        VerticalGroup riversAmountGroup = new VerticalGroup();
        riversAmountGroup.addActor(riversAmountLabel);
        riversAmountGroup.addActor(riversAmountSlider);
        riversAmountGroup.align(Align.center);
        controls.add(new Label("Rivers amount", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE)).padLeft(25f).align(Align.left);
        controls.add(riversAmountGroup).fill();
        controls.row().space(20f);

        topHorizontalGroup.addActor(controls);

        // default settings button
        Button defaultButton = new TextButton("Default", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
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

        HorizontalGroup bottomHorizontalGroup = new HorizontalGroup();
        bottomHorizontalGroup.align(Align.left);
        bottomHorizontalGroup.space(20f);

        // back button
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!backButton.isDisabled()) {
                    Game.getGameVisual().backMenuStage();
                }
            }
        });
        bottomHorizontalGroup.addActor(backButton);

        // show roads
        bottomHorizontalGroup.addActor(new Label("Show roads", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        bottomHorizontalGroup.addActor(showRoadsCheckBox);

        // map name
        TextField nameField = new TextField("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        bottomHorizontalGroup.addActor(new Label("Name", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
        bottomHorizontalGroup.addActor(nameField);

        // save button
        Button saveButton = new TextButton("Save", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameMap != null && pixmap != null) {
                    String name = nameField.getText().isBlank() ? "Unnamed map " + Functions.generateGuid() : nameField.getText();
                    gameMap.setName(name);
                    GameMapFileSaver.save(gameMap, pixmap, name);
                }
            }
        });
        bottomHorizontalGroup.addActor(saveButton);

        // generate button
        cancelButton.setVisible(false);
        Group generateCancelGroup = new Group();
        generateCancelGroup.addActor(generateButton);
        generateCancelGroup.addActor(cancelButton);
        generateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancelButton.setVisible(true);
                generateButton.setVisible(false);
                backButton.setDisabled(true);
                generateMap();
            }
        });
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancelButton.setVisible(false);
                generateButton.setVisible(true);
                if (currentGenerator != null) {
                    currentGenerator.interrupt();
                }
                backButton.setDisabled(false);
            }
        });
        controls.add(generateCancelGroup).align(Align.bottomLeft);


        controls.pack();
        verticalGroup.addActor(topHorizontalGroup);
        mainTable.add(verticalGroup);
        mainTable.row();
        mainTable.add(bottomHorizontalGroup).align(Align.left).padLeft(25f).padTop(20f);
        mainTable.pack();
        addActor(mainTable);

        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
    }

    private void generateMap() {
        progressTable.setVisible(true);
        currentGenerator = new Generator(gameMapSizeSelectBox.getSelected());
        currentGenerator.setNewBiomCoefficient(biomeFrequencySlider.getValue());
        currentGenerator.setWaterAlgorithm(GeneratorWaterAlgorithm.byDescription(waterAlgorithmSelectBox.getSelected()));
        currentGenerator.setWaterAmount(waterAmountSlider.getValue());
        currentGenerator.setEqualBioms(equalBiomesCheckBox.isChecked());
        currentGenerator.setRiversAmount((int) riversAmountSlider.getValue());
        currentGenerator.subscribe(this);
        Thread generationThread = new Thread(() -> {
            GameMap generatedMap = currentGenerator.createMap();
            gameMap = generatedMap;
            if (generationProcess != GenerationProcess.INTERRUPTED) {
                startCreatingTiles = true;
            }
            currentGenerator.unsubscribe(this);
        });
        generationThread.start();
    }

    @Override
    public void update(GenerationProcess process, float current, float max) {
        currentProgressText = process.description;
        currentProgressBar.setRange(0f, max);
        currentProgressBar.setValue(current);
        if (process == GenerationProcess.FINISH || process == GenerationProcess.INTERRUPTED) {
            currentProgressText = "";
            generationProcess = process == GenerationProcess.FINISH ? GenerationProcess.NO : GenerationProcess.INTERRUPTED;
            mainProgressBar.setValue(0f);
            progressTable.setVisible(false);
            cancelButton.setVisible(false);
            generateButton.setVisible(true);
            backButton.setDisabled(false);
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
        mapHighlightsImage.setVisible(showRoadsCheckBox.isChecked());
        if (startCreatingTiles) {
            startCreatingTiles = false;
            final OrthographicCamera camera = new OrthographicCamera();
            float w = gameMap.getWidth() * VisualConstants.MINI_TILE_WIDTH_0_75;
            float h = gameMap.getHeight() * VisualConstants.MINI_TILE_HEIGHT;
            camera.setToOrtho(false, w, h);
            camera.update();

            SmallMapVisual smallMapVisual = new SmallMapVisual(camera, gameMap);

            Texture mapTexture = smallMapVisual.getMapTexture();
            pixmap = smallMapVisual.getPixmap();
            ((SpriteDrawable) mapImage.getDrawable()).getSprite().setRegion(mapTexture);
            ((SpriteDrawable) mapHighlightsImage.getDrawable()).getSprite().setRegion(smallMapVisual.getRoadsAndObjectsTexture());
        }
    }
}
