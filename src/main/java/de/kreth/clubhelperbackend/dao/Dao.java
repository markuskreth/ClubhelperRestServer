package de.kreth.clubhelperbackend.dao;

import java.util.List;

import de.kreth.clubhelperbackend.pojo.Data;

public interface Dao<T extends Data> {

	/**
	 * Liefert das Objekt zur übergebenen ID
	 * @param id	Id des geswünschten Objekts
	 * @return	Objekt.
	 */
	public abstract T getById(long id);

	/**
	 * Liefert alle Datenbankeinträge
	 * @return Collection aller Datenbankeinträge
	 */
	public abstract List<T> getAll();
	
	/**
	 * Erstellt ein neues Objekt in der Datenbank. Id wird im rückgabeobjekt gesetzt.
	 * <br /> changed und created müssen vor aufruf gesetzt sein!
	 * @param obj	Daten des zu zu erstellenden Datenbankeintrags
	 * @return	Aktualisiertes Objekt oder null, wenn update fehlgeschlagen.
	 */
	public abstract T insert(T obj);

	/**
	 * Aktualisiert den Datenbankeintrag zum Objekt. Keine werte (auch changed nicht!) werden geändert.
	 * <br /> changed muss vor aufruf gesetzt sein!
	 * @param obj Zu aktualiserendes Objekt mit aktualisiertem Changed-Eintrag.
	 * @return	true, wenn update erfolgreich.
	 */
	public abstract boolean update(T obj);

	/**
	 * Löscht einen Datenbankeintrag.
	 * @param obj	Objekt, das gelöscht werden soll.
	 * @return	true, wenn löschen erfolgreich.
	 */
	public abstract boolean delete(T obj);

	/**
	 * 
	 * Löscht einen Datenbankeintrag über die Id des Objekts
	 * @param id	id des Objekts, das gelöscht werden soll.
	 * @return	true, wenn löschen erfolgreich.
	 */
	public abstract boolean delete(long id);
}