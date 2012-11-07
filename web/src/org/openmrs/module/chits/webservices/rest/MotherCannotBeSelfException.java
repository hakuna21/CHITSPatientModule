package org.openmrs.module.chits.webservices.rest;

@SuppressWarnings("serial")
public class MotherCannotBeSelfException extends IllegalArgumentException {
	public MotherCannotBeSelfException(String msg) {
		super(msg);
	}
}
