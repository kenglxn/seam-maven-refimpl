package org.open18.action;

import org.open18.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;

@Name("golferList")
public class GolferList extends EntityQuery<Golfer>
{
	@Override
	public String getEjbql() 
	{ 
			return "select golfer from Golfer golfer";
	}
		
/*
 * LOO-20090630. fixed pagination propblem as described here:
 * http://seamframework.org/Community/PaginationAndEntityQuery
 */ 
	@Override
	public List<Golfer> getResultList() {
		List<Golfer> result = super.getResultList();
		if (getFirstResult() != null && getFirstResult() > 0 
				&& (result == null ||  result.size() == 0)) {
			setFirstResult(0);
			refresh();
		}
		return super.getResultList();
	}


}
