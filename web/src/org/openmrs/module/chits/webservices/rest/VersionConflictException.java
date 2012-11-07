package org.openmrs.module.chits.webservices.rest;

@SuppressWarnings("serial")
public class VersionConflictException extends IllegalArgumentException {
	public VersionConflictException(String msg) {
		super(msg);
	}
}
