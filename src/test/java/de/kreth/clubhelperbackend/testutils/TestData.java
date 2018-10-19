package de.kreth.clubhelperbackend.testutils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import de.kreth.clubhelperbackend.pojo.Data;

public class TestData {

	private static TestData instance;
	private List<TestDataGenerator<? extends Data>> generators;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TestData() {
		Reflections refl = new Reflections("de.kreth.clubhelperbackend");
		Set<Class<? extends TestDataGenerator>> gene = refl.getSubTypesOf(TestDataGenerator.class);
		generators = new ArrayList<>();
		for (Class<? extends TestDataGenerator> toInit: gene) {
			try {
				if (Modifier.isAbstract(toInit.getModifiers()) == false) {
					TestDataGenerator<? extends Data> gen = toInit.newInstance();
					generators.add(gen);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static <T extends Data> T getTestObject(Class<T> dataClass) {
		ensureInstance();
		try {
			return instance.findGenerator(dataClass).instance(dataClass);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException("No testdata for class " + dataClass, e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Data> TestDataGenerator<T> findGenerator(Class<T> dataClass) {
		for (TestDataGenerator<? extends Data> gen: generators) {
			if (gen.isResponsible(dataClass)) {
				return (TestDataGenerator<T>) gen;
			}
		}
		throw new IllegalArgumentException("No testdata for class " + dataClass);
	}

	private static void ensureInstance() {
		if (instance == null) {
			instance = new TestData();
		}		
	}

	@SuppressWarnings("unchecked")
	public static <T extends Data> void change(T inserted) {
		ensureInstance();
		TestDataGenerator<T> generator = (TestDataGenerator<T>) instance.findGenerator(inserted.getClass());
		generator.change(inserted);
	}
	
	public interface TestDataGenerator <T extends Data> {
		boolean isResponsible(Class<?> objClass);
		T instance(Class<T> pojoClass);
		void change(T obj);
	}
}
