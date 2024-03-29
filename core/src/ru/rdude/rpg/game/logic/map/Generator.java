package ru.rdude.rpg.game.logic.map;

import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.enums.WaterDepth;
import ru.rdude.rpg.game.logic.map.aStarImpl.MapPathFinder;
import ru.rdude.rpg.game.logic.map.aStarImpl.MapRiverScorer;
import ru.rdude.rpg.game.logic.map.aStarImpl.MapRoadScorer;
import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.logic.map.objects.Dungeon;
import ru.rdude.rpg.game.logic.map.objects.MapObject;
import ru.rdude.rpg.game.logic.map.objects.MapObjectRoadAvailability;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.TimeCounter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.floor;
import static ru.rdude.rpg.game.utils.Functions.*;

public class Generator {

    private final AtomicBoolean generating = new AtomicBoolean(false);

    private Set<MapGenerationObserver> observers = new HashSet<>();

    private final GameMap map;
    private int width; // 64
    private int height; // 64
    private final List<Biom> bioms; // available bioms
    private final List<Relief> reliefs; // available reliefs

    private final int citiesAmount; //  Need to be smaller on smaller maps
    private final int dungeonsAmount;
    private final List<Point> mapObjectsPoints;
    private final List<City> cities;
    private final List<Dungeon> dungeons;
    private final Map<Biom, Integer> biomAmount;
    // next parameters can be set directly with setter after creating Generator. Instead - default values:
    private double newBiomCoefficient; // chance to generate new biom instead of surrounding bioms
    private double newReliefCoefficient; // same with relief
    private boolean equalBioms; // generation type where biom trying to be equal size

    private GeneratorWaterAlgorithm waterAlgorithm;
    private float waterAmount; // works only with separate water algorithm
    private int riversAmount;
    private final Set<Cell> waterCells = new HashSet<>();

    // zones help optimize finding unstepped cells on big maps
    private final Map<CellProperty, Set<Zone>> zones;

    public Generator(GameMapSize gameMapSize) {
        this(
                gameMapSize.getWidth(),
                gameMapSize.getHeight(),
                Arrays.asList(Biom.values()),
                Arrays.asList(Relief.values()),
                gameMapSize.getWidth() / 32,
                gameMapSize.getWidth() / 4);
    }

    public Generator(int width, int height, List<Biom> bioms, List<Relief> reliefs, int citiesAmount, int dungeonsAmount) {
        this.width = width;
        this.height = height;
        this.bioms = new ArrayList<>();
        this.bioms.addAll(bioms);
        this.reliefs = new ArrayList<>();
        this.reliefs.addAll(reliefs);
        this.citiesAmount = citiesAmount;
        this.dungeonsAmount = dungeonsAmount;
        zones = new HashMap<>();
        for (CellProperty property : CellProperty.values()) {
            zones.put(property, divideMapToZones());
        }
        biomAmount = new HashMap<>();
        fillBiomAmountMap(bioms);
        map = new GameMap(width, height);
        newBiomCoefficient = 0.004;
        newReliefCoefficient = 0.3;
        waterAlgorithm = GeneratorWaterAlgorithm.MIXED;
        waterAmount = 0.33f;
        riversAmount = 10;

        equalBioms = true;

        mapObjectsPoints = new ArrayList<>();
        cities = new ArrayList<>();
        dungeons = new ArrayList<>();
    }

