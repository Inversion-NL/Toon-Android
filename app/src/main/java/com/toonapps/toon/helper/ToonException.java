package com.toonapps.toon.helper;

public class ToonException extends Exception{

    private int exceptionType;

    public static final int FORBIDDEN = 0;
    public static final int NOT_FOUND = 1;
    public static final int UNAUTHORIZED = 2;
    public static final int UNHANDLED = 99;

    public ToonException(int type) {
        exceptionType = type;
    }

    public int getType() {
        return exceptionType;
    }
}