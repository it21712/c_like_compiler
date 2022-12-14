/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
package ast;

import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract syntax tree node.
 *
 * This is the basic tree node class. Every other tree node is an instance of
 * this node.
 *
 * <p>
 * This node has a method in order to be able to accept an abstract syntax tree
 * visitor. Moreover it contains a map in order to store properties at the
 * nodes.
 * </p>
 */
public abstract class ASTNode {

    private final Map<String, Object> properties;
    private int line;
    private int column;

    private boolean inLoop = false;

    private boolean _returnsValue = false;
    private Type parentType = Type.VOID_TYPE;
    private boolean _alwaysReturns = false;


    /**
     * Default Constructor
     */
    public ASTNode() {
        properties = new HashMap<String, Object>();
    }

    /**
     * Get a node property by name
     *
     * @param propertyName The property name
     * @return The value of the property
     */
    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * Set a property at the node by name
     *
     * @param propertyName The property name
     * @param data The value of the property
     */
    public void setProperty(String propertyName, Object data) {
        properties.put(propertyName, data);
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isInLoop() {
        return inLoop;
    }

    public void setInLoop(boolean inLoop) {
        this.inLoop = inLoop;
    }

    public boolean returnsValue() {
        return _returnsValue;
    }

    public void setReturnsValue(boolean returnsValue) {
        this._returnsValue = returnsValue;
    }

    public Type getParentType() {
        return parentType;
    }

    public void setParentType(Type parentType) {
        this.parentType = parentType;
    }

    public boolean alwaysReturns() {
        return _alwaysReturns;
    }

    public void setAlwaysReturns(boolean _alwaysReturns) {
        this._alwaysReturns = _alwaysReturns;
    }

    /**
     * Accept an abstract syntax tree visitor.
     *
     * @param visitor The AST visitor.
     * @throws ASTVisitorException In case a visitor error occurs.
     */
    public abstract void accept(ASTVisitor visitor) throws ASTVisitorException;

}
