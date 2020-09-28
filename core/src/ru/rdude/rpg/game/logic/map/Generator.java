package ru.rdude.rpg.game.logic.map;

import ru.rdude.rpg.game.logic.map.aStarImpl.MapRiverScorer;
import ru.rdude.rpg.game.logic.map.aStarImpl.MapRoadScorer;
import ru.rdude.rpg.game.logic.map.bioms.Biom;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.logic.map.objects.MapObject;
import ru.rdude.rpg.game.logic.map.reliefs.Relief;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.TimeCounter;
import ru.rdude.rpg.game.utils.aStar.AStarGraph;
import ru.rdude.rpg.game.utils.aStar.AStarRouteFinder;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static ru.rdude.rpg.game.utils.Functions.random;
import static ru.rdude.rpg.game.utils.Functions.randomWithWeights;

/*
# Bioms (z = 0):
# 0 - empty
# 1 - water
# 2 - grass
# 3 - dirt
# 4 - sand
# 5 - snow
# 6 - swamp
# 7 - jungle
# 8 - deadland
# 9 - volcanic

# Reliefs (z = 1):
# 1 - plain
# 2 - forest
# 3 - hills
# 4 - mountain

# Objects (z = 2):
# 0 - no object
# 1 - city
# 2 - dungeon

# Roads (z = 3)
var Cell_Propertyes_Count = 5 # This is depth of the array
# 0 - biom type, 1 - relief, 2 - object, 3 - road, 4 - helper for placing dungeons
 */
public class Generator {

    public enum WaterAlgorithm {AS_BIOM, SEPARATE_FROM_BIOM, MIXED, SMALL_ISLANDS, SUPER_MIXED, NO_WATER}

    private GameMap map;
    private int width; // 64
    private int height; // 64
    private List<Biom> bioms; // available bioms
    private List<Relief> reliefs; // available reliefs

    private int citiesAmount; //  Need to be smaller on smaller maps
    private int dungeonsAmount;
    private List<MapObject> objects;
    private List<City> cities;
    private Map<Biom, Integer> biomAmount;
    // next parameters can be set directly with setter after creating Generator. Instead - default values:
    private double newBiomCoefficient; // chance to generate new biom instead of surrounding bioms
    private double newReliefCoefficient; // same with relief
    private boolean equalBioms; // generation type where biom trying to be equal size

    private WaterAlgorithm waterAlgorithm;
    private float waterAmount; // works only with separate water algorithm
    private int riversAmount;

    // zones help optimize finding unstepped cells on big maps
    private Map<CellProperty.Type, Set<Zone>> zones;


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
        for (CellProperty.Type property : CellProperty.Type.values()) {
            zones.put(property, divideMapToZones());
        }
        biomAmount = new HashMap<>();
        fillBiomAmountMap(bioms);
        map = new GameMap(width, height);
        newBiomCoefficient = 0.004;
        newReliefCoefficient = 0.3;
        waterAlgorithm = WaterAlgorithm.MIXED;
        waterAmount = 0.33f;
        riversAmount = 10;

        equalBioms = true;

