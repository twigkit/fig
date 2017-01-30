package twigkit.fig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @param <T>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Value<T> implements Serializable {

    @XmlElement
    private String label;
    @XmlElement
    private T value;
    @XmlElement
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
        if (value != null) {
            return value.toString();
        }
        return "";
    }

    /**
     * Get the object of this {@link Value} as an Integer.
     *
     * @return
     */
    public int as_int() {
        if (value != null) {
            if (value instanceof Integer) {
                return (Integer) value;
            } else {
                if (!as_string().isEmpty()) {
                    return Integer.parseInt(as_string());
                }
            }
        }

        return 0;
    }

    /**
     * Get the object of this {@link Value} as a Long.
     *
     * @return
     */
    public long as_long() {
        if (value != null) {
            if (value instanceof Long) {
                return (Long) value;
            } else {
                if (!as_string().isEmpty()) {
                    return Long.parseLong(as_string());
                }
            }
        }

        return 0L;
    }

    /**
     * Get the object of this {@link Value} as a Float.
     *
     * @return
     */
    public float as_float() {
        if (value != null) {
            if (value instanceof Float) {
                return (Float) value;
            } else {
                if (!as_string().isEmpty()) {
                    return Float.parseFloat(as_string());
                }
            }
        }

        return 0f;
    }

    /**
     * Get the object of this {@link Value} as a Double.
     *
     * @return
     */
    public double as_double() {
        if (value != null) {
            if (value instanceof Double) {
                return (Double) value;
            } else {
                if (!as_string().isEmpty()) {
                    return Double.parseDouble(as_string());
                }
            }
        }
        return 0d;
    }

    /**
     * Get the object of this {@link Value} as a Boolean.
     *
     * @return
     */
    public boolean as_boolean() {
        if (value != null) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else {
                return Boolean.parseBoolean(as_string());
            }
        }

        return false;
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
        return as_string();
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
