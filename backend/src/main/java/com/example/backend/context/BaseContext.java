package com.example.backend.context;


public final class BaseContext {

    private static final ThreadLocal<Integer> CONTEXT_ID = new ThreadLocal<>();

    private BaseContext() {}

    public static void setContextId(Integer id) {
        CONTEXT_ID.set(id);
    }
    public static Integer getContextId() {
        return CONTEXT_ID.get();
    }
    public static void removeContextId() {
        CONTEXT_ID.remove();
    }
}