package de.kreth.clubhelperbackend;

public interface Dao<T> {

	public abstract T getById(long Id);

	public abstract T insert(T p);

	public abstract void update(T p);

}