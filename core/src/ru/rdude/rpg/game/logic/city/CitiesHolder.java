package ru.rdude.rpg.game.logic.city;

import ru.rdude.rpg.game.logic.map.objects.City;
import ru.rdude.rpg.game.logic.map.objects.CityInside;

import java.util.HashMap;
import java.util.Map;

public class CitiesHolder {

    private Map<Long, CityInside> cities = new HashMap<>();

    public void addCity(City city) {
        cities.put(city.getId(), new CityInside(city));
    }

    public CityInside getCityById(long id) {
        return cities.get(id);
    }

}
