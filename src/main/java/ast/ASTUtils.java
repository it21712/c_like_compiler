package ast;


import org.objectweb.asm.Type;
import org.objectweb.asm.tree.JumpInsnNode;
import symbol.Info;
import symbol.LocalIndexPool;
import symbol.SimpleSymbolTable;

import java.util.ArrayList;
import java.util.List;


public class ASTUtils {

	public static final String SYMTABLE_PROPERTY = "SYMTABLE_PROPERTY";
	public static final String TYPE_PROPERTY = "TYPE_PROPERTY";
	public static final String PARENT_TYPE_PROPERTY = "PARENT_TYPE_PROPERTY";

	public static final String LOCAL_INDEX_POOL_PROPERTY = "LOCAL_INDEX_POOL_PROPERTY";

	public static final String IS_BOOLEAN_EXPR_PROPERTY = "IS_BOOLEAN_EXPR_PROPERTY";
	public static final String NEXT_LIST_PROPERTY = "NEXT_LIST_PROPERTY";
	public static final String BREAK_LIST_PROPERTY = "BREAK_LIST_PROPERTY";
	public static final String CONTINUE_LIST_PROPERTY = "CONTINUE_LIST_PROPERTY";
	public static final String TRUE_LIST_PROPERTY = "TRUE_LIST_PROPERTY";
	public static final String FALSE_LIST_PROPERTY = "FALSE_LIST_PROPERTY";

	public static final String LEFT_SIDE_EXPRESSION_PROPERTY = "LEFT_SIDE_EXPRESSION_PROPERTY";
	public static final String FIELD_PROPERTY = "FIELD_PROPERTY";
	public static final String STRUCT_NAME_PROPERTY = "STRUCT_NAME_PROPERTY";


	private ASTUtils() {
	}

	@SuppressWarnings("unchecked")
	public static SimpleSymbolTable<Info> getSymbolTable(ASTNode node) {
		return (SimpleSymbolTable<Info>) node.getProperty(SYMTABLE_PROPERTY);
	}

	@SuppressWarnings("unchecked")
	public static SimpleSymbolTable<Info> getSafeSymbolTable(ASTNode node) throws ASTVisitorException {
		SimpleSymbolTable<Info> symTable = (SimpleSymbolTable<Info>) node.getProperty(SYMTABLE_PROPERTY);
		if (symTable == null) {
			ASTUtils.error(node, "Symbol table not found.");
		}
		return symTable;
	}

	public static void setSymbolTable(ASTNode node, SimpleSymbolTable<Info> symbolTable) {
		node.setProperty(SYMTABLE_PROPERTY, symbolTable);
	}

	public static Type getType(ASTNode node) {
		return (Type) node.getProperty(TYPE_PROPERTY);
	}
	public static Type getSafeType(ASTNode node) throws ASTVisitorException {
		Type type = (Type) node.getProperty(TYPE_PROPERTY);
		if (type == null) {
			ASTUtils.error(node, "Type not found.");
		}
		return type;
	}
	public static void setType(ASTNode node, Type type) {
		node.setProperty(TYPE_PROPERTY, type);
	}

	public static Type getSafeParentType(ASTNode node) throws ASTVisitorException {
		Type type = (Type) node.getProperty(PARENT_TYPE_PROPERTY);
		if (type == null) {
			ASTUtils.error(node, "Type not found.");
		}
		return type;
	}

	public static void setParentType(ASTNode node, Type type){node.setProperty(PARENT_TYPE_PROPERTY, type);}

	public static void setLocalIndexPool(ASTNode node, LocalIndexPool pool) {
		node.setProperty(LOCAL_INDEX_POOL_PROPERTY, pool);
	}

	public static LocalIndexPool getSafeLocalIndexPool(ASTNode node) throws ASTVisitorException {
		LocalIndexPool lip = (LocalIndexPool) node.getProperty(LOCAL_INDEX_POOL_PROPERTY);
		if (lip == null) {
			ASTUtils.error(node, "Local index pool not found.");
		}
		return lip;
	}




