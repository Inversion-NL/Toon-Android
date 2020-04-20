package com.toonapps.toon.helper;

public class ToonException extends Exception{

    private final int exceptionType;
    private final Exception exception;

    public static final int FORBIDDEN = 0;
    public static final int NOT_FOUND = 1;
    public static final int UNAUTHORIZED = 2;
    public static final int UNSUPPORTED = 3;
    public static final int GETDEVICESERROR = 4;
    public static final int UNHANDLED = 99;

    public ToonException(int type, Exception e) {
        exceptionType = type;
        exception = e;
    }

    int getType() {
        return exceptionType;
    }

    public Exception getException() {
        return exception;
    }
}