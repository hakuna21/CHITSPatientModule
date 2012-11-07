package org.openmrs.module.chits.webservices.rest.resource;

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.chits.Defaulter.DueServiceInfo;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Handler for generating resource representation for the DueServiceInfo class.
 * 
 * @author Bren
 */
@Resource("CHITSDueServiceInfoResource")
@Handler(supports = { DueServiceInfo.class }, order = -10)
public class DueServiceInfoResource extends BaseDelegatingResource<DueServiceInfo> {
	@Override
	public DueServiceInfo getByUniqueId(String param) {
		// operation not supported
		return null;
	}

	@Override
	public String getUri(Object delegate) {
		if (!(delegate instanceof DueServiceInfo)) {
			return "";
		}

		Resource res = (Resource) getClass().getAnnotation(Resource.class);
		if (res != null) {
			return RestConstants.URI_PREFIX + "v1" + "/" + res.value() + "/" + delegate.toString();
		}

		throw new RuntimeException(getClass() + " needs a @Resource or @SubResource annotation");
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		final DelegatingResourceDescription desc = new DelegatingResourceDescription();

		// these are read-only! (the property setters don't do anything)
		desc.addProperty("serviceType");
		desc.addProperty("description");
		desc.addProperty("dateDue");
		desc.addProperty("daysOverdue");

		return desc;
	}

	@PropertyGetter("serviceType")
	public static String getServiceType(DueServiceInfo delegate) {
		return (delegate != null && delegate.getServiceType() != null) ? delegate.getServiceType().toString() : null;
	}

	@PropertySetter("serviceType")
	public static void setServiceType(Patient delegate, String lpin) {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	@PropertyGetter("dateDue")
	public static String getDateDue(DueServiceInfo delegate) {
		return delegate.getDateDue() != null ? Context.getDateFormat().format(delegate.getDateDue()) : null;
	}

	@PropertySetter("dateDue")
	public static void setDateDue(DueServiceInfo delegate, String dateDue) {
		// this is a read-only attribute, so this setter doesn't do anything!
	}

	@Override
	protected DueServiceInfo newDelegate() {
		throw new UnsupportedOperationException("operation is not supported");
	}

	@Override
	protected void delete(DueServiceInfo dueServiceInfo, String reason, RequestContext paramRequestContext) throws ResponseException {
		throw new UnsupportedOperationException("operation is not supported");
	}

	@Override
	public void purge(DueServiceInfo patient, RequestContext paramRequestContext) throws ResponseException {
		throw new UnsupportedOperationException("operation is not supported");
	}

	@Override
	protected DueServiceInfo save(DueServiceInfo defaulter) {
		throw new UnsupportedOperationException("operation is not supported");
	}
}
