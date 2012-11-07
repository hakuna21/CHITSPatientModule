package org.openmrs.module.chits.mcprogram;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.mcprogram.MaternalCareConstants.MCPostpartumIERecordConcepts;
import org.openmrs.module.chits.obs.GroupObs;

/**
 * Encapsulates a single internal examination record either based on an existing record or containing new observations.
 * 
 * @author Bren
 */
public class PostpartumInternalExaminationRecord extends GroupObs {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/** Parser for time component */
	private final SimpleDateFormat TIME_FMT = new SimpleDateFormat("hh:mma");

	/** Time component of the IE exam, in hh:mma format */
	private String visitTime;

	public PostpartumInternalExaminationRecord() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(MCPostpartumIERecordConcepts.POSTDELIVERY_IE, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public PostpartumInternalExaminationRecord(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(MCPostpartumIERecordConcepts.values());

		// initialize the 'visit time' if the observation has a visit date time
		final Obs visitDateObs = getMember(MCPostpartumIERecordConcepts.VISIT_DATE);
		if (visitDateObs != null) {
			Date visitDate = visitDateObs.getValueDatetime();
			if (visitDate != null) {
				this.visitTime = TIME_FMT.format(visitDate);
			}
		}
	}

	/**
	 * Convenience method to extract the visit date from the observation map.
	 * 
	 * @return The visit date
	 */
	public Date getVisitDate() {
		final Obs visitDateObs = getMember(MCPostpartumIERecordConcepts.VISIT_DATE);
		return visitDateObs != null ? visitDateObs.getValueDatetime() : null;
	}

	public String getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(String visitTime) {
		this.visitTime = visitTime;
	}

	/**
	 * Stores the visit time into the time component of the VISIT_DATE member.
	 * 
	 * @param visitTime
	 *            The time in hh:mma format
	 */
	public void storeVisitTimeIntoVisitDate() {
		// split up the time component
		int hour = 0, mins = 0, secs = 0;

		// store visit time component into the visit date
		if (!StringUtils.isEmpty(visitTime)) {
			try {
				final Date time = TIME_FMT.parse(visitTime);
				final Calendar c = Calendar.getInstance();
				c.setTime(time);
				hour = c.get(Calendar.HOUR_OF_DAY);
				mins = c.get(Calendar.MINUTE);
				secs = c.get(Calendar.SECOND);
			} catch (Exception ex) {
				// leave the time unchanged
				log.warn("Unable to set time component: " + visitTime);
			}
		}

		// store time component into the visit date
		final Obs visitDateObs = getMember(MCPostpartumIERecordConcepts.VISIT_DATE);
		if (visitDateObs != null) {
			final Date visitDate = visitDateObs.getValueDatetime();

			// NOTE: Time component will only be stored if there is a date component
			if (visitDate != null) {
				// setup the time component of the visit date
				final Calendar c = Calendar.getInstance();
				c.setTime(visitDate);
				c.set(Calendar.HOUR_OF_DAY, hour);
				c.set(Calendar.MINUTE, mins);
				c.set(Calendar.SECOND, secs);
				c.set(Calendar.MILLISECOND, 0);
				visitDateObs.setValueDatetime(c.getTime());
			}
		}
	}
}