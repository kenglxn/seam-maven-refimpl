package org.open18.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;

/** 
 * <p>
 * Convert the string to the telephone storage format used in the database.
 * </p>
 * <p>
 * Alternatively, you could define this in the faces-config.xml descriptor.
 * </p>
 * <pre>
 * &lt;converter>
 *   &lt;converter-id>org.open18.PhoneConverter&lt;/converter-id>
 *   &lt;converter-class>org.open18.converter.PhoneConverter&lt;/converter-class>
 * &lt;/converter></pre>
 * <p>
 * However, if you choose to use the faces-config.xml descriptor, this class
 * must be in the src/model classpath hierarchy because JSF cannot see classes
 * in the Seam hot redeployable classloader.
 * </p>
 * <p>
 * Note that the component name is used as the converter id if an id is not
 * specified on the @Converter annotation.
 * </p>
 */
@Name("org.open18.PhoneConverter")
@Converter
public class PhoneConverter implements javax.faces.convert.Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (context == null || component == null) {
			throw new NullPointerException();
		}
		
		if (value == null || "".equals(value)) {
			return null;
		}
		
		String digits = value.replaceAll("[^0-9]", "");
		if (digits.length() == 11) {
			// country code not supported
			digits = digits.substring(1);
		}
		if (digits.length() != 10) {
			throw new ConverterException(
				new FacesMessage(FacesMessage.SEVERITY_WARN,
				"Invalid phone number: " + value,
				"Invalid number of digits in phone number."));
		}
		
		return digits;
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return "";
		}
		
		return (String) value;
	}
}
