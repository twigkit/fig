package twigkit.fig;

import java.io.Serializable;

/**
 * 
 * @param <T>
 */
public class Value<T> implements Serializable {

    private String name;
    private T value;
    private boolean required;

    public Value() {
        this("", null);
    }

    public Value(String name, T value) {
        this(name, value, false);
    }

    public Value(String name, T value, boolean required) {
        this.name = name;
        this.value = value;
        this.required = required;
    }

    public String name() {
        return name;
    }

    public Value name(String name) {
        this.name = name;
        return this;
    }

    public T get() {
        return value;
    }

    public Value set(T value) {
        this.value = value;
        return this;
    }

    public boolean required() {
        return required;
    }

    public Value require() {
        this.required = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value attribute = (Value) o;

        if (required != attribute.required) return false;
        if (name != null ? !name.equals(attribute.name) : attribute.name != null) return false;
        if (value != null ? !value.equals(attribute.value) : attribute.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (required ? 1 : 0);
        return result;
    }
}
