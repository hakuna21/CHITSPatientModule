package org.openmrs.module.chits.fpprogram;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.ObsUtil;
import org.openmrs.module.chits.PatientConsultEntryForm;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPClientTypeConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPFamilyPlanningMethodConcepts;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPMethodOptions;
import org.openmrs.module.chits.fpprogram.FamilyPlanningConstants.FPServiceDeliveryRecordConcepts;
import org.openmrs.module.chits.obs.DatetimeGroupObsComparator;
import org.openmrs.module.chits.obs.RepeatingObs;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Family planning method first entered during registration.
 * 
 * @author Bren
 */
public class FamilyPlanningMethod extends RepeatingObs<ServiceDeliveryRecord> {
	/** The number of milliseconds in a day */
	private final static long ONE_DAY = 24L * 60 * 60 * 1000;

	public FamilyPlanningMethod() {
		// setup a new parent observation when constructing from blank
		this(PatientConsultEntryForm.newObs(FPFamilyPlanningMethodConcepts.FAMILY_PLANNING_METHOD, ObsUtil.PATIENT_CONTEXT.get()));
	}

	public FamilyPlanningMethod(Obs obs) {
		// setup homogenous member concepts
		super(obs);

		// set heterogenous member concepts
		super.setConceptsExceptFirst(FPFamilyPlanningMethodConcepts.values());
	}

	/**
	 * Synonym for 'children': 'serviceRecords' but returns the children sorted by 'date given'.
	 * 
	 * @return The children of this concept
	 */
	public List<ServiceDeliveryRecord> getServiceRecords() {
		// sort the children by 'date given'
		final List<ServiceDeliveryRecord> svcRecords = super.getChildren();
		Collections.sort(svcRecords, new DatetimeGroupObsComparator<ServiceDeliveryRecord>(FPServiceDeliveryRecordConcepts.DATE_ADMINISTERED));

		// ... reverse so that the order becomes latest to earliest
		Collections.reverse(svcRecords);

		// return the children after sorting them by 'date given / administered'
		return svcRecords;
	}

	/**
	 * Return the homogenous children concept type (Service Delivery Records)
	 */
	@Override
	public CachedConceptId getChildrenConcept() {
		return FPServiceDeliveryRecordConcepts.SERVICE_DELIVERY_RECORD;
	}

	/**
	 * Returns true if the DATE_DROPPED_OUT member has already been filled
	 * 
	 * @return true if this family planning method has already been dropped
	 */
	public boolean isDroppedOut() {
		final Obs dropoutDate = getMember(FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT);
		return dropoutDate != null && dropoutDate.getValueDatetime() != null;
	}

	/**
	 * Returns the DATE_DROPPED_OUT date value
	 * 
	 * @return The date value of the date of dropout member
	 */
	public Date getDroppedDate() {
		final Obs dropoutDate = getMember(FPFamilyPlanningMethodConcepts.DATE_OF_DROPOUT);
		return dropoutDate != null ? dropoutDate.getValueDatetime() : null;
	}

	/**
	 * Returns the DATE_OF_ENROLLMENT
	 * 
	 * @return The date value of the date of enrollment of this method
	 */
	public Date getEnrollmentDate() {
		final Obs enrollmentDate = getMember(FPFamilyPlanningMethodConcepts.DATE_OF_ENROLLMENT);
		return enrollmentDate != null ? enrollmentDate.getValueDatetime() : null;
	}

	/**
	 * Returns the dropout reason
	 * 
	 * @return The dropout reason
	 */
	public String getDropoutReason() {
		final Obs dropoutReason = getMember(FPFamilyPlanningMethodConcepts.DROPOUT_REASON);
		return dropoutReason != null ? dropoutReason.getValueText() : null;
	}

	/**
	 * Returns true if the family planning method is of a permanent type based on the group observation's coded value.
	 * 
	 * @return True if this family planning method falls within the permanent method category
	 */
	public boolean isPermanentMethod() {
		// this is a permanent method if the method type is a member of the permanent method convenience set
		return Functions.members(FPMethodOptions.ARTIFICIAL_PERM).contains(getObs().getValueCoded());
	}

	/**
	 * Returns whether the client type is "LU" (Learning User).
	 * 
	 * @return true if the client type is LU
	 */
	public boolean isLearningUser() {
		return Functions.concept(FPClientTypeConcepts.LU).equals(getMember(FPFamilyPlanningMethodConcepts.CLIENT_TYPE).getValueCoded());
	}

	/**
	 * Returns whether the client type is "NA" (New Acceptor).
	 * 
	 * @return true if the client type is NA
	 */
	public boolean isNewAcceptor() {
		return Functions.concept(FPClientTypeConcepts.NA).equals(getMember(FPFamilyPlanningMethodConcepts.CLIENT_TYPE).getValueCoded());
	}

	/**
	 * Set initially to the system date of registration if newly registered; Otherwise, once the Service Delivery Form is filled out and saved, the date
	 * indicated on the said form will be displayed.
	 * <p>
	 * If the current date is later than the schedule of next service, this date is indicated in red.
	 * 
	 * @return
	 */
	public Date getDateOfNextService() {
		final Obs nextSvcObs = getMember(FPFamilyPlanningMethodConcepts.DATE_OF_NEXT_SERVICE);
		if (nextSvcObs != null && nextSvcObs.getValueDatetime() != null) {
			// return indicated date of next service from the latest (current) family planning method
			return nextSvcObs.getValueDatetime() != null ? DateUtil.stripTime(nextSvcObs.getValueDatetime()) : null;
		}

		// no applicable date of next service
		return null;
	}

	/**
	 * Checks if the the date today is passed the current method's scheduled next service plus the allowable extension time.
	 * 
	 * @return If the next scheduled service date plus the allowable extension time has elapsed.
	 */
	public boolean isMethodShouldBeDropped() {
		// determine if a warning needs to be displayed
		if (!isDroppedOut() && !isPermanentMethod()) {
			// an active, not-permanent family planning method: determine the next service schedule
			final Date nextScheduledService = getDateOfNextService();
			if (nextScheduledService != null) {
				// determine the grace period (in days) for the family planning method
				final int extension = FamilyPlanningUtil.calculateFamilyPlaningMethodExtension(this);

				// calculate next service schedule plus extension date
				final Date warningDate = new Date(nextScheduledService.getTime() + extension * ONE_DAY);
				final Date today = DateUtil.stripTime(new Date());

				// check if the extension has elapsed
				if (today.after(warningDate)) {
					return true;
				}
			}
		}

		// either the conditions for considering a method needing to be dropped have not been met or do not apply to this method
		return false;
	}
}
