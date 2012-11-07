package org.openmrs.module.chits.web;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * An {@link HttpSession} implementation that can be used in place of a real HttpSession for invoking controls outside of an actual request.
 * 
 * @author Bren
 */
@SuppressWarnings("deprecation")
public class DummyHttpSession implements HttpSession {
	/** Stores attributes */
	private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new Vector<String>(attributes.keySet()).elements();
	}

	@Override
	public long getCreationTime() {
		return 0;
	}

	@Override
	public String getId() {
		return "";
	}

	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getValue(String key) {
		return attributes.get(key);
	}

	@Override
	public String[] getValueNames() {
		return new ArrayList<String>(attributes.keySet()).toArray(new String[attributes.size()]);
	}

	@Override
	public void invalidate() {
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public void putValue(String key, Object value) {
		setAttribute(key, value);
	}

	@Override
	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	@Override
	public void removeValue(String key) {
	}

	@Override
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	@Override
	public void setMaxInactiveInterval(int arg0) {
	}
}
