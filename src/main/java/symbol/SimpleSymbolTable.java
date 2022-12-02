package symbol;

import org.objectweb.asm.Type;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSymbolTable<E extends Info> {

    private Map<String, E> table = new HashMap<String, E>();
    private SimpleSymbolTable<E> parent;
    public SimpleSymbolTable() {
        parent = null;
    }
    public SimpleSymbolTable(SimpleSymbolTable<E> parent) {
        this.parent = parent;
    }

    public E lookup(String s) {
        E r = table.get(s);
        if (r != null) {
            return r;
        }
        if (parent != null) {
            return parent.lookup(s);
        }
        return null;
    }




    public E lookupOnlyInTop(String s) {
        return table.get(s);
    }


    public void put(String name, E symbol) {
        table.put(name, symbol);
    }
    public E get(String name) {
        for(SimpleSymbolTable<E> t = this; t != null; t = t.parent) {
            E found = t.table.get(name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public E getOnlyInTop(String name) {
        return table.get(name);
    }

    public List<E> getSymbols() {
        List<E> symbols = new ArrayList<E>();
        symbols.addAll(table.values());
        if (parent != null) {
            symbols.addAll(parent.getSymbols());
        }
        return symbols;
    }

}