        objects = new ArrayList<>();
        cities = new ArrayList<>();
    }

    public void setSize(GameMapSize size) {
        this.width = size.getWidth();
        this.height = size.getHeight();
    }

    public void setWaterAlgorithm(WaterAlgorithm waterAlgorithm) {
        this.waterAlgorithm = waterAlgorithm;
    }

    public void setNewBiomCoefficient(double newBiomCoefficient) {
        this.newBiomCoefficient = newBiomCoefficient;
    }

    public void setEqualBioms(boolean equalBioms) {
        this.equalBioms = equalBioms;
    }

    public GameMap createMap() {
        TimeCounter timeCounter = new TimeCounter("map generation");
        System.out.println(width + "x" + height + " (" + width*height + " cells)");

        switch (waterAlgorithm) {
            case SEPARATE_FROM_BIOM:
                createWater();
                bioms.remove(Water.getInstance());
                break;
            case NO_WATER:
                bioms.remove(Water.getInstance());
                break;
            case MIXED:
                createBioms();
                createWater();
                break;
            case SMALL_ISLANDS:
                createWaterWithSmallIslands();
                bioms.remove(Water.getInstance());
                break;
            case SUPER_MIXED:
                createWaterWithSmallIslands();
                createWater();
                break;

        }
        System.out.println(timeCounter.getCount("water creation"));


        createBioms();
        System.out.println(timeCounter.getCountFromPrevious("bioms creation"));
        denoiseBioms();
        System.out.println(timeCounter.getCountFromPrevious("bioms denoising"));
        createRivers();
        System.out.println(timeCounter.getCountFromPrevious("rivers creation"));
        createRelief();
        System.out.println(timeCounter.getCountFromPrevious("relief creation"));
        createCities();
        System.out.println(timeCounter.getCountFromPrevious("cities creation"));
        createRoads();
        System.out.println(timeCounter.getCountFromPrevious("roads creation"));
        createDeepOfWater();
        System.out.println(timeCounter.getCountFromPrevious("deep of water creation"));

        System.out.println(timeCounter.getCount());

        return map;
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
        int zonesAmountX = ((int) (Math.log(width)/Math.log(2)));
        int zonesAmountY = ((int) (Math.log(height)/Math.log(2)));
        int zoneWidth = width / zonesAmountX;
        int zoneHeight = height / zonesAmountY;

        if (width % zoneWidth != 0)
            zonesAmountX++;
        if (height % zoneHeight != 0)
            zonesAmountY++;

        for (int x = 0; x <= zonesAmountX; x++) {
            for (int y = 0; y <= zonesAmountY; y++) {
                int endX = x * zoneWidth + zoneWidth;
                if (endX > width - 1)
                    endX = width - 1;
                int endY = y * zoneHeight + zoneHeight;
                if (endY > height - 1)
                    endY = height - 1;
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


    private void increaseBiomAmount(Biom biom) {
        biomAmount.put(biom, biomAmount.get(biom) + 1);
    }

    private List<Point> getAroundCells(int x, int y, int closeness) {
        return getAroundCells(new Point(x, y), closeness);
    }

    // get cells in closeness radius. Closeness 1 and 2 implemented by
    // old implementation. This method still uses old one with closeness 1 though
    private List<Point> getAroundCells(Point point, int closeness) {
        if (closeness == 1 || closeness == 2)
            return getCloseAroundCells(point, closeness);

        Set<Point> checkedPoints = new HashSet<>();
        Set<Point> nextCheckingPoints = new HashSet<>();
        checkedPoints.add(point);
        nextCheckingPoints.add(point);

        for (int i = 0; i < closeness; i++) {
            Set<Point> currentPoints = new HashSet<>(nextCheckingPoints);
            nextCheckingPoints.clear();
            for (Point currentPoint : currentPoints) {
                List<Point> aroundCells = getAroundCells(currentPoint, 1).stream()
                        .filter(p -> !checkedPoints.contains(p))
                        .collect(Collectors.toList());
                checkedPoints.addAll(aroundCells);
                nextCheckingPoints.addAll(aroundCells);
            }
            if (i == closeness - 1)
                return new ArrayList<>(nextCheckingPoints);
        }
        return new ArrayList<>();
    }

    private List<Point> getArea(int x_center, int y_center, int radius) {
        return getArea(new Point(x_center, y_center), radius);
    }

    private List<Point> getArea(Point center, int radius) {
        List<Point> result = new ArrayList<>();
        for (int i = 1; i <= radius; i++) {
            result.addAll(getAroundCells(center, i));
        }
        return result;
    }

    // get points around given point
    // closeness - how close this points to given cell (1 - short radius, 2 - huge radius)
    private List<Point> getCloseAroundCells(Point point, int closeness) {
        if (closeness > 2 || closeness < 1)
            throw new IllegalArgumentException("closeness in AroundCells() method is wrong");
        List<Point> aroundCells = new ArrayList<>();
        int x = point.x;
        int y = point.y;
        if (closeness == 1) {
            if (point.x > 0)
                aroundCells.add(new Point(x - 1, y));
            if (point.y > 0)
                aroundCells.add(new Point(x, y - 1));
            if (x < width - 1)
                aroundCells.add(new Point(x + 1, y));
            if (y < height - 1)
                aroundCells.add(new Point(x, y + 1));
            if (x % 2 == 0 && (x < width - 1) && (y < height - 1))
                aroundCells.add(new Point(x + 1, y + 1));
            else if ((x < width - 1) && y > 0)
                aroundCells.add(new Point(x + 1, y - 1));
            if (x % 2 == 0 && x > 0 && y < height - 1)
                aroundCells.add(new Point(x - 1, y + 1));
            else if (x > 0 && y > 0)
                aroundCells.add(new Point(x - 1, y - 1));
        } else if (closeness == 2) {
            if (x % 2 == 0 && x > 0 && y > 0)
                aroundCells.add(new Point(x - 1, y - 1));
            else if (x > 0 && y > 1)
                aroundCells.add(new Point(x - 1, y - 2));
            if (y > 1)
                aroundCells.add(new Point(x, y - 2));
            if (x % 2 == 0 && x < width - 1 && y > 1)
                aroundCells.add(new Point(x + 1, y - 1));
            else if (x < width - 1 && y > 2)
                aroundCells.add(new Point(x + 1, y - 2));
            if (x > 1 && y > 0)
                aroundCells.add(new Point(x - 2, y - 1));
            if (x < width - 2 && y > 0)
                aroundCells.add(new Point(x + 2, y - 1));
            if (x > 1)
                aroundCells.add(new Point(x - 2, y));
            if (x < width - 2)
                aroundCells.add(new Point(x + 2, y));
            if (x > 1 && y < height - 1)
                aroundCells.add(new Point(x - 2, y + 1));
            if (x < width - 2 && y < height - 1)
                aroundCells.add(new Point(x + 2, y + 1));
            if (y < height - 2)
                aroundCells.add(new Point(x, y + 2));
            if (x % 2 == 0 && x < width - 1 && y < height - 2)
                aroundCells.add(new Point(x + 1, y + 2));
            else if (x < width - 1 && y < height - 1)
                aroundCells.add(new Point(x + 1, y + 1));
            if (x % 2 == 0 && x > 0 && y < height - 2)
                aroundCells.add(new Point(x - 1, y + 2));
            else if (x > 0 && y < height - 1)
                aroundCells.add(new Point(x - 1, y + 1));
        }
        return aroundCells;
    }


    // looking for cells around, then everywhere
    private List<Point> findUnSteppedCells(Point point, CellProperty.Type property) {
        List<Point> result = new ArrayList<>();

        // close cells
        for (int i = 1; i <= 2; i++) {
            for (Point aroundCell : getAroundCells(point, i)) {
                if (map.cell(aroundCell).get(property) == null)
                    result.add(aroundCell);
            }
            if (!result.isEmpty()) return result;
        }

        // far cells from zones
        Zone zone = zones.get(property).stream()
                .filter(z -> z.hasPoint(point))
                .findFirst()
                .orElse(null);

        // if there is zone with unstepped cells and contains point
        if (zone != null) {
            for (int x = zone.getStartPoint().x; x <= zone.getEndPoint().x; x++) {
                for (int y = zone.getStartPoint().y; y <= zone.getEndPoint().y; y++) {
                    if (map.cell(x, y).get(property) == null) {
                        result.add(new Point(x, y));
                        return result;
                    }
                }
            }
            zones.get(property).remove(zone);
        }

        // else check through other zones
        Set<Zone> zonesToRemove = new HashSet<>();
        zIter: for (Zone z : zones.get(property)) {
            for (int x = z.getStartPoint().x; x <= z.getEndPoint().x; x++) {
                for (int y = z.getStartPoint().y; y <= z.getEndPoint().y; y++) {
                    if (map.cell(x, y).get(property) == null) {
                        result.add(new Point(x, y));
                        break zIter;
                    }
                }
            }
            zonesToRemove.add(z);
        }

        zones.get(property).removeAll(zonesToRemove);
        return result;
    }

    private CellSide getRelativeLocation(Cell from, Cell searching) {
        return getRelativeLocation(new Point(from.getX(), from.getY()), new Point(searching.getX(), searching.getY()));
    }

    // from which side of cell another cell locates
    private CellSide getRelativeLocation(Point from, Point searching) {
        if (from.x == searching.x && from.y + 1 == searching.y)
            return CellSide.NN;
        if (from.x == searching.x && from.y - 1 == searching.y)
            return CellSide.SS;

        if (from.x % 2 == 0) {
            if (from.x + 1 == searching.x && from.y + 1 == searching.y)
                return CellSide.NE;
            if (from.x + 1 == searching.x && from.y == searching.y)
                return CellSide.SE;
            if (from.x - 1 == searching.x && from.y == searching.y)
                return CellSide.SW;
            if (from.x - 1 == searching.x && from.y + 1 == searching.y)
                return CellSide.NW;
        } else {
            if (from.x + 1 == searching.x && from.y == searching.y)
                return CellSide.NE;
            if (from.x + 1 == searching.x && from.y - 1 == searching.y)
                return CellSide.SE;
            if (from.x - 1 == searching.x && from.y - 1 == searching.y)
                return CellSide.SW;
            if (from.x - 1 == searching.x && from.y == searching.y)
                return CellSide.NW;
        }
        return CellSide.NOT_RELATED;
    }


    private boolean isChangeBiom(Biom lastBiom, int cellsWithNoBiomAmount) {
        if (equalBioms && biomAmount.get(lastBiom) > cellsWithNoBiomAmount / bioms.size()) {
            if (lastBiom == Water.getInstance() && (waterAlgorithm == WaterAlgorithm.MIXED || waterAlgorithm == WaterAlgorithm.SUPER_MIXED))
                return random(1d) < newBiomCoefficient;
            else
                return true;
        }
        return random(1d) < newBiomCoefficient;
    }


    private void createBioms() {
        int nonNullCells = map.nonNullCells(CellProperty.Type.BIOM);
        int steps = height * width - nonNullCells;
        int cellsWithNoBiomAmount = steps;
        Biom lastBiom;
        List<Point> points = createStartPoints();
        // generate random biom at start positions and 1 cell around:
        for (Point point : points) {
            Biom randomBiom = randomBiom();
            if (map.cell(point).getBiom() == null) // cause water can be at this cell already
                steps -= 1;
            map.cell(point).setBiom(randomBiom);
            increaseBiomAmount(randomBiom);
            for (Point aroundCell : getAroundCells(point, 1)) {
                increaseBiomAmount(randomBiom);
                if (map.cell(point).getBiom() == null) // cause water can be at this cell already
                    steps -= 1;
                map.cell(aroundCell).setBiom(randomBiom);
            }
        }
        // move through the map and generate bioms:
        while (steps > 0) {
            for (Point point : points) {
                lastBiom = map.cell(point).getBiom();
                // move to the next position:
                List<Point> unSteppedCells = findUnSteppedCells(point, CellProperty.Type.BIOM);
                if (unSteppedCells.isEmpty())
                    return;
                Point unSteppedPoint = unSteppedCells.get(random(0, unSteppedCells.size() - 1));
                point.x = unSteppedPoint.x;
                point.y = unSteppedPoint.y;
                // if absolutely new biom creating:
                if (isChangeBiom(lastBiom, cellsWithNoBiomAmount)) {
                    // next biom will be a biom with less present amount
                    Biom biom = bioms.stream()
                            .min(Comparator.comparingInt(biom2 -> biomAmount.get(biom2)))
                            .orElse(lastBiom);
                    steps -= 1;
                    increaseBiomAmount(biom);
                    map.cell(point).setBiom(biom);
                    continue;
                }
                // else creating biom based on around cells:
                // creating biom coefficients from cells around:
                // close cells:
                Map<Biom, Double> coefficients = getAroundCells(point, 1).stream()
                        .filter(p -> map.cell(p).getBiom() != null)
                        .map(p -> map.cell(p).getBiom())
                        .collect(Collectors.toMap(Biom::getThisInstance, v -> 7d, Double::sum));
                // far cells:
                coefficients.putAll(getAroundCells(point, 2).stream()
                        .filter(p -> map.cell(p).getBiom() != null)
                        .map(p -> map.cell(p).getBiom())
                        .collect(Collectors.toMap(Biom::getThisInstance, v -> 1d, Double::sum)));
                // extra coefficient to last biom if it presents around:
                if (coefficients.containsKey(lastBiom))
                    coefficients.put(lastBiom, coefficients.get(lastBiom) + 15);
                // get random biom based on coefficients:
                Biom biom = randomWithWeights(coefficients);
                if (biom == null)
                    biom = randomBiom();
                steps -= 1;
                increaseBiomAmount(biom);
                map.cell(point).setBiom(biom);
            }
        }
    }

    private void createWater() {
        int steps = (int) (height * width * waterAmount);
        Water water = Water.getInstance();
        List<Point> points = createStartPoints();
        while (steps > 0) {
            for (Point point : points) {
                steps--;
                Point nextPoint = Functions.random(getAroundCells(point, 1));
                point.x = nextPoint.x;
                point.y = nextPoint.y;

                map.cell(point).setBiom(Water.getInstance());
            }
        }
    }


    private void createWaterWithSmallIslands() {
        int steps = (int) (height * width * waterAmount);
        Water water = Water.getInstance();
        // creating start points for generation:
        List<Point> points = createStartPoints();
        // set start points to water:
        for (Point point : points) {
            map.cell(point).setBiom(water);
            steps -= 1;
        }
        while (steps > 0) {
            for (Point point : points) {
                // move to the next position:
                Point nextPoint = random(findUnSteppedCells(point, CellProperty.Type.BIOM));
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
                    map.cell(point).setBiom(water);
                    continue;
                }
                // random around cells:
                List<Point> cellsToAddWater = getAroundCells(point, 1);
                int amount = random(0, cellsToAddWater.size() - 1);
                while (amount > 0) {
                    Point p = cellsToAddWater.get(random(0, cellsToAddWater.size() - 1));
                    cellsToAddWater.remove(p);
                    map.cell(p).setBiom(water);
                    amount--;
                }
            }
        }
    }

    private void createRivers() {
        Set<Cell> nodes = new HashSet<>();
        Map<Cell, Set<Cell>> connections = new HashMap<>();
        AStarScorer<Cell> scorer = new MapRiverScorer();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = map.cell(x, y);
                nodes.add(cell);
                Set<Cell> aroundCells = getAroundCells(x, y, 1)
                        .stream().map(map::cell)
                        .collect(Collectors.toSet());
                connections.put(cell, aroundCells);
            }
        }

        AStarGraph<Cell> graph = new AStarGraph<>(nodes, connections);
        AStarRouteFinder<Cell> routeFinder = new AStarRouteFinder<>(graph, scorer, scorer);

        for (int i = 0; i < riversAmount; i++) {
            Cell from = map.cell(Functions.random(width - 1), Functions.random(height - 1));
            Cell to;
            if (!map.getCellsWithProperty(Water.getInstance()).isEmpty())
                to = Functions.random(map.getCellsWithProperty(Water.getInstance()));
            else
                to = map.cell(Functions.random(width - 1), Functions.random(height - 1));
            routeFinder.findRoute(from, to).forEach(cell -> cell.setBiom(Water.getInstance()));
        }
    }

    private void createDeepOfWater() {
        for (Cell cell : map.getCellsWithProperty(Water.getInstance())) {

            Water.DeepProperty deepProperty;

            if (getAroundCells(cell.getX(), cell.getY(), 1).stream()
                    .anyMatch(point -> map.cell(point).getBiom() != Water.getInstance())) {
                deepProperty = Water.DeepProperty.SMALL;
            } else if (getArea(cell.getX(), cell.getY(), 4).stream()
                    .filter(point -> map.cell(point).getBiom() != Water.getInstance())
                    .count() < 2) {
                deepProperty = Water.DeepProperty.DEEP;
            } else {
                deepProperty = Water.DeepProperty.NORMAL;
            }

            cell.setDeepProperty(deepProperty);
        }
    }

    private void createRelief() {
        int steps = height * width;
        List<Point> points = createStartPoints();
        Relief lastRelief;
        // generate random relief at start points:
        for (Point point : points) {
            Relief randomRelief = randomRelief();
            map.cell(point).setRelief(randomRelief);
            steps--;
            for (Point aroundCell : getAroundCells(point, 1)) {
                map.cell(aroundCell).setRelief(randomRelief);
                steps--;
            }
        }
        while (steps > 0) {
            for (Point point : points) {
                // moving through the map:
                lastRelief = map.cell(point).getRelief();
                Point nextPoint = random(findUnSteppedCells(point, CellProperty.Type.RELIEF));
                point.x = nextPoint.x;
                point.y = nextPoint.y;
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
                Map<Relief, Double> coefficients = getAroundCells(point, 1).stream()
                        .filter(p -> map.cell(p).getRelief() != null)
                        .map(p -> map.cell(p).getRelief())
                        .collect(Collectors.toMap(Relief::getThisInstance, v -> 7d, Double::sum));
                // far cells:
                coefficients.putAll(getAroundCells(point, 2).stream()
                        .filter(p -> map.cell(p).getRelief() != null)
                        .map(p -> map.cell(p).getRelief())
                        .collect(Collectors.toMap(Relief::getThisInstance, v -> 1d, Double::sum)));
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
    }

    // reduce single biom cells
    private void denoiseBioms() {
        // denoising stops when next iteration do not denoise anything
        int denoisedAmount = 1;
        while (denoisedAmount > 0) {
            denoisedAmount = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int sameBiomAroundAmount = 0;
                    int waterAround = 0;
                    Biom thisBiom = map.cell(x, y).getBiom();
                    // denoise only non water cells
                    if (thisBiom.equals(Water.getInstance()))
                        continue;
                    List<Point> aroundCells = getAroundCells(x, y, 1);
                    for (Point point : aroundCells) {
                        if (thisBiom.equals(map.cell(point).getBiom()))
                            sameBiomAroundAmount++;
                        else if (map.cell(point).getBiom().equals(Water.getInstance()))
                            waterAround++;
                    }
                    // denoise if there are no same bioms around and this cell is not one-cell island
                    if (waterAround < 6 && sameBiomAroundAmount == 0) {
                        denoisedAmount++;
                        map.cell(x, y).setBiom(map.cell(random(aroundCells)).getBiom());
                    }
                }
            }
        }
    }


    private void createCities() {
        int currentID = objects.size();
        List<Point> startingPoints = new ArrayList<>();

        // create starting points for cities
        while (startingPoints.size() < citiesAmount) {
            List<Point> newPoints = createStartPoints().stream()
                    .filter(newPoint -> {
                        Set<Point> existingPoints = new HashSet<>(startingPoints);
                        startingPoints.forEach(sp -> {
                            existingPoints.addAll(getAroundCells(sp, 1));
                            existingPoints.addAll(getAroundCells(sp, 2));
                        });
                        return !existingPoints.contains(newPoint);
                    })
                    .collect(Collectors.toList());
            startingPoints.addAll(newPoints);
        }

        // create cities
        for (int i = 0; i < citiesAmount; i++) {
            Point point = startingPoints.get(i);
            while (map.cell(point).getBiom() == Water.getInstance()) {
                List<Point> unsteppedCells = findUnSteppedCells(point, CellProperty.Type.OBJECT);
                point = Functions.random(unsteppedCells);
            }
            City city = createCity(currentID);
            map.cell(point).setObject(city);
            currentID++;
            cities.add(city);
        }
    }

    private City createCity(int id) {
        return new City(id);
    }


    private void createRoads() {
        Set<Cell> nodes = new HashSet<>();
        Map<Cell, Set<Cell>> connections = new HashMap<>();
        AStarScorer<Cell> scorer = new MapRoadScorer();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = map.cell(x, y);
                nodes.add(cell);
                Set<Cell> aroundCells = getAroundCells(x, y, 1)
                        .stream().map(map::cell)
                        .collect(Collectors.toSet());
                connections.put(cell, aroundCells);
            }
        }

        AStarGraph<Cell> graph = new AStarGraph<>(nodes, connections);
        AStarRouteFinder<Cell> routeFinder = new AStarRouteFinder<>(graph, scorer, scorer);


        //test
        createRoad(routeFinder, map.cell(2, 2), map.cell(5, 5));
        createRoad(routeFinder, map.cell(2, 5), map.cell(5, 2));
    }

    private void createRoad(AStarRouteFinder<Cell> routeFinder, Cell from, Cell to) {
        List<Cell> route = routeFinder.findRoute(from, to);
        for (int i = 0; i < route.size() - 1; i++) {
            Cell cell = route.get(i);
            Road road = cell.getRoad() == null ? new Road() : cell.getRoad();
            if (i > 0) {
                CellSide destination = getRelativeLocation(cell, route.get(i - 1));
                road.addDestination(destination);
            }
            if (i < route.size() - 1) {
                CellSide destination = getRelativeLocation(cell, route.get(i + 1));
                road.addDestination(destination);
            }
            cell.setRoad(road);
        }
    }
}