	public static boolean isBooleanExpression(Expression node) {
		Boolean b = (Boolean) node.getProperty(IS_BOOLEAN_EXPR_PROPERTY);
		if (b == null) {
			return false;
		}
		return b;
	}

	@SuppressWarnings("unchecked")
	public static List<JumpInsnNode> getTrueList(Expression node) {
		List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(TRUE_LIST_PROPERTY);
		if (l == null) {
			l = new ArrayList<JumpInsnNode>();
			node.setProperty(TRUE_LIST_PROPERTY, l);
		}
		return l;
	}

	public static void setTrueList(Expression node, List<JumpInsnNode> list) {
		node.setProperty(TRUE_LIST_PROPERTY, list);
	}

	@SuppressWarnings("unchecked")
	public static List<JumpInsnNode> getFalseList(Expression node) {
		List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(FALSE_LIST_PROPERTY);
		if (l == null) {
			l = new ArrayList<JumpInsnNode>();
			node.setProperty(FALSE_LIST_PROPERTY, l);
		}
		return l;
	}

	public static void setFalseList(Expression node, List<JumpInsnNode> list) {
		node.setProperty(FALSE_LIST_PROPERTY, list);
	}

	@SuppressWarnings("unchecked")
	public static List<JumpInsnNode> getNextList(Statement node) {
		List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(NEXT_LIST_PROPERTY);
		if (l == null) {
			l = new ArrayList<JumpInsnNode>();
			node.setProperty(NEXT_LIST_PROPERTY, l);
		}
		return l;
	}

	public static void setNextList(Statement node, List<JumpInsnNode> list) {
		node.setProperty(NEXT_LIST_PROPERTY, list);
	}

	@SuppressWarnings("unchecked")
	public static List<JumpInsnNode> getBreakList(Statement node) {
		List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(BREAK_LIST_PROPERTY);
		if (l == null) {
			l = new ArrayList<JumpInsnNode>();
			node.setProperty(BREAK_LIST_PROPERTY, l);
		}
		return l;
	}

	public static void setBreakList(Statement node, List<JumpInsnNode> list) {
		node.setProperty(BREAK_LIST_PROPERTY, list);
	}

	@SuppressWarnings("unchecked")
	public static List<JumpInsnNode> getContinueList(Statement node) {
		List<JumpInsnNode> l = (List<JumpInsnNode>) node.getProperty(CONTINUE_LIST_PROPERTY);
		if (l == null) {
			l = new ArrayList<JumpInsnNode>();
			node.setProperty(CONTINUE_LIST_PROPERTY, l);
		}
		return l;
	}

	public static void setContinueList(Statement node, List<JumpInsnNode> list) {
		node.setProperty(CONTINUE_LIST_PROPERTY, list);
	}



	public static void setBooleanExpression(Expression node, boolean value) {
		node.setProperty(IS_BOOLEAN_EXPR_PROPERTY, value);
	}

	public static boolean getLeftSideExpressionProperty(Expression node){
		if(node.getProperty(LEFT_SIDE_EXPRESSION_PROPERTY) == null) return false;
		return (boolean)node.getProperty(LEFT_SIDE_EXPRESSION_PROPERTY);
	}

	public static void setLeftSideExpressionProperty(Expression node, boolean value){
		node.setProperty(LEFT_SIDE_EXPRESSION_PROPERTY, value);
	}

	public static void setFieldProperty(Statement node, boolean value){
		node.setProperty(FIELD_PROPERTY, value);
	}

	public static boolean isField(Statement node){
		if(node.getProperty(FIELD_PROPERTY) == null) return false;
		return (boolean) node.getProperty(FIELD_PROPERTY);
	}

	public static void setStructFieldProperty(Statement node, String value){
		node.setProperty(STRUCT_NAME_PROPERTY, value);
	}

	public static String getStructNameProperty(Statement node){
		return (String) node.getProperty(STRUCT_NAME_PROPERTY);
	}

	public static void error(ASTNode node, String message) throws ASTVisitorException {
		throw new ASTVisitorException(node.getLine() + ":" + node.getColumn() + ": " + message);
	}

}
