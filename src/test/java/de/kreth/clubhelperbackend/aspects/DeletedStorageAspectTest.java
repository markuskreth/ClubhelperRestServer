package de.kreth.clubhelperbackend.aspects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.junit.Before;
import org.junit.Test;

import de.kreth.clubhelperbackend.dao.DeletedEntriesDao;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;
import de.kreth.clubhelperbackend.pojo.Group;
import de.kreth.clubhelperbackend.utils.TimeProvider;

public class DeletedStorageAspectTest {

	private DeletedStorageAspect aspect;
	private DeletedEntriesDao deletedEntriesDao;
	private TimeProvider timeProvider;

	@Before
	public void initAspect() {
		timeProvider = mock(TimeProvider.class);
		deletedEntriesDao = mock(DeletedEntriesDao.class);
		this.aspect = new DeletedStorageAspect(deletedEntriesDao);
		this.aspect.setTime(timeProvider);
	}
	
	@Test
	public void testDeleteStorage() {
		Date now = new Date();
		when(timeProvider.getNow()).thenReturn(now );
		Group deleted = new Group(123L, "Testname", null, null);
		JoinPoint joinPoint = mock(JoinPoint.class);
		aspect.storeDeleted(joinPoint, deleted);
		DeletedEntries del = new DeletedEntries(-1L, "Group", 123L, now, now);
		verify(deletedEntriesDao, times(1)).insert(del );
	}
}
