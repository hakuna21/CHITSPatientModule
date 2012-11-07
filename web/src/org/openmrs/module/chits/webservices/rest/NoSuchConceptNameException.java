package org.openmrs.module.chits.webservices.rest;

@SuppressWarnings("serial")
public class NoSuchConceptNameException extends IllegalArgumentException {
	public NoSuchConceptNameException(String msg) {
		super(msg);
	}
}
