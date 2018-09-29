package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.kreth.clubhelperbackend.dao.StartpassDao;
import de.kreth.clubhelperbackend.pojo.Startpass;

public class StartpassControllerTest {

	@Mock
	public StartpassDao dao;
	
	private StartpassController controller;

	private List<Startpass> resultList;
	
	@Before
	public void initController() {
		MockitoAnnotations.initMocks(this);
		controller = new StartpassController(dao);
		resultList = new ArrayList<>();
		when(dao.getForPersonId(anyLong())).thenReturn(resultList);
	}
	
	@Test
	public void testGetByParentIdLong() {
		resultList.add(new Startpass(11L));
		List<Startpass> result = controller.getByParentId(1);
		assertSame(resultList, result);
	}

}
