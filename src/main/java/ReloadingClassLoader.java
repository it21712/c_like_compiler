import java.util.HashMap;
import java.util.Map;

public class ReloadingClassLoader extends ClassLoader {

    private final Map<String, byte[]> m;

    public ReloadingClassLoader(ClassLoader parent) {
        super(parent);
        m = new HashMap<String, byte[]>();
    }

    public void register(String name, byte[] bytes) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if (name.endsWith(".class")) {
            name = name.substring(0, name.length() - 6);
        }
        name = name.replace('/', '.');
        m.put(name, bytes);
    }

    void unregister(String name) {
        m.remove(name);
    }

    @Override
    public Class<?> loadClass(String s) throws ClassNotFoundException {
        if (!m.containsKey(s)) {
            return super.loadClass(s);
        } else {
            byte[] bytes = m.get(s);
            return defineClass(s, bytes, 0, bytes.length);
        }
    }


}
