package de.kreth.clubhelperbackend.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.reflections.Reflections;

import de.kreth.clubhelperbackend.controller.abstr.ClubController;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Data;

@RunWith(Parameterized.class)
public class InstanciateAllController {

	@Parameter
	public Class<? extends ClubController<?>> testClass;

    @SuppressWarnings("rawtypes")
	@Parameters(name = "{index}: {0}")
    public static Set<Class<? extends ClubController>> data() {
    	List<Class<? extends ClubController>> excluded = new ArrayList<>();
    	excluded.add(AttendanceController.class);
    	excluded.add(DeletedEntriesController.class);
    	excluded.add(StartpassController.class);
    	Reflections reflections = new Reflections("de.kreth.clubhelperbackend.controller");
    	Set<Class<? extends ClubController>> classes = reflections.getSubTypesOf(ClubController.class).stream()
    			.filter(c -> !c.isAnonymousClass() && !Modifier.isAbstract(c.getModifiers())).collect(Collectors.toSet());
    	return classes;
    }

	@Test
	public void instanciate() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Constructor<? extends ClubController<?>> constructor = testClass.getConstructor(Dao.class);
		constructor.newInstance((Dao<Data>)null);
	}

}
