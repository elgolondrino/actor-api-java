package im.actor.proto.api.parsers;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ex3ndr on 03.09.14.
 */
public abstract class SchemeParser {
    private HashMap<Integer, Parser<? extends Message>> keyToClassRpc = new HashMap<Integer, Parser<? extends Message>>();
    private HashMap<Class<? extends Message>, Integer> classToKeyRpc = new HashMap<Class<? extends Message>, Integer>();

    private boolean isInited = false;

    protected abstract void init();

    protected void register(int id, Parser<? extends Message> parser, Class<? extends Message> clazz) {
        if (keyToClassRpc.containsKey(id)) {
            throw new RuntimeException("Already registered class #" + id);
        }
        keyToClassRpc.put(id, parser);
        classToKeyRpc.put(clazz, id);
    }

    public synchronized <T extends Message> int getId(T obj) {
        if (!isInited) {
            init();
            isInited = true;
        }
        if (!classToKeyRpc.containsKey(obj.getClass())) {
            throw new RuntimeException("Unregistered class " + obj.getClass().getCanonicalName());
        }
        return classToKeyRpc.get(obj.getClass());
    }

    public synchronized Message read(int type, byte[] payload) throws IOException {
        if (!isInited) {
            init();
            isInited = true;
        }
        Parser<? extends Message> parserClass = keyToClassRpc.get(type);
        if (parserClass == null) {
            throw new IOException("Unknown class #" + type);
        }

        return parserClass.parseFrom(payload);
    }

    public synchronized <T extends Message> boolean supportMessage(T obj) {
        if (!isInited) {
            init();
            isInited = true;
        }
        return classToKeyRpc.containsKey(obj.getClass());
    }
}
