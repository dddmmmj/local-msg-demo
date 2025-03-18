package org.ddmj.localmsg;

import java.util.Objects;


public class InvokeStatusHolder {
    private static final ThreadLocal<Boolean> INVOKE_THREAD_LOCAL = new ThreadLocal<>();

    public static boolean inInvoke() {
        return Objects.nonNull(INVOKE_THREAD_LOCAL.get());
    }

    public static void startInvoke() {
        INVOKE_THREAD_LOCAL.set(Boolean.TRUE);
    }

    public static void endInvoke() {
        INVOKE_THREAD_LOCAL.remove();
    }
}
