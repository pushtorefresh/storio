package com.pushtorefresh.storio;

public class LoggerAdapter implements ILogger {

    @Override public void v(String subTag, String message) {

    }

    @Override public void d(String subTag, String message) {

    }

    @Override public void i(String subTag, String message) {

    }

    @Override public void w(String subTag, String message) {

    }

    @Override public void e(String subTag, String message) {

    }

    @Override public void e(String subTag, String message, Throwable tr) {

    }

    @Override public int wtf(String subTag, String message) {
        return 0;
    }

    @Override public int wtf(String subTag, String message, Throwable tr) {
        return 0;
    }
}
