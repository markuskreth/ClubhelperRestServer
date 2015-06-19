package de.kreth.clubhelperbackend.controller;

import org.springframework.ui.Model;

public interface ClubController {

	public abstract String update(String toUpdate, Model m);

	public abstract String create(String toCreate, Model m);
	
	public abstract String delete(String toCreate, Model m);

}