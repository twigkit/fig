package twigkit.fig;

import java.io.Serializable;

/**
 * 
 * @param <T>
 */
public class Value<T> implements Serializable {

    private String label;
    private T value;
    private boolean required;

    public Value() {
        this("", null);
    }

    public Value(String label, T value) {
        this(label, value, false);
    }

    public Value(String label, T value, boolean required) {
        this.label = label;
        this.value = value;
        this.required = required;
    }

    /**
     * Get the label/name of this {@link Value}.
     * 
     * @return
     */
    public String label() {
        return label;
    }

    /**
     * Set the label/name of this value.
     *
     * @param label
     * @return
     */
    public Value label(String label) {
        this.label = label;
        return this;
    }

    /**
     * Get the object associated with this {@link Value}.
     * 
     * @return
     */
    public T get() {
        return value;
    }

    /**
     * Set the object associated with this {@link Value}.
     *
     * @param value
     * @return
     */
    public Value set(T value) {
        this.value = value;
        return this;
    }

    /**
     * Get the object of this {@link Value} as a String.
     * 
     * @return
     */
    public String as_string() {
        return value.toString();
    }

    /**
     * Get the object of this {@link Value} as an Integer.
     * 
     * @return
     */
    public int as_int() {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return Integer.parseInt(as_string());
        }
    }

	/**
     * Get the object of this {@link Value} as a Long.
     *
     * @return
     */
    public long as_long() {
        if (value instanceof Long) {
            return (Long) value;
        } else {
            return Long.parseLong(as_string());
        }
    }

	/**
     * Get the object of this {@link Value} as a Float.
     *
     * @return
     */
    public float as_float() {
        if (value instanceof Float) {
            return (Float) value;
        } else {
            return Float.parseFloat(as_string());
        }
    }

	/**
     * Get the object of this {@link Value} as a Double.
     *
     * @return
     */
    public double as_double() {
        if (value instanceof Double) {
            return (Double) value;
        } else {
            return Double.parseDouble(as_string());
        }
    }

    /**
     * Get the object of this {@link Value} as a Boolean.
     *
     * @return
     */
    public boolean as_boolean() {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return Boolean.parseBoolean(as_string());
        }
    }

    /**
     * Returns true if this {@link Value} is required, i.e. it can not be null or an empty string.
     * 
     * @return
     */
    public boolean required() {
        return required;
    }

	public Value required(boolean required) {
		this.required = true;
        return this;
	}

    /**
     * Set this {@link Value} as required, i.e. should not be null or contain an empty string.
     * 
     * @return
     */
    public Value require() {
        return required(true);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value value1 = (Value) o;

        if (required != value1.required) return false;
        if (label != null ? !label.equals(value1.label) : value1.label != null) return false;
        if (value != null ? !value.equals(value1.value) : value1.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        return result;
    }
}
