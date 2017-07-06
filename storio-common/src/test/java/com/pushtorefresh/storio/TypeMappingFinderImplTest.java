package com.pushtorefresh.storio;

import com.pushtorefresh.storio.internal.TypeMapping;
import com.pushtorefresh.storio.internal.TypeMappingFinderImpl;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TypeMappingFinderImplTest {

    interface InterfaceEntity {
    }

    interface DescendantInterface extends InterfaceEntity {
    }

    static class ClassEntity {
    }

    static class DescendantClass extends ClassEntity implements InterfaceEntity {
    }

    @Test
    public void shouldReturnNullIfDirectTypeMappingIsEmpty() {
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(Collections.<Class<?>, TypeMapping<?>>emptyMap());

        assertThat(typeMappingFinder.findTypeMapping(ClassEntity.class)).isNull();
    }

    @Test
    public void shouldReturnNullIfDirectTypeMappingIsNull() {
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(null);

        assertThat(typeMappingFinder.findTypeMapping(ClassEntity.class)).isNull();
    }

    @Test
    public void shouldReturnNullIfNoTypeMappingRegisteredForType() {
        class ClassWithoutTypeMapping {
        }

        //noinspection unchecked
        final TypeMapping<ClassEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping =
                Collections.<Class<?>, TypeMapping<?>>singletonMap(ClassEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        assertThat(typeMappingFinder.findTypeMapping(ClassEntity.class)).isSameAs(typeMapping);

        assertThat(typeMappingFinder.findTypeMapping(ClassWithoutTypeMapping.class)).isNull();
    }

    @Test
    public void directTypeMappingShouldWork() {
        //noinspection unchecked
        final TypeMapping<ClassEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping =
                Collections.<Class<?>, TypeMapping<?>>singletonMap(ClassEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        assertThat(typeMappingFinder.findTypeMapping(ClassEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void indirectTypeMappingShouldWork() {
        //noinspection unchecked
        final TypeMapping<ClassEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping =
                Collections.<Class<?>, TypeMapping<?>>singletonMap(ClassEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        // Direct type mapping should still work
        assertThat(typeMappingFinder.findTypeMapping(ClassEntity.class)).isSameAs(typeMapping);

        // Indirect type mapping should give same type mapping as for parent class
        assertThat(typeMappingFinder.findTypeMapping(DescendantClass.class)).isSameAs(typeMapping);
    }

    @Test
    public void indirectTypeMappingShouldReturnFromCache() {
        //noinspection unchecked
        final TypeMapping<ClassEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping = spy(new HashMap<Class<?>, TypeMapping<?>>(1));
        directTypeMapping.put(ClassEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        // Indirect type mapping should give same type mapping as for parent class
        assertThat(typeMappingFinder.findTypeMapping(DescendantClass.class)).isSameAs(typeMapping);

        // The second call
        assertThat(typeMappingFinder.findTypeMapping(DescendantClass.class)).isSameAs(typeMapping);

        // Should call only once, the second return time from cache
        verify(typeMappingFinder.directTypeMapping()).get(ClassEntity.class);
    }

    @Test
    public void typeMappingShouldWorkInCaseOfMoreConcreteTypeMapping() {
        //noinspection unchecked
        final TypeMapping<ClassEntity> typeMapping = mock(TypeMapping.class);
        //noinspection unchecked
        final TypeMapping<DescendantClass> subclassTypeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping = new HashMap<Class<?>, TypeMapping<?>>(2);

        directTypeMapping.put(ClassEntity.class, typeMapping);
        directTypeMapping.put(DescendantClass.class, subclassTypeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        // Parent class should have its own type mapping
        assertThat(typeMappingFinder.findTypeMapping(ClassEntity.class)).isSameAs(typeMapping);

        // Child class should have its own type mapping
        assertThat(typeMappingFinder.findTypeMapping(DescendantClass.class)).isSameAs(subclassTypeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingInCaseOfComplexInheritance() {
        // Good test case — inheritance with AutoValue/AutoParcel

        class Entity {
        }

        class AutoValue_Entity extends Entity {
        }

        class ConcreteEntity extends Entity {
        }

        class AutoValue_ConcreteEntity extends ConcreteEntity {
        }

        //noinspection unchecked
        final TypeMapping<Entity> entityTypeMapping = mock(TypeMapping.class);
        //noinspection unchecked
        final TypeMapping<ConcreteEntity> concreteEntityTypeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping = new HashMap<Class<?>, TypeMapping<?>>(2);

        directTypeMapping.put(Entity.class, entityTypeMapping);
        directTypeMapping.put(ConcreteEntity.class, concreteEntityTypeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        // Direct type mapping for Entity should work
        assertThat(typeMappingFinder.findTypeMapping(Entity.class)).isSameAs(entityTypeMapping);

        // Direct type mapping for ConcreteEntity should work
        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(concreteEntityTypeMapping);

        // Indirect type mapping for AutoValue_Entity should get type mapping for Entity
        assertThat(typeMappingFinder.findTypeMapping(AutoValue_Entity.class)).isSameAs(entityTypeMapping);

        // Indirect type mapping for AutoValue_ConcreteEntity should get type mapping for ConcreteEntity, not for Entity!
        assertThat(typeMappingFinder.findTypeMapping(AutoValue_ConcreteEntity.class)).isSameAs(concreteEntityTypeMapping);
    }

    @Test
    public void typeMappingShouldFindInterface() {
        //noinspection unchecked
        final TypeMapping<InterfaceEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping =
                Collections.<Class<?>, TypeMapping<?>>singletonMap(InterfaceEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        assertThat(typeMappingFinder.findTypeMapping(InterfaceEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatDirectlyImplementsKnownInterface() {
        //noinspection unchecked
        final TypeMapping<InterfaceEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping =
                Collections.<Class<?>, TypeMapping<?>>singletonMap(InterfaceEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        class ConcreteEntity implements InterfaceEntity {
        }

        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(typeMapping);

        // Just to make sure that we don't return this type mapping for all classes.
        assertThat(typeMappingFinder.findTypeMapping(Random.class)).isNull();
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatIndirectlyImplementsKnownInterface() {
        //noinspection unchecked
        final TypeMapping<InterfaceEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping =
                Collections.<Class<?>, TypeMapping<?>>singletonMap(InterfaceEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        class ConcreteEntity implements DescendantInterface {
        }

        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatIndirectlyImplementsKnownInterfaceAndExtendsUnknownClass() {
        //noinspection unchecked
        final TypeMapping<InterfaceEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping =
                Collections.<Class<?>, TypeMapping<?>>singletonMap(InterfaceEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        class ConcreteEntity extends ClassEntity implements DescendantInterface {
        }

        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void typeMappingShouldFindIndirectTypeMappingForClassThatHasParentThatImplementsKnownInterface() {
        //noinspection unchecked
        final TypeMapping<InterfaceEntity> typeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> directTypeMapping =
                Collections.<Class<?>, TypeMapping<?>>singletonMap(InterfaceEntity.class, typeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(directTypeMapping);

        class ConcreteEntity implements InterfaceEntity {
        }

        class Parent_ConcreteEntity extends ConcreteEntity {
        }

        assertThat(typeMappingFinder.findTypeMapping(Parent_ConcreteEntity.class)).isSameAs(typeMapping);
    }

    @Test
    public void shouldPreferDirectTypeMappingToIndirectOfInterface() {
        class ConcreteEntity implements InterfaceEntity {
        }

        //noinspection unchecked
        final TypeMapping<InterfaceEntity> indirectTypeMapping = mock(TypeMapping.class);
        //noinspection unchecked
        final TypeMapping<ConcreteEntity> directTypeMapping = mock(TypeMapping.class);

        Map<Class<?>, TypeMapping<?>> mappingMap = new HashMap<Class<?>, TypeMapping<?>>(2);

        mappingMap.put(InterfaceEntity.class, indirectTypeMapping);
        mappingMap.put(ConcreteEntity.class, directTypeMapping);

        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        typeMappingFinder.directTypeMapping(mappingMap);

        assertThat(typeMappingFinder.findTypeMapping(ConcreteEntity.class)).isSameAs(directTypeMapping);
    }
}
