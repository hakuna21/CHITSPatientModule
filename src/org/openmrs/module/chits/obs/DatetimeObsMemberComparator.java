package org.openmrs.module.chits.obs;

import java.util.Comparator;
import java.util.Date;

import org.openmrs.Obs;
import org.openmrs.module.chits.CachedConceptId;
import org.openmrs.module.chits.DateUtil;
import org.openmrs.module.chits.web.taglib.Functions;

/**
 * Compares instances of {@link GroupObs} based on the valueDatetime of the member observations of each.
 * 
 * @author Bren
 */
public class DatetimeObsMemberComparator implements Comparator<Obs> {
	/** The concept type of each group observation's member to compare the valueDatetimes of */
	private final CachedConceptId memberType;

	public DatetimeObsMemberComparator(CachedConceptId memberType) {
		this.memberType = memberType;
	}

	/**
	 * Compares the two group observations by taking the member observation of each corresponding to the member type passed in the constructor and comparing
	 * each's valueDatetime value.
	 */
	public int compare(Obs o1, Obs o2) {
		// get the member observation of each group
		final Obs visit1DateObs = Functions.observation(o1, memberType);
		final Obs visit2DateObs = Functions.observation(o2, memberType);

		// extract the valueDatetime from each
		final Date visit1Date = visit1DateObs != null ? visit1DateObs.getValueDatetime() : null;
		final Date visit2Date = visit2DateObs != null ? visit2DateObs.getValueDatetime() : null;

		// compare with each watching out for null values
		if (visit1Date == null && visit2Date == null) {
			return 0;
		} else if (visit1Date == null) {
			return +1;
		} else if (visit2Date == null) {
			return -1;
		} else {
			final int comp = DateUtil.stripTime(visit1Date).compareTo(DateUtil.stripTime(visit2Date));
			if (comp == 0 && visit1DateObs.getId() != null && visit2DateObs.getId() != null) {
				// compare their IDs instead
				return visit1DateObs.getId().compareTo(visit2DateObs.getId());
			} else {
				return comp;
			}
		}
	}
}
