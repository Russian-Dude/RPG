package ru.rdude.rpg.game.utils.jsonextension;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "polymorphicType")
final class PolymorphicSuperclassMixIn { }
