package symbol;

import org.objectweb.asm.Type;

/**
 * Information about a symbol
 */
public class Info {

	private String id;
	private String structId = "";
	private Object value;
	private boolean isFunction = false;
	private int arraySize = 0;
	private Integer index = -1;

	public Info(String id, Object value) {
		this.id = id;
		this.value = value;
		this.isFunction = false;
	}

	public Info(String id, Object value, int arraySize, Integer index) {
		this.id = id;
		this.value = value;
		this.isFunction = false;
		this.arraySize = arraySize;
		this.index = index;
	}
	public Info(String id, Object value, boolean isFunction) {
		this.id = id;
		this.value = value;
		this.isFunction = isFunction;
	}
	public Info(String id, Object value, boolean isFunction, Integer index) {
		this.id = id;
		this.value = value;
		this.isFunction = isFunction;
		this.index = index;
	}

	public Info(String id, Object value, String structId) {
		this.id = id;
		this.value = value;
		this.structId = structId;
	}

	public Info(String id, Object value, boolean isFunction, String structId) {
		this.id = id;
		this.value = value;
		this.isFunction = isFunction;
		this.structId = structId;
	}

	public Info(String id, Object value, Integer index){
		this.id = id;
		this.value = value;
		this.index = index;
	}
	public Info(String id, String structId, Object value, Integer index){
		this.id = id;
		this.structId = structId;
		this.value = value;
		this.index = index;
	}
	public Info(String id, String structId, Object value, int arraySize, Integer index){
		this.id = id;
		this.structId = structId;
		this.value = value;
		this.arraySize = arraySize;
		this.index = index;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Type value) {
		this.value = value;
	}

	public boolean isFunction() {
		return isFunction;
	}

	public void setFunction(boolean function) {
		isFunction = function;
	}

	public String getStructId() {
		return structId;
	}

	public void setStructId(String structId) {
		this.structId = structId;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public int getArraySize() {
		return arraySize;
	}

	public void setArraySize(int arraySize) {
		this.arraySize = arraySize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + (isFunction ? 0 : 1);
		result = prime * result + ((structId == null) ? 0 : structId.hashCode());
		result = prime * result + Integer.valueOf(arraySize).hashCode();
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Info other = (Info) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if(structId.equals("")){
			if(!other.structId.equals(""))
				return false;
		}else if(!structId.equals(other.structId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if(isFunction != other.isFunction)
			return false;

		return true;
	}

}
