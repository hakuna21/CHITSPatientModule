package org.openmrs.module.chits.webservices.rest;

@SuppressWarnings("serial")
public class NoSuchConceptAttributeException extends IllegalArgumentException {
	public NoSuchConceptAttributeException(String msg) {
		super(msg);
	}
}
