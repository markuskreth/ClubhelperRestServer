package de.kreth.clubhelperbackend.pojo;

/**
 * Created by markus on 06.03.16.
 */
public enum SyncStatus {
	/**
	 * Clientside created, not synchronized
	 */
	NEW(0),
	/**
	 * Clientside changed since last synchronization
	 */
	CHANGED(10),
	/**
	 * Synchronized with server, no changes.
	 */
	SYNCHRONIZED(100),
	/**
	 * Deleted ClientSide.
	 */
	DELETED(1000);

	@SuppressWarnings("unused")
	private final int id;

	SyncStatus(int id) {
		this.id = id;
	}

}
