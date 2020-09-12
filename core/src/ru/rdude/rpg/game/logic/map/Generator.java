package ru.rdude.rpg.game.logic.map;

import ru.rdude.rpg.game.logic.map.aStarImpl.MapRoadScorer;
import ru.rdude.rpg.game.logic.map.bioms.Biom;
import ru.rdude.rpg.game.logic.map.bioms.Water;
import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.logic.map.objects.MapObject;
import ru.rdude.rpg.game.logic.map.reliefs.Relief;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.utils.aStar.AStarGraph;
import ru.rdude.rpg.game.utils.aStar.AStarRouteFinder;
import ru.rdude.rpg.game.utils.aStar.AStarScorer;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.floor;
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

    int test = 0;

    public enum WaterAlgorithm {AS_BIOM, SEPARATE_FROM_BIOM, NO_WATER}

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


    public Generator(int width, int height, List<Biom> bioms, List<Relief> reliefs, int citiesAmount, int dungeonsAmount) {
        this.width = width;
        this.height = height;
        this.bioms = new ArrayList<>();
        this.bioms.addAll(bioms);
        this.reliefs = new ArrayList<>();
        this.reliefs.addAll(reliefs);
        this.citiesAmount = citiesAmount;
        this.dungeonsAmount = dungeonsAmount;
        biomAmount = new HashMap<>();
        fillBiomAmountMap(bioms);
        map = new GameMap(width, height);
        newBiomCoefficient = 0.004;
        newReliefCoefficient = 0.3;
        waterAlgorithm = WaterAlgorithm.AS_BIOM;
        waterAmount = 0.01f;
        equalBioms = true;

        objects = new ArrayList<>();
        cities = new ArrayList<>();
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
        switch (waterAlgorithm) {
            case SEPARATE_FROM_BIOM:
                createWater();
                break;
            case NO_WATER:
                bioms.remove(Water.getInstance());
                break;
        }
        createBioms();
        denoiseBioms();
        createRelief();
        //createCities();
        //createRoads();

        test();

        return map;
    }

    private void test() {
        Point point1 = new Point(5, 5);
        Point point2 = new Point(12, 5);
        map.cell(point1).setRoad(new Road());
        map.cell(point2).setRoad(new Road());
        getAroundCells(point1, 1).stream()
                .filter(point ->
                        getRelativeLocation(point1, point) == CellSide.NN ||
                                getRelativeLocation(point1, point) == CellSide.SS ||
                                getRelativeLocation(point1, point) == CellSide.NE ||
                                getRelativeLocation(point1, point) == CellSide.NW ||
                                getRelativeLocation(point1, point) == CellSide.SE ||
                                getRelativeLocation(point1, point) == CellSide.SW
                )
                .forEach(point -> map.cell(point).setRoad(new Road()));

        getAroundCells(point2, 1).stream()
                .filter(point ->
                        getRelativeLocation(point2, point) == CellSide.NN ||
                                getRelativeLocation(point2, point) == CellSide.SS ||
                                getRelativeLocation(point2, point) == CellSide.NE ||
                                getRelativeLocation(point2, point) == CellSide.NW ||
                                getRelativeLocation(point2, point) == CellSide.SE ||
                                getRelativeLocation(point2, point) == CellSide.SW
                )
                .forEach(point -> map.cell(point).setRoad(new Road()));
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

        // 1/4 map zone cells cells
        int xStart = point.x < width / 2 ? 0 : width / 2;
        int xEnd = point.x < width / 2 ? width / 2 : width - 1;
        int yStart = point.y < height / 2 ? 0 : height / 2;
        int yEnd = point.y < height / 2 ? height / 2 : height - 1;
        for (int x = xStart; x < xEnd; x++) {
            for (int y = yStart; y < yEnd; y++) {
                if (map.cell(x, y).get(property) == null) {
                    result.add(new Point(x, y));
                    if (result.size() > 5)
                        return result;
                }
            }
        }
        if (!result.isEmpty())
            return result;

        // all cells
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (map.cell(x, y).get(property) == null) {
                    result.add(new Point(x, y));
                    return result;
                }
            }
        }
        return result;
    }

    private CellSide getRelativeLocation(Cell of, Cell to) {
        return getRelativeLocation(new Point(of.getX(), of.getY()), new Point(to.getX(), to.getY()));
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
        }
        else {
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
        // remove water from biom types list so generate bioms method won't generate it
        bioms.remove(water);
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
        createRoad(routeFinder, map.cell(5, 5), map.cell(31, 31));
        createRoad(routeFinder, map.cell(31, 5), map.cell(5, 31));
    }

    private void createRoad(AStarRouteFinder<Cell> routeFinder, Cell from, Cell to) {
        List<Cell> route = routeFinder.findRoute(from, to);
        for (int i = 0; i < route.size() - 1; i++) {
            Cell cell = route.get(i);
            cell.setRoad(new Road());
        }
    }


    /*

func Create_Dungeons():
	Dungeons_Amount = (Map_Width*Map_Height)/76
	var Dungeons_Left = Dungeons_Amount
	var Correct_Place = false
	var x
	var y
	while Dungeons_Left > 0:
		Correct_Place = false
		while Correct_Place == false: # Looking for correct place for dungeon
			randomize()
			x = floor(rand_range(0, Map_Width))
			y = floor(rand_range(0, Map_Height))
			if Map_Global[x][y][4] != 1:
				Map_Global[x][y][2] = 2 # Creating a dungeon
				var Around_Cells = Get_Around_Cells(x, y, 0, 1, 'Cell')
				for cell in Around_Cells:
					Map_Global[cell[0]][cell[1]][4] = 1 # Making closest cells unable to put dungeons to
				Around_Cells = null
				Around_Cells = Get_Around_Cells(x, y, 0, 2, 'Cell')
				for cell in Around_Cells:
					Map_Global[cell[0]][cell[1]][4] = 1 # Making 2nd close cells unable to put dungeons to
				Dungeons_Left -= 1
				Correct_Place = true



func Initialize_Astar():
	var TheID = 0
	var Weight = 1
	# Add points:
	for x in Map_Width:
		for y in Map_Height:
			if Map_Global[x][y][0] > 1: # Dont add water cells
				if Map_Global[x][y][2] == 0: # Add cells without cityes or dungeons
					if Map_Global[x][y][4] == 0: # Weight of the point if no dungeons near
						if Map_Global[x][y][1] == 2: # Forest
							Weight = 5
						elif Map_Global[x][y][1] == 3: # Hills
							Weight = 6
						elif Map_Global[x][y][1] == 4: # Mountain
							Weight == 7
						else: # Plain
							Weight = 4
					else:
						Weight = 50 # Weight of the point if dungeons near
					Astar_Node.add_point(TheID, Vector3(x, y, 0), Weight)
					TheID += 1
					pass
				pass
	# Connect points:
	var All_Points = Astar_Node.get_points()
	var Point_Position
	var x
	var y
	for Point in All_Points:
		Point_Position = Astar_Node.get_point_position(Point)
		x = Point_Position[0]
		y = Point_Position[1]
		var Around_Cells = Get_Around_Cells(x, y, 0, 1, 'Cell')
		for cell in Around_Cells:
			if Map_Global[cell[0]][cell[1]][0] > 1: # Only non water cells
				if Map_Global[cell[0]][cell[1]][2] == 0: # Cells without dungeons and cyties
					var CellID = Astar_Node.get_closest_point(Vector3(cell[0], cell[1], 0))
					Astar_Node.connect_points(Point, CellID, true)
				pass
			pass


func Find_Astar_Points_Near_City(city, Is_It_Second):
	var x = city['x']
	var y = city['y']
	var Around_Cells = Get_Around_Cells(x, y, 0, 1, 'Cell')
	var City_Around_Cells_Amount = 0 # Becaouse we cant connect center cell with road if there is no empty space around
	var City_Usefull_Cells = []
	var Is_Point_Found = 0
	if Is_It_Second == 1:
		for cell in Around_Cells:
			if Map_Global[cell[0]][cell[1]][3] == 1:
				City_Usefull_Cells.append(cell)
				Is_Point_Found = 1
				break
		if Is_Point_Found == 0:
			for cell in Around_Cells:
				var Around_Cells2 = Get_Around_Cells(cell[0], cell[1], 0, 1, 'Cell')
				for cell2 in Around_Cells2:
					if Map_Global[cell2[0]][cell2[1]][3] == 1:
						City_Usefull_Cells.append(cell2)
						Is_Point_Found = 1
						break
				if Is_Point_Found == 1:
					break
	if Is_It_Second == 0 or Is_Point_Found == 0:
		for cell in Around_Cells:
			if Map_Global[cell[0]][cell[1]][2] == 1:
				City_Around_Cells_Amount += 1
				var Available_cells_for_road = 0 # Cant connect city cell if there is no empty space around
				var Around_Cells2 = Get_Around_Cells(cell[0], cell[1], 0, 1, 'Cell')
				for cell2 in Around_Cells2:
					if Map_Global[cell2[0]][cell2[1]][0] > 1 && Map_Global[cell2[0]][cell2[1]][2] == 0:
						Available_cells_for_road += 1
				if Available_cells_for_road > 0:
					City_Usefull_Cells.append(cell)
	if City_Around_Cells_Amount < 6:
		City_Usefull_Cells.append([x, y])
	randomize()
	var City_Starting_Cell = City_Usefull_Cells[floor(rand_range(0, City_Usefull_Cells.size()))]
	var Road_Starting_Point = Astar_Node.get_closest_point(Vector3(City_Starting_Cell[0], City_Starting_Cell[1], 0))
	return Road_Starting_Point


func Create_Roads():
	Initialize_Astar()
	var Roads = []
	var Current_Road
	# First iteration:
	for city in Cityes:
		if city['Connected'] < 2:
			var Start_Point = Find_Astar_Points_Near_City(city, 0)
			randomize()
			var Second_city
			var AnotherCity = 0
			while AnotherCity == 0:
				Second_city = Cityes[floor(rand_range(0, Cityes.size()))]
				if Second_city['x'] == city['x'] && Second_city['y'] == city['y']:
					AnotherCity = 0
					print('Trying to connect same city')
				else:
					AnotherCity = 1
			var Finish_Point = Find_Astar_Points_Near_City(Second_city, 1)
			var Road_Path = Astar_Node.get_point_path(Start_Point, Finish_Point)
			if Road_Path.size() != 0:
				var Is_Cityes_Connected = false
				for ID_Search in Second_city['Connected_With']:
					if city['ID'] == ID_Search:
						Is_Cityes_Connected = true
						break
				if Is_Cityes_Connected == false:
					city['Connected'] += 1
					Second_city['Connected'] += 1
					city['Connected_With'].append(Second_city['ID'])
					Second_city['Connected_With'].append(city['ID'])
					for cell in Road_Path:
						Map_Global[cell[0]][cell[1]][3] = 1
						var CellID = Astar_Node.get_closest_point(Vector3(cell[0], cell[1], 0))
						Astar_Node.set_point_weight_scale(CellID, 1)
			else:
				print('This 2 cityes can not be connected')
	# Second iteration:
	for city in Cityes:
		var Need_to_Connect_With = []
		for id in Cityes.size():
			Need_to_Connect_With.append(id - 1)

*/
}
