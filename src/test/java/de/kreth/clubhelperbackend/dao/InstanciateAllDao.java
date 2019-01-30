package de.kreth.clubhelperbackend.dao;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.reflections.Reflections;

import de.kreth.clubhelperbackend.dao.abstr.Dao;

@RunWith(Parameterized.class)
public class InstanciateAllDao {

	@Parameter
	public Class<? extends Dao<?>> testClass;

	@SuppressWarnings("rawtypes")
	@Parameters(name = "{index}: {0}")
	public static Set<Class<? extends Dao>> data() {
		Reflections reflections = new Reflections("de.kreth.clubhelperbackend.dao");
		Set<Class<? extends Dao>> classes = reflections.getSubTypesOf(Dao.class).stream()
				.filter(c -> !c.isAnonymousClass() && !Modifier.isAbstract(c.getModifiers()))
				.collect(Collectors.toSet());
		return classes;
	}

	@Test
	public void instanciate() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			ReflectiveOperationException, SecurityException {
		testClass.getConstructor().newInstance();
	}

}
