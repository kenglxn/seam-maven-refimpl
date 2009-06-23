package no.knowit.m2m.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import no.knowit.m2m.model.Company;

@Name("companyList")
public class CompanyList extends EntityQuery<Company> {
	
  @Override
  public String getEjbql()  { 
    return "select company from Company company";
  }

	@Override
	public String getOrder() {
		if (super.getOrder() == null) {
			setOrder("name asc");
		}
		return super.getOrder();
	}
}
