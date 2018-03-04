package de.kreth.clubhelperbackend.utils;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class TimeProviderImpl implements TimeProvider {

	@Override
	public Date getNow() {
		return new Date();
	}

}
