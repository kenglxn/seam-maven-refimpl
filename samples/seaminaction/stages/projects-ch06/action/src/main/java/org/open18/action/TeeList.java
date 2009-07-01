package org.open18.action;

import org.open18.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("teeList")
public class TeeList extends EntityQuery<Tee> {

	private static final String[] RESTRICTIONS = {};

	private Tee tee;

	public TeeList() {
		tee = new Tee();
		tee.setId(new TeeId());
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
	}

	@Override
	public String getEjbql() {
		return "select tee from Tee tee";
	}

	/*
   * LOO-20090630, see:
	 * http://www.seamframework.org/Community/OrghibernatehqlastQuerySyntaxExceptionExpectingCLOSEFoundNull
   */
	@Override
	public String getCountEjbql()  {
		return "select count(*) from Tee";
	}
	
	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Tee getTee() {
		return tee;
	}

/*
 * LOO-20090627, NOTE: This does not work. 
 * Must use setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS)); in constructor
 *
	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
 */
 
/*
 * LOO-20090630. fixed pagination propblem as described here:
 * http://seamframework.org/Community/PaginationAndEntityQuery
 */ 
	@Override
	public List<Tee> getResultList() {
		List<Tee> result = super.getResultList();
		if (getFirstResult() != null && getFirstResult() > 0 
				&& (result == null ||  result.size() == 0)) {
			setFirstResult(0);
			refresh();
		}
		return super.getResultList();
	}
}
