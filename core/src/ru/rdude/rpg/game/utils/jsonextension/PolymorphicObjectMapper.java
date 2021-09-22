package ru.rdude.rpg.game.utils.jsonextension;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PolymorphicObjectMapper extends ObjectMapper {

    protected final Map<String, Reflections> reflections;

    public PolymorphicObjectMapper(String... packages) {
        // reflections
        if (packages != null && packages.length > 0) {
            reflections = new HashMap<>();
            for (String p : packages) {
                reflections.put(p, new Reflections(p));
            }
            config();
        } else {
            reflections = null;
        }
        // mapper features
        setVisibility(
                getVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
    }

    private void config() {
        Set<Class<?>> classesForMixIn = new HashSet<>();
        for (Map.Entry<String, Reflections> entry : reflections.entrySet()) {
            Reflections reflection = entry.getValue();
            String pack = entry.getKey();
            for (Class<?> annotatedClass : reflection.getTypesAnnotatedWith(JsonPolymorphicSubType.class)) {
                final JsonPolymorphicSubType annotation = annotatedClass.getAnnotation(JsonPolymorphicSubType.class);
                registerSubtypes(new NamedType(annotatedClass, annotation.value()));
                final Set<Class<?>> allSuperclasses = getAllSuperclasses(annotatedClass);
                final Set<Class<?>> classesFromThisPackage = allSuperclasses.stream()
                        .filter(cl -> cl.getPackageName().startsWith(pack))
                        .collect(Collectors.toSet());
                classesForMixIn.addAll(classesFromThisPackage);
            }
        }
        classesForMixIn.forEach(superClass -> addMixIn(superClass, PolymorphicSuperclassMixIn.class));
    }

    private Set<Class<?>> getAllSuperclasses(Class<?> cl) {
        return getAllSuperclasses(cl, new HashSet<>());
    }

    private Set<Class<?>> getAllSuperclasses(Class<?> cl, Set<Class<?>> founded) {
        // classes
        final Class<?> superclass = cl.getSuperclass();
        if (superclass != null) {
            if (Modifier.isAbstract(superclass.getModifiers())) {
                founded.add(superclass);
            }
            getAllSuperclasses(superclass, founded);
        }
        // interfaces
        final Class<?>[] interfaces = cl.getInterfaces();
        if (interfaces.length > 0) {
            for (Class<?> anInterface : interfaces) {
                founded.add(anInterface);
                getAllSuperclasses(anInterface, founded);
            }
        }
        return founded;
    }

}
