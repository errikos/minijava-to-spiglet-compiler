package minijava.semantic_check.structures;

/**
 *
 */
public class PrimitiveType implements Type {

    Identifier typeName;

    public PrimitiveType(Identifier typeName) {
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  PrimitiveType) {
            PrimitiveType other = (PrimitiveType)obj;
            return this.typeName.equals(other.typeName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.typeName.hashCode();
    }

    @Override
    public String toString() {
        return this.typeName.toString();
    }

}
