package org.openmrs.module.chits.webservices.rest;

@SuppressWarnings("serial")
public class NoSuchConceptAnswerException extends IllegalArgumentException {
	public NoSuchConceptAnswerException(String msg) {
		super(msg);
	}
}
