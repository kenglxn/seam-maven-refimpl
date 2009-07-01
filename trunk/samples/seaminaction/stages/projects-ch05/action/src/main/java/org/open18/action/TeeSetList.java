package org.open18.action;

import org.open18.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("teeSetList")
public class TeeSetList extends EntityQuery<TeeSet> {

	private static final String[] RESTRICTIONS = {
			"lower(teeSet.color) like concat(lower(#{teeSetList.teeSet.color}),'%')",
			"lower(teeSet.name) like concat(lower(#{teeSetList.teeSet.name}),'%')",};

	private TeeSet teeSet = new TeeSet();

	public TeeSetList() {
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
	}
	
	@Override
	public String getEjbql() {
		return "select teeSet from TeeSet teeSet";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public TeeSet getTeeSet() {
		return teeSet;
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
	public List<TeeSet> getResultList() {
		List<TeeSet> result = super.getResultList();
		if (getFirstResult() != null && getFirstResult() > 0 
				&& (result == null ||  result.size() == 0)) {
			setFirstResult(0);
			refresh();
		}
		return super.getResultList();
	}

}
