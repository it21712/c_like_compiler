package symbol;

import org.objectweb.asm.Type;
import types.TypeUtils;

import java.util.SortedSet;
import java.util.TreeSet;

public class LocalIndexPool {

    private final SortedSet<Integer> used;
    private int max;
    private int maxUsed;

    public LocalIndexPool() {
        this(Integer.MAX_VALUE);
    }

    public LocalIndexPool(int max) {
        this.used = new TreeSet<Integer>();
        this.max = max;
        this.maxUsed = 1; //0
    }

    public int getLocalIndex(Type type) {
        /*if (type.equals(Type.INT_TYPE) || type.equals(TypeUtils.STRING_TYPE)
                || type.equals(Type.getType("Ljava/lang/Object;"))
                || type.equals(Type.getType("Ljava/lang/Object"))

                || type.equals(Type.FLOAT_TYPE) || type.equals(Type.BOOLEAN_TYPE) || type.equals(Type.CHAR_TYPE)
                || type.equals(Type.getType("[I"))
                || type.equals(Type.getType("[F"))
                || type.equals(Type.getType("[C"))
                || type.equals(Type.getType("[Z"))
                ) {
            return getLocalIndex();
        } else {
            throw new IllegalArgumentException("Not supported type " + type);
        }*/
        Integer index = getLocalIndex();
        return index;
    }

    public void freeLocalIndex(int i, Type type) {
        if (type.equals(Type.INT_TYPE) || type.equals(TypeUtils.STRING_TYPE)
                || type.equals(Type.FLOAT_TYPE) || type.equals(Type.BOOLEAN_TYPE) || type.equals(Type.CHAR_TYPE)
                || type.equals(Type.getType("[I"))
                || type.equals(Type.getType("[F"))
                || type.equals(Type.getType("[C"))
                || type.equals(Type.getType("[Z"))
                || type.equals(Type.getType("Ljava/lang/Object;"))
                || type.equals(Type.getType("Ljava/lang/Object"))) {
            freeLocalIndex(i);
        } else {
            throw new IllegalArgumentException("Not supported type " + type);
        }
    }

    public int getLocalIndex() {
        for (int i = 1; i < max; i++) {
            if (!used.contains(i)) {
                used.add(i);
                if (i > maxUsed) {
                    maxUsed = i;
                }
                return i;
            }
        }
        throw new RuntimeException("Pool cannot contain more temporaries.");
    }

    public void freeLocalIndex(int t) {
        used.remove(t);
    }

    public int getDoubleLocalIndex() {
        for (int i = 0; i < max; i++) {
            if (!used.contains(i) && !used.contains(i + 1)) {
                used.add(i);
                used.add(i + 1);
                if (i + 1 > maxUsed) {
                    maxUsed = i + 1;
                }
                return i;
            }
        }
        throw new RuntimeException("Pool cannot contain more temporaries.");
    }

    public void freeDoubleLocalIndex(int t) {
        used.remove(t);
        used.remove(t + 1);
    }

    public int getMaxLocals() {
        return maxUsed;
    }

}
