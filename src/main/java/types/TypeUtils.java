package types;

import java.util.Set;

import ast.Operator;
import org.objectweb.asm.Type;

/**
 * Helper class for type manipulations.
 */
public class TypeUtils {

	public static final Type STRING_TYPE = Type.getType(String.class);

	private TypeUtils() {
	}

	public static Type maxType(Type type1, Type type2) {
		if (type1.equals(STRING_TYPE)) {
			return type1;
		} else if (type2.equals(STRING_TYPE)) {
			return type2;
		} else if (type1.equals(Type.FLOAT_TYPE)) {
			return type1;
		} else if (type2.equals(Type.FLOAT_TYPE)) {
			return type2;
		} else if (type1.equals(Type.INT_TYPE)) {
			return type1;
		} else if (type2.equals(Type.INT_TYPE)) {
			return type2;
		} else if (type1.equals(Type.CHAR_TYPE)) {
			return type1;
		} else if (type2.equals(Type.CHAR_TYPE)) {
			return type2;
		} else if (type1.equals(Type.BOOLEAN_TYPE)) {
			return type1;
		} else if (type2.equals(Type.BOOLEAN_TYPE)) {
			return type2;
		} else {
			return type1;
		}
	}

	public static Type minType(Type type1, Type type2) {
		if (type1.equals(Type.BOOLEAN_TYPE)) {
			return type1;
		} else if (type2.equals(Type.BOOLEAN_TYPE)) {
			return type2;
		} else if (type1.equals(Type.INT_TYPE)) {
			return type1;
		} else if (type2.equals(Type.INT_TYPE)) {
			return type2;
		} else if (type1.equals(Type.DOUBLE_TYPE)) {
			return type1;
		} else if (type2.equals(Type.DOUBLE_TYPE)) {
			return type2;
		} else if (type1.equals(STRING_TYPE)) {
			return type1;
		} else if (type2.equals(STRING_TYPE)) {
			return type2;
		} else {
			return type1;
		}
	}

	public static boolean isLargerOrEqualType(Type type1, Type type2) {
		return type1.getSort() >= type2.getSort();
	}

	public static boolean isLargerType(Type type1, Type type2){
		return type1.getSort() > type2.getSort();
	}

	public static boolean isAssignable(Type target, Type source) {
		return isLargerOrEqualType(target, source);
	}

	public static Type maxType(Set<Type> types) {
		Type max = null;
		for (Type t : types) {
			if (max == null) {
				max = t;
			}
			max = maxType(max, t);
		}
		return max;
	}

	public static Type minType(Set<Type> types) {
		Type min = null;
		for (Type t : types) {
			if (min == null) {
				min = t;
			}
			min = minType(min, t);
		}
		return min;
	}

	public static boolean isUnaryComparible(Operator op, Type type) {
		switch (op) {
		case MINUS:
			return isNumber(type);
		case NOT:
			return isLogical(type);
		default:
			return false;
		}
	}

	public static boolean isNumber(Type type) {
		return type.equals(Type.INT_TYPE) || type.equals(Type.FLOAT_TYPE);
	}

	public static boolean isLogical(Type type){
		return type.equals(Type.BOOLEAN_TYPE);
	}

	public static boolean isNumber(Set<Type> types) {
		for (Type t : types) {
			if (t.equals(Type.INT_TYPE) || t.equals(Type.DOUBLE_TYPE)) {
				return true;
			}
		}
		return false;
	}

	public static Type applyUnary(Operator op, Type type) throws TypeException {
		if (!op.isUnary()) {
			throw new TypeException("Operator " + op + " is not unary");
		}
		if (!TypeUtils.isUnaryComparible(op, type)) {
			throw new TypeException("Type " + type + " is not unary comparible");
		}
		return type;
	}


	public static Type applyBinary(Operator op, Type t1, Type t2) throws TypeException {
		if (op.isRelational()) {
			if(isNumber(t1) && isNumber(t2))
				return Type.BOOLEAN_TYPE;
			else throw new TypeException("Relational operations can only be applied between numbers");
		}else if(op.isMathematical()){
			if (isNumber(t1) && isNumber(t2))
				return maxType(t1, t2);
			else throw new TypeException("Mathematical operations can only be applied between numbers");
		}else if(op.isLogical()){
			if(isLogical(t1) && isLogical(t2))
				return Type.BOOLEAN_TYPE;
			else throw new TypeException("Logical operations can only be applied between logical expressions");
		}else
			throw new TypeException("Operator " + op + " not supported");
	}

	public static int getTypeCode(Type type){
		if (type.equals(Type.INT_TYPE)) return 10;
		if (type.equals(Type.FLOAT_TYPE)) return 6;
		if(type.equals(Type.BOOLEAN_TYPE)) return 4;
		if(type.equals(Type.CHAR_TYPE)) return 5;
		return -2;
	}

}
