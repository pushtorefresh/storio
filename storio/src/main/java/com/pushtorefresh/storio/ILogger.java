package com.pushtorefresh.storio;

public interface ILogger {
    void v(String tag, String message);

    void d(String tag, String message);

    void i(String tag, String message);

    void w(String tag, String message);

    void e(String tag, String message);

    void e(String tag, String message, Throwable tr);

    int wtf(String tag, String message);

    int wtf(String tag, String message, Throwable tr);
}