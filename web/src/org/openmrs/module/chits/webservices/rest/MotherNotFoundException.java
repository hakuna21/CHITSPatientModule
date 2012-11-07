package org.openmrs.module.chits.webservices.rest;

@SuppressWarnings("serial")
public class MotherNotFoundException extends IllegalArgumentException {
	public MotherNotFoundException(String msg) {
		super(msg);
	}
}
