package de.kreth.clubhelperbackend.aspects;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.kreth.clubhelperbackend.dao.DeletedEntriesDao;
import de.kreth.clubhelperbackend.pojo.Data;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;

@Aspect
@Component
public class DeletedStorageAspect {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private DeletedEntriesDao deletedEntriesDao;

	@Autowired
	public DeletedStorageAspect(DeletedEntriesDao deletedEntriesDao) {
		super();
		this.deletedEntriesDao = deletedEntriesDao;
	}

	@Pointcut("execution (public * de.kreth.clubhelperbackend.controller.abstr.AbstractController.delete(..))")
	private void invocation() {
	}

	@AfterReturning(pointcut = "invocation()", returning = "deleted")
	public void storeDeleted(JoinPoint joinPoint, Data deleted) {

		logger.debug("Deleted: " + deleted);
		Class<?> class1 = deleted.getClass();

		while (!class1.getSuperclass().equals(Object.class))
			class1 = class1.getSuperclass();

		String tableName = class1.getSimpleName();
		long id = deleted.getId();
		Date now = new Date();

		DeletedEntries entry = new DeletedEntries(-1L, tableName, id, now, now);
		logger.info("Inserted Deleteentry: " + entry);
		deletedEntriesDao.insert(entry);
	}

}
