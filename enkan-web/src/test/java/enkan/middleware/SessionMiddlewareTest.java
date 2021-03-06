package enkan.middleware;

import enkan.data.*;
import enkan.middleware.session.MemoryStore;
import enkan.util.MixinUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static enkan.util.BeanBuilder.builder;
import static enkan.util.ReflectionUtils.tryReflection;
import static org.junit.Assert.*;

/**
 * @author kawasima
 */
public class SessionMiddlewareTest {
    @Test
    public void restoreSession() {
        SessionMiddleware middleware = new SessionMiddleware();
        HttpRequest request = builder(new DefaultHttpRequest())
                .build();
        request = MixinUtils.mixin(request, WebSessionAvailable.class);

        UUID sessionKey = UUID.randomUUID();
        Session session = new Session();
        session.put("name", "kawasima");

        tryReflection(() -> {
            Field storeField = SessionMiddleware.class.getDeclaredField("store");
            storeField.setAccessible(true);
            MemoryStore store = (MemoryStore) storeField.get(middleware);
            store.write(sessionKey.toString(), session);
            return store;
        });
        Map<String, Cookie> params = new HashMap<>();
        params.put("enkan-session", Cookie.create("enkan-session", sessionKey.toString()));
        request.setCookies(params);
        middleware.sessionRequest(request);
        assertEquals("kawasima", request.getSession().get("name"));
    }

    @Test
    public void sessionIdNotSent() {
        SessionMiddleware middleware = new SessionMiddleware();
        HttpRequest request = builder(new DefaultHttpRequest())
                .build();
        request = MixinUtils.mixin(request, WebSessionAvailable.class);

        UUID sessionKey = UUID.randomUUID();
        Session session = new Session();
        session.put("name", "kawasima");

        tryReflection(() -> {
            Field storeField = SessionMiddleware.class.getDeclaredField("store");
            storeField.setAccessible(true);
            MemoryStore store = (MemoryStore) storeField.get(middleware);
            store.write(sessionKey.toString(), session);
            return store;
        });
        middleware.sessionRequest(request);
        assertNull(request.getSession());
    }

}
