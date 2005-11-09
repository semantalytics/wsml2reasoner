package org.wsml.reasoner.builtin.kaon2;

public class WsmlBoolean {
    
    private Boolean value = Boolean.FALSE;

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
    
    public WsmlBoolean(Boolean value) {
        this.value = value;
    }
    
    public String toString() {
        return this.value.toString();
    }
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if ((object == null) || (object.getClass() != this.getClass()))
            return false;
        WsmlBoolean other = (WsmlBoolean) object;
        return (value == other.value || (value != null && value.equals(other.value)));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == value ? 0 : value.hashCode());
        return hash;
    }

}
