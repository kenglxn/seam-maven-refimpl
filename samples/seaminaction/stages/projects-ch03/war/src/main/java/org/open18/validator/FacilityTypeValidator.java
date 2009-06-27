package org.open18.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Validator;
import org.open18.lookup.FacilityTypeLookup;

/** 
 * <p>
 * Ensure that the entry is a valid facility type. Null and empty string is permitted.
 * </p>
 * <p>
 * Alternatively, you could define this in the faces-config.xml descriptor.
 * </p>
 * <pre>
 * &lt;validator>
 *   &lt;validator-id>org.open18.FacilityTypeValidator&lt;/validator-id>
 *   &lt;validator-class>org.open18.validator.FacilityTypeValidator&lt;/validator-class>
 * &lt;/validator></pre>
 * <p>
 * However, if you choose to use the faces-config.xml descriptor, this class
 * must be in the src/model classpath hierarchy because JSF cannot see classes
 * in the Seam hot redeployable classloader.
 * </p>
 */
@Name("facilityTypeValidator")
@Validator(id = "org.open18.FacilityTypeValidator")
public class FacilityTypeValidator implements javax.faces.validator.Validator {
	public void validate(FacesContext context, UIComponent component, Object value)
		throws ValidatorException {
		
		if (context == null || component == null) {
			throw new NullPointerException();
		}

		if (value == null || "".equals(value)) {
			return;
		}

		if (!(value instanceof String)) {
			throw new ValidatorException(
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Expecting a string value for facility type",
					"Expecting a string value for facility type")
			);
		}

		for (String type : FacilityTypeLookup.types) {
			if (type.equals(value)) {
				return;
			}
		}

		throw new ValidatorException(
			new FacesMessage(FacesMessage.SEVERITY_ERROR,
				"Invalid facility type: " + value,
				"Invalid facility: " + value)
		);
	}
}
