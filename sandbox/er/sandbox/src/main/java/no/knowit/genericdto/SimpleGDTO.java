package no.knowit.genericdto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SimpleGDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> attributes = null;

	public SimpleGDTO() {
		this.attributes = new HashMap<String, Object>();
	}

	public <T> SimpleGDTO add(String name, T value) {
		ikkeNull(name, "Attribute name cannot be null");
		this.attributes.put(name, value);
		return this;

	}

	public Object get(String property) {
		return attributes.get(property);
	}

	private void ikkeNull(Object property, String message) {
		if (property == null) {
			throw new IllegalArgumentException(message);
		}
	}
}
