package org.openmrs.module.chits.webservices.rest;

@SuppressWarnings("serial")
public class InvalidDateException extends IllegalArgumentException {
	public InvalidDateException(String msg) {
		super(msg);
	}
}
