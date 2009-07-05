package org.open18.action;

import org.open18.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Name("courseList")
@Scope(ScopeType.CONVERSATION)
public class CourseList extends EntityQuery<Course> {

	private static final String[] RESTRICTIONS = {
			"lower(course.description) like concat(lower(#{courseList.course.description}),'%')",
			"lower(course.designer) like concat(lower(#{courseList.course.designer}),'%')",
			"lower(course.fairways) like concat(lower(#{courseList.course.fairways}),'%')",
			"lower(course.greens) like concat(lower(#{courseList.course.greens}),'%')",
			"lower(course.name) like concat(lower(#{courseList.course.name}),'%')",};

	private Course course = new Course();

	public CourseList() {
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
	}
	
	@Override
	public String getEjbql() {
		return "select course from Course course";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	@Override
	public String getOrder() {
		if (super.getOrder() == null) {
			setOrder("name asc");
		}
		return super.getOrder();
	}

	public Course getCourse() {
		return course;
	}

	/*
	 * LOO-20090630, NOTE: This does not work. 
	 * Must use setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS)); in constructor
	 *
	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
	 */

	/*
	 * LOO-20090630. fixed pagination problem as described here:
	 * http://seamframework.org/Community/PaginationAndEntityQuery
	 */ 
	@Override
	public List<Course> getResultList() {
		List<Course> result = super.getResultList();
		if (getFirstResult() != null && getFirstResult() > 0 
				&& (result == null ||  result.size() == 0)) {
			setFirstResult(0);
			refresh();
		}
		return super.getResultList();
	}

}
