package no.knowit.example.jpqlconsole;

public class ColumnData {
	private String header;
	private Object index;
	private Class type;
	
	public ColumnData(String header, Object index, Class type) {
		super();
		this.header = header;
		this.index = index;
		this.type = type;
	}

	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public Object getIndex() {
		return index;
	}
	public void setIndex(Object index) {
		this.index = index;
	}
	
	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

}
