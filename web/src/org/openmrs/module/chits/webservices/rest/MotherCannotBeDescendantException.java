package org.openmrs.module.chits.webservices.rest;

@SuppressWarnings("serial")
public class MotherCannotBeDescendantException extends IllegalArgumentException {
	public MotherCannotBeDescendantException(String msg) {
		super(msg);
	}
}
