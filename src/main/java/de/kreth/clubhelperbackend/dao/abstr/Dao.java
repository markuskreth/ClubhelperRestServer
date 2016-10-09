package de.kreth.clubhelperbackend.dao.abstr;

import java.util.Date;
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
	 * @return List aller Datenbankeinträge
	 */
	public abstract List<T> getAll();

	/**
	 * Delivers List of objects matching the Where clause. No where Statement is needed.
	 * @return List of Objects matching the where clause.
	 */
	public abstract List<T> getByWhere(String where);

	/**
	 * Delivers List of objects that changed since provided Date
	 * @return List of Objects changed since Date
	 */
	public abstract List<T> getChangedSince(Date changed);
	
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
	 * Aktualisiert den Datenbankeintrag mit der angegebenen Id und setzt die Werte des Objekts. Keine werte (auch changed nicht!) werden geändert.
	 * <br /> changed muss vor aufruf gesetzt sein!
	 * @param obj Zu aktualiserendes Objekt mit aktualisiertem Changed-Eintrag.
	 * @return	true, wenn update erfolgreich.
	 */
	public abstract boolean update(long id, T obj);

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