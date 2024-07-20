package io.amplicode.jpa;

import org.springframework.aop.framework.Advised;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class TestUtils {

    public static Object getProxyTargetObject(Object proxy) throws Exception {
        if (!Proxy.isProxyClass(proxy.getClass())) {
            return proxy;
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);
        Field advisedField = invocationHandler.getClass().getDeclaredField("advised");
        advisedField.setAccessible(true);
        Object advisedValue = advisedField.get(invocationHandler);
        if (advisedValue instanceof Advised) {
            return ((Advised) advisedValue).getTargetSource().getTarget();
        }
        return proxy;
    }
}
