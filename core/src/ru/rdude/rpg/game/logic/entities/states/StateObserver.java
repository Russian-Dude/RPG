package ru.rdude.rpg.game.logic.entities.states;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface StateObserver<T> {

    void update(Set<T> current);

}