    public void subscribe(MapGenerationObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(MapGenerationObserver observer) {
        observers.remove(observer);
    }

    public void setSize(GameMapSize size) {
        this.width = size.getWidth();
        this.height = size.getHeight();
    }

    public void setWaterAlgorithm(GeneratorWaterAlgorithm waterAlgorithm) {
        this.waterAlgorithm = waterAlgorithm;
    }

    public void setNewBiomCoefficient(double newBiomCoefficient) {
        this.newBiomCoefficient = newBiomCoefficient;
    }

    public void setWaterAmount(float waterAmount) {
        this.waterAmount = waterAmount;
    }

    public void setRiversAmount(int riversAmount) {
        this.riversAmount = riversAmount;
    }

    public void setEqualBioms(boolean equalBioms) {
        this.equalBioms = equalBioms;
    }

    public GameMap createMap() {
        generating.set(true);
        notifySubscribers(GenerationProcess.START, 0f, 0f);
        TimeCounter timeCounter = new TimeCounter("map generation");
        System.out.println(width + "x" + height + " (" + width * height + " cells)");

        switch (waterAlgorithm) {
            case SEPARATE_FROM_BIOM:
                createWater();
                bioms.remove(Biom.WATER);
                break;
            case NO_WATER:
                bioms.remove(Biom.WATER);
                break;
            case MIXED:
                createBioms();
                if (!generating.get()) {
                    notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
                    return null;
                }
                createWater();
                break;
            case SMALL_ISLANDS:
                createWaterWithSmallIslands();
                bioms.remove(Biom.WATER);
                break;
            case SUPER_MIXED:
                createWaterWithSmallIslands();
                if (!generating.get()) {
                    notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
                    return null;
                }
                createWater();
                break;

        }
        System.out.println(timeCounter.getCount("water creation"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        createBioms();
        System.out.println(timeCounter.getCountFromPrevious("bioms creation"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        createRivers();
        System.out.println(timeCounter.getCountFromPrevious("rivers creation"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        denoiseBioms();
        System.out.println(timeCounter.getCountFromPrevious("bioms denoising"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        createRelief();
        System.out.println(timeCounter.getCountFromPrevious("relief creation"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        createCities();
        System.out.println(timeCounter.getCountFromPrevious("cities creation"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        createDungeons();
        System.out.println(timeCounter.getCountFromPrevious("dungeons creation"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        createRoads();
        System.out.println(timeCounter.getCountFromPrevious("roads creation"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        createDeepOfWater();
        System.out.println(timeCounter.getCountFromPrevious("deep of water creation"));

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        createLevels();
        System.out.println(timeCounter.getCountFromPrevious("leveling cells"));
        System.out.println(timeCounter.getCount());

        if (!generating.get()) {
            notifySubscribers(GenerationProcess.INTERRUPTED, 0, 0);
            return null;
        }
        notifySubscribers(GenerationProcess.FINISH, 0f, 0f);

        return map;
    }

    public void interrupt() {
        generating.set(false);
    }

    // Passing a list of bioms allows to use specific bioms on created map
    private void fillBiomAmountMap(List<Biom> availableBioms) {
        availableBioms.forEach(biom -> biomAmount.put(biom, 0));
    }

    private List<Point> createStartPoints() {
        List<Point> points = new ArrayList<>();
        // point 1
        int y = (int) (height / 4 + floor(random(height / 5 * (-1), height / 5)));
        int x = (int) (width / 4 + floor(random(width / 5 * (-1), width / 5)));
        points.add(new Point(x, y));
        // point 2
        y = (int) (height - height / 4 + floor(random(height / 5 * (-1), height / 5)));
        x = (int) (width / 4 + floor(random(width / 5 * (-1), width / 5)));
        points.add(new Point(x, y));
        // point 3
        y = (int) (height / 4 + floor(random(height / 5 * (-1), height / 5)));
        x = (int) (width - width / 4 + floor(random(width / 5 * (-1), width / 5)));
        points.add(new Point(x, y));
        // point 4
        y = (int) (height - height / 4 + floor(random(height / 5 * (-1), height / 5)));
        x = (int) (width - width / 4 + floor(random(width / 5 * (-1), width / 5)));
        points.add(new Point(x, y));
        return points;
    }

    private Set<Zone> divideMapToZones() {
        Set<Zone> zones = new HashSet<>();
        int zonesAmountX = ((int) (Math.log(width) / Math.log(2)));
        int zonesAmountY = ((int) (Math.log(height) / Math.log(2)));
        int zoneWidth = width / zonesAmountX;
        int zoneHeight = height / zonesAmountY;

        if (width % zoneWidth != 0)
            zonesAmountX++;
        if (height % zoneHeight != 0)
            zonesAmountY++;

        for (int x = 0; x <= zonesAmountX; x++) {
            for (int y = 0; y <= zonesAmountY; y++) {
                int endX = x * zoneWidth + zoneWidth;
                if (endX > width - 1) {
                    endX = width - 1;
                } else if (x == zonesAmountX && endX < width - 1) {
                    endX = width - 1;
                }
                int endY = y * zoneHeight + zoneHeight;
                if (endY > height - 1) {
                    endY = height - 1;
                } else if (y == zonesAmountY && endY < height - 1) {
                    endY = height - 1;
                }
                zones.add(new Zone(x * zoneWidth, y * zoneHeight, endX, endY));
            }
        }

        return zones;
    }

    private Biom randomBiom() {
        return bioms.get(random(0, bioms.size() - 1));
    }

    private Relief randomRelief() {
        return reliefs.get(random(0, reliefs.size() - 1));
    }


    private void increaseBiomAmount(Cell cell) {
        if (cell.getBiom() == Biom.WATER) {
            waterCells.add(cell);
        }
        biomAmount.put(cell.getBiom(), biomAmount.get(cell.getBiom()) + 1);
    }

    // looking for cells around, then everywhere
    private List<Cell> findUnSteppedCells(Cell cell, CellProperty property) {
        List<Cell> result = new ArrayList<>();

        // close cells
        for (int i = 1; i <= 2; i++) {
            for (Cell aroundCell : cell.getAroundCells(i)) {
                if (aroundCell.hasNot(property))
                    result.add(aroundCell);
            }
            if (!result.isEmpty()) return result;
        }

        // far cells from zones
        Zone zone = zones.get(property).stream()
                .filter(z -> z.hasCell(cell))
                .findFirst()
                .orElse(null);

        // if there is zone with unstepped cells and contains point
        if (zone != null) {
            for (int x = zone.getStartPoint().x; x <= zone.getEndPoint().x; x++) {
                for (int y = zone.getStartPoint().y; y <= zone.getEndPoint().y; y++) {
                    if (map.cell(x, y).hasNot(property)) {
                        result.add(map.cell(x, y));
                        return result;
                    }
                }
            }
            zones.get(property).remove(zone);
        }

        // else check through other zones
        Set<Zone> zonesToRemove = new HashSet<>();
        zIter:
        for (Zone z : zones.get(property)) {
            for (int x = z.getStartPoint().x; x <= z.getEndPoint().x; x++) {
                for (int y = z.getStartPoint().y; y <= z.getEndPoint().y; y++) {
                    if (map.cell(x, y).hasNot(property)) {
                        result.add(map.cell(x, y));
                        break zIter;
                    }
                }
            }
            zonesToRemove.add(z);
        }

        zones.get(property).removeAll(zonesToRemove);
        return result;
    }


    private boolean isChangeBiom(Biom lastBiom, int cellsWithNoBiomAmount) {
        if (equalBioms && biomAmount.get(lastBiom) > cellsWithNoBiomAmount / bioms.size()) {
            if (lastBiom == Biom.WATER && (waterAlgorithm == GeneratorWaterAlgorithm.MIXED || waterAlgorithm == GeneratorWaterAlgorithm.SUPER_MIXED))
                return random(1d) < newBiomCoefficient;
            else
                return true;
        }
        return random(1d) < newBiomCoefficient;
    }


    private void createBioms() {
        int nonNullCells = map.nonNullCells(CellProperty.BIOM);
        int steps = height * width - nonNullCells;
        int allSteps = steps;
        notifySubscribers(GenerationProcess.BIOME_CREATION, 0f, allSteps);
        int cellsWithNoBiomAmount = steps;
        Biom lastBiom;
        List<Point> points = createStartPoints();
        // generate random biom at start positions and 1 cell around:
        for (Point point : points) {
            Biom randomBiom = randomBiom();
            if (map.cell(point).getBiom() == null) // cause water can be at this cell already
                steps -= 1;
            map.cell(point).setBiom(randomBiom);
            increaseBiomAmount(map.cell(point));
            for (Cell aroundCell : map.cell(point).getAroundCells(1)) {
                if (aroundCell.getBiom() == null) // cause water can be at this cell already
                    steps -= 1;
                aroundCell.setBiom(randomBiom);
                increaseBiomAmount(aroundCell);
            }
        }
        // move through the map and generate bioms:
        while (steps > 0) {
            if (!generating.get()) {
                return;
            }
            notifySubscribers(GenerationProcess.BIOME_CREATION, allSteps - steps, allSteps);
            for (Point point : points) {
                lastBiom = map.cell(point).getBiom();
                // move to the next position:
                List<Cell> unSteppedCells = findUnSteppedCells(map.cell(point), CellProperty.BIOM);
                if (unSteppedCells.isEmpty())
                    return;
                Cell unSteppedCell = random(unSteppedCells);
                point.x = unSteppedCell.getX();
                point.y = unSteppedCell.getY();
                // if absolutely new biom creating:
                if (isChangeBiom(lastBiom, cellsWithNoBiomAmount)) {
                    // next biom will be a biom with less present amount
                    Biom biom = bioms.stream()
                            .min(Comparator.comparingInt(biomAmount::get))
                            .orElse(lastBiom);
                    steps -= 1;
                    map.cell(point).setBiom(biom);
                    increaseBiomAmount(map.cell(point));
                    continue;
                }
                // else creating biom based on around cells:
                // creating biom coefficients from cells around:
                // close cells:
                Map<Biom, Double> coefficients = map.cell(point).getAroundCells(1).stream()
                        .filter(cell -> cell.getBiom() != null)
                        .map(Cell::getBiom)
                        .collect(Collectors.toMap(Function.identity(), v -> 7d, Double::sum));
                // far cells:
                coefficients.putAll(map.cell(point).getAroundCells(2).stream()
                        .filter(cell -> cell.getBiom() != null)
                        .map(Cell::getBiom)
                        .collect(Collectors.toMap(Function.identity(), v -> 1d, Double::sum)));
                // extra coefficient to last biom if it presents around:
                if (coefficients.containsKey(lastBiom))
                    coefficients.put(lastBiom, coefficients.get(lastBiom) + 15);
                // get random biom based on coefficients:
                Biom biom = randomWithWeights(coefficients);
                if (biom == null)
                    biom = randomBiom();
                steps -= 1;
                map.cell(point).setBiom(biom);
                increaseBiomAmount(map.cell(point));
            }
        }
        notifySubscribers(GenerationProcess.BIOME_CREATION, allSteps, allSteps);
    }

    private void createWater() {
        int steps = (int) (height * width * waterAmount);
        int allSteps = steps;
        notifySubscribers(GenerationProcess.WATER_CREATION, 0f, allSteps);
        List<Point> points = createStartPoints();
        while (steps > 0) {
            if (!generating.get()) {
                return;
            }
            notifySubscribers(GenerationProcess.WATER_CREATION, allSteps - steps, allSteps);
            for (Point point : points) {
                steps--;
                Point nextPoint = Functions.random(map.cell(point).getAroundCells(1)).point();
                point.x = nextPoint.x;
                point.y = nextPoint.y;

                map.cell(point).setBiom(Biom.WATER);
                increaseBiomAmount(map.cell(point));
            }
        }
        notifySubscribers(GenerationProcess.WATER_CREATION, allSteps, allSteps);
    }


    private void createWaterWithSmallIslands() {
        int steps = (int) (height * width * waterAmount);
        int allSteps = steps;
        notifySubscribers(GenerationProcess.WATER_CREATION, 0f, allSteps);
        // creating start points for generation:
        List<Point> points = createStartPoints();
        // set start points to water:
        for (Point point : points) {
            map.cell(point).setBiom(Biom.WATER);
            increaseBiomAmount(map.cell(point));
            steps -= 1;
        }
        while (steps > 0) {
            if (!generating.get()) {
                return;
            }
            notifySubscribers(GenerationProcess.WATER_CREATION, allSteps - steps, allSteps);
            for (Point point : points) {
                // move to the next position:
                Point nextPoint = random(findUnSteppedCells(map.cell(point), CellProperty.BIOM)).point();
                if (nextPoint == null) return;
                point.x = nextPoint.x;
                point.y = nextPoint.y;
                // generating:
                // not every cell visited by points will generate water
                steps -= 1;
                if (random(0, 1) > 0.75)
                    continue;
                // water biom will be set to the current point or to the random amount of around cells:
                // current point:
                if (random(0d, 1d) < 0.1) {
                    map.cell(point).setBiom(Biom.WATER);
                    increaseBiomAmount(map.cell(point));
                    continue;
                }
                // random around cells:
                List<Cell> cellsToAddWater = map.cell(point).getAroundCells(1);
                int amount = random(0, cellsToAddWater.size() - 1);
                while (amount > 0) {
                    Cell cell = cellsToAddWater.get(random(0, cellsToAddWater.size() - 1));
                    cellsToAddWater.remove(cell);
                    cell.setBiom(Biom.WATER);
                    increaseBiomAmount(cell);
                    amount--;
                }
            }
        }
        notifySubscribers(GenerationProcess.WATER_CREATION, allSteps, allSteps);
    }

    private void createRivers() {
        notifySubscribers(GenerationProcess.RIVERS_CREATION, 0f, riversAmount);
        MapPathFinder pathFinder = new MapPathFinder(map, new MapRiverScorer());
        for (int i = 0; i < riversAmount; i++) {
            if (!generating.get()) {
                return;
            }
            notifySubscribers(GenerationProcess.RIVERS_CREATION, i, riversAmount);
            Cell from = map.cell(Functions.random(width - 1), Functions.random(height - 1));
            Cell to;
            if (!waterCells.isEmpty())
                to = Functions.random(waterCells);
            else
                to = map.cell(Functions.random(width - 1), Functions.random(height - 1));
            pathFinder.find(from, to).ifPresent(cells -> cells.forEach(cell -> {
                cell.setBiom(Biom.WATER);
                waterCells.add(cell);
            }));
        }
        notifySubscribers(GenerationProcess.RIVERS_CREATION, 100, riversAmount);
    }

    private void createDeepOfWater() {
        int allSteps = waterCells.size();
        int currentStep = 0;
        notifySubscribers(GenerationProcess.DEPTH_OF_WATER_CREATION, 0, allSteps);
        for (Cell cell : waterCells) {

            if (!generating.get()) {
                return;
            }

            WaterDepth deepProperty;

            if (cell.getAroundCells(1).stream()
                    .anyMatch(c -> c.getBiom() != Biom.WATER)) {
                deepProperty = WaterDepth.SMALL;
            } else if (cell.getArea(4).stream()
                    .filter(c -> c.getBiom() != Biom.WATER)
                    .count() < 2) {
                deepProperty = WaterDepth.DEEP;
            } else {
                deepProperty = WaterDepth.NORMAL;
            }

            cell.setWaterDepth(deepProperty);
            currentStep++;
            notifySubscribers(GenerationProcess.DEPTH_OF_WATER_CREATION, currentStep, allSteps);
        }
        notifySubscribers(GenerationProcess.DEPTH_OF_WATER_CREATION, allSteps, allSteps);
    }

    private void createRelief() {
        int steps = height * width;
        int allSteps = steps;
        notifySubscribers(GenerationProcess.RELIEF_CREATION, 0f, allSteps);
        List<Point> points = createStartPoints();
        Relief lastRelief;
        // generate random relief at start points:
        for (Point point : points) {
            Relief randomRelief = randomRelief();
            map.cell(point).setRelief(randomRelief);
            steps--;
            for (Cell aroundCell : map.cell(point).getAroundCells(1)) {
                aroundCell.setRelief(randomRelief);
                steps--;
            }
        }
        while (steps > 0) {
            if (!generating.get()) {
                return;
            }
            notifySubscribers(GenerationProcess.RELIEF_CREATION, allSteps - steps, allSteps);
            for (Point point : points) {
                // moving through the map:
                lastRelief = map.cell(point).getRelief();
                Cell nextCell = random(findUnSteppedCells(map.cell(point), CellProperty.RELIEF));
                point.x = nextCell.getX();
                point.y = nextCell.getY();
                // generating relief:
                // if random relief creating:
                if (random(1d) < newReliefCoefficient) {
                    map.cell(point).setRelief(random(reliefs));
                    steps--;
                    continue;
                }
                // else creating relief based on around cells:
                // creating relief coefficients from cells around:
                // close cells:
                Map<Relief, Double> coefficients = map.cell(point).getAroundCells(1).stream()
                        .filter(cell -> cell.getRelief() != null)
                        .map(Cell::getRelief)
                        .collect(Collectors.toMap(Function.identity(), v -> 7d, Double::sum));
                // far cells:
                coefficients.putAll(map.cell(point).getAroundCells(2).stream()
                        .filter(cell -> cell.getRelief() != null)
                        .map(Cell::getRelief)
                        .collect(Collectors.toMap(Function.identity(), v -> 1d, Double::sum)));
                // extra coefficient to last relief if it presents around:
                if (coefficients.containsKey(lastRelief))
                    coefficients.put(lastRelief, coefficients.get(lastRelief) + 15);
                // get random relief based on coefficients:
                Relief relief = randomWithWeights(coefficients);
                if (relief == null)
                    relief = randomRelief();
                steps -= 1;
                map.cell(point).setRelief(relief);
            }
        }
        notifySubscribers(GenerationProcess.RELIEF_CREATION, allSteps, allSteps);
    }

    // reduce single biom cells
    private void denoiseBioms() {
        notifySubscribers(GenerationProcess.DENOISING, 0f, 1f);
        // denoising stops when next iteration do not denoise anything
        int denoisedAmount = 1;
        while (denoisedAmount > 0) {
            denoisedAmount = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (!generating.get()) {
                        return;
                    }
                    int sameBiomAroundAmount = 0;
                    int waterAround = 0;
                    Biom thisBiom = map.cell(x, y).getBiom();
                    // denoise only non water cells
                    if (thisBiom.equals(Biom.WATER))
                        continue;
                    List<Cell> aroundCells = map.cell(x, y).getAroundCells(1);
                    for (Cell cell : aroundCells) {
                        if (thisBiom.equals(cell.getBiom()))
                            sameBiomAroundAmount++;
                        else if (cell.getBiom().equals(Biom.WATER))
                            waterAround++;
                    }
                    // denoise if there are no same bioms around and this cell is not one-cell island
                    if (waterAround < 6 && sameBiomAroundAmount == 0) {
                        denoisedAmount++;
                        Biom biom = random(aroundCells).getBiom();
                        map.cell(x, y).setBiom(biom);
                        if (biom == Biom.WATER) {
                            waterCells.add(map.cell(x, y));
                        }
                    }
                }
            }
        }
        notifySubscribers(GenerationProcess.DENOISING, 1f, 1f);
    }


    private void createCities() {
        int currentID = mapObjectsPoints.size();
        List<Point> startingPoints = new ArrayList<>();

        // create starting points for cities
        while (startingPoints.size() < citiesAmount) {
            if (!generating.get()) {
                return;
            }
            List<Point> newPoints = createStartPoints().stream()
                    .filter(newPoint -> {
                        Set<Point> existingPoints = new HashSet<>(startingPoints);
                        startingPoints.forEach(sp -> map.cell(sp).getArea(2).forEach(cell -> existingPoints.add(cell.point())));
                        return !existingPoints.contains(newPoint);
                    })
                    .collect(Collectors.toList());
            startingPoints.addAll(newPoints);
        }

        // create cities
        for (int i = 0; i < citiesAmount; i++) {
            notifySubscribers(GenerationProcess.CITIES_CREATION, i, citiesAmount);
            Point point = startingPoints.get(i);
            while (map.cell(point).getBiom() == Biom.WATER) {
                List<Cell> unsteppedCells = findUnSteppedCells(map.cell(point), CellProperty.OBJECT);
                point = Functions.random(unsteppedCells).point();
            }
            City city = createCity(currentID);
            map.cell(point).setObject(city);
            currentID++;
            cities.add(city);
            mapObjectsPoints.add(point);
        }

        // create start point
        Point startPoint = mapObjectsPoints.stream()
                .map(map::cell)
                .filter(cell -> cell.getObject() instanceof City)
                .flatMap(cell -> cell.getAroundCells(1).stream())
                .filter(cell -> !cell.getBiom().equals(Biom.WATER))
                .map(Cell::point)
                .collect(randomCollector());

        map.setStartPoint(startPoint);

        notifySubscribers(GenerationProcess.CITIES_CREATION, citiesAmount, citiesAmount);
    }

    private City createCity(int id) {
        return new City(id);
    }

    private void createDungeons() {
        int startID = mapObjectsPoints.size();
        for (int i = 0; i < dungeonsAmount; i++) {
            if (!generating.get()) {
                return;
            }
            notifySubscribers(GenerationProcess.DUNGEONS_CREATION, i, dungeonsAmount);
            Dungeon currentDungeon = new Dungeon(startID + i);
            boolean notAllowedToPlace = true;
            int tries = 0;
            while (notAllowedToPlace) {
                if (tries > 100) {
                    break;
                }
                Cell cell = map.cell(random(width), random(height));
                if (cell.getObject() == null
                        && cell.getArea(2).stream().noneMatch(c -> c.getObject() != null)) {
                    notAllowedToPlace = false;
                    tries = 0;
                    cell.setObject(currentDungeon);
                    dungeons.add(currentDungeon);
                    mapObjectsPoints.add(cell.point());
                }
                tries++;
            }
        }
        notifySubscribers(GenerationProcess.DUNGEONS_CREATION, dungeonsAmount, dungeonsAmount);
    }

    private void createRoads() {
        MapPathFinder pathFinder = new MapPathFinder(map, new MapRoadScorer(),
                (to, from) -> from.getBiom() != Biom.WATER || from.getObject() == null);

        List<Point> remainedObjects = mapObjectsPoints.stream()
                .filter(
                        point -> {
                            MapObjectRoadAvailability availability = map.cell(point).getObject().roadAvailability();
                            return map.cell(point).getBiom() != Biom.WATER
                                    && (availability == MapObjectRoadAvailability.MUST
                                    || (availability == MapObjectRoadAvailability.CAN && randomBoolean()));
                        }
                ).collect(Collectors.toList());

        int objectsSize = remainedObjects.size();
        while (remainedObjects.size() > 2) {
            if (!generating.get()) {
                return;
            }
            notifySubscribers(GenerationProcess.ROADS_CREATION, objectsSize - remainedObjects.size(), objectsSize);
            Point first = remainedObjects.get(remainedObjects.size() - 1);
            Point second = remainedObjects.get(random(remainedObjects.size() - 1));
            pathFinder.find(map.cell(first), map.cell(second)).ifPresent(path -> {
                createRoad(path);
                remainedObjects.remove(second);
            });
        }
        notifySubscribers(GenerationProcess.ROADS_CREATION, objectsSize, objectsSize);
    }

    private void createRoad(List<Cell> route) {
        for (int i = 0; i < route.size(); i++) {
            Cell cell = route.get(i);
            Road road = cell.getRoad() == null ? new Road() : cell.getRoad();
            if (cell.getObject() != null) {
                road.addDestination(cell.getObject().getPosition());
            }
            if (i > 0) {
                Cell previousCell = route.get(i - 1);
                if (cell.getBiom() == Biom.WATER && previousCell.getBiom() == Biom.WATER) {
                    road.setRealRoad(false);
                } else {
                    CellSide destination = cell.getRelativeLocation(previousCell);
                    road.addDestination(destination);
                }
            }
            if (i < route.size() - 1) {
                Cell nextCell = route.get(i + 1);
                if (cell.getBiom() == Biom.WATER && nextCell.getBiom() == Biom.WATER) {
                    road.setRealRoad(false);
                } else {
                    CellSide destination = cell.getRelativeLocation(nextCell);
                    road.addDestination(destination);
                }
            }
            cell.setRoad(road);
        }
    }

    private void createLevels() {
        notifySubscribers(GenerationProcess.LEVELING, 0f, width / 100f);
        for (int x = 0; x < width; x++) {
            notifySubscribers(GenerationProcess.LEVELING, x / 100f, width / 100f);
            for (int y = 0; y < height; y++) {
                if (!generating.get()) {
                    return;
                }
                int lvl;
                Cell cell = map.cell(x, y);
                if (cell.getObject() instanceof City || cell.getRoad() != null) {
                    lvl = Functions.random(1, 6);
                } else {
                    int howFarIsRoadOrCity = 5;
                    for (int i = 1; i < 5; i++) {
                        boolean hasRoadOrCity = cell.getAroundCells(i).stream()
                                .anyMatch(point -> point.getRoad() != null || point.getObject() instanceof City);
                        if (hasRoadOrCity) {
                            howFarIsRoadOrCity = i;
                            break;
                        }
                    }
                    lvl = Functions.random(Math.max(10 * howFarIsRoadOrCity - 20, 1), 10 * howFarIsRoadOrCity + 6);
                }
                cell.setLvl(lvl);
            }
        }
        notifySubscribers(GenerationProcess.LEVELING, width / 100f, width / 100f);
    }

    private void notifySubscribers(GenerationProcess process, float current, float max) {
        observers.forEach(observer -> observer.update(process, current, max));
    }
}
