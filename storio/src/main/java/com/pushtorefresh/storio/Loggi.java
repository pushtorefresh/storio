package com.pushtorefresh.storio;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Simple android Log proxy, includes default TAG and provides ability to turn logs on/off
 * <p/>
 * Call it "Loggi, come here!", you can think that Loggi is your little dog :)
 */
@SuppressWarnings({ "PMD.ProtectLogD", "PMD.ProtectLogV" })
public class Loggi {

    private static final String TAG = "StorIO";
    private volatile boolean isEnabled = BuildConfig.DEBUG;

    private ILogger externalLogger;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    private static String getTag() {
        return TAG;
    }

    private static String getTag(String subTag) {
        return TAG + "/" + subTag;
    }

    @NonNull public static String classNameAsTag(@NonNull Object o) {
        return o.getClass().getSimpleName();
    }

    public void setExternalLogger(ILogger externalLogger) {
        this.externalLogger = externalLogger;
    }

    public void v(String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag();
        if (externalLogger != null) {
            externalLogger.v(tag, message);
        } else {
            Log.v(tag, message);
        }
    }

    public void v(String subTag, String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        if (externalLogger != null) {
            externalLogger.v(tag, message);
        } else {
            Log.v(tag, message);
        }
    }

    public void d(String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag();
        if (externalLogger != null) {
            externalLogger.d(tag, message);
        } else {
            Log.d(tag, message);
        }
    }

    public void d(String subTag, String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        if (externalLogger != null) {
            externalLogger.d(tag, message);
        } else {
            Log.d(tag, message);
        }
    }

    public void i(String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag();
        if (externalLogger != null) {
            externalLogger.i(tag, message);
        } else {
            Log.i(tag, message);
        }
    }

    public void i(String subTag, String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        if (externalLogger != null) {
            externalLogger.i(tag, message);
        } else {
            Log.i(tag, message);
        }
    }

    public void w(String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag();
        if (externalLogger != null) {
            externalLogger.w(tag, message);
        } else {
            Log.w(tag, message);
        }
    }

    public void w(String subTag, String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        if (externalLogger != null) {
            externalLogger.w(tag, message);
        } else {
            Log.w(tag, message);
        }
    }

    public void w(String message, Throwable e) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag();
        final String fullMessage = message + ", ex: \n" + throwableToString(e);
        if (externalLogger != null) {
            externalLogger.w(tag, fullMessage);
        } else {
            Log.w(tag, fullMessage);
        }
    }

    public void w(String subTag, String message, Throwable e) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        final String fullMessage = message + ", ex: \n" + throwableToString(e);
        if (externalLogger != null) {
            externalLogger.w(tag, fullMessage);
        } else {
            Log.w(tag, fullMessage);
        }
    }

    public void e(String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag();
        if (externalLogger != null) {
            externalLogger.e(tag, message);
        } else {
            Log.e(tag, message);
        }
    }

    public void e(Throwable e) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag();
        final String message = throwableToString(e);
        if (externalLogger != null) {
            externalLogger.e(tag, message);
        } else {
            Log.e(tag, message);
        }
    }

    public void e(String subTag, String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        if (externalLogger != null) {
            externalLogger.e(tag, message);
        } else {
            Log.e(tag, message);
        }
    }

    public void e(String message, Throwable e) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag();
        if (externalLogger != null) {
            externalLogger.e(tag, message, e);
        } else {
            Log.e(tag, message + ", ex: \n" + throwableToString(e));
        }
    }

    public void e(String subTag, String message, Throwable e) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        if (externalLogger != null) {
            externalLogger.e(tag, message, e);
        } else {
            Log.e(tag, message + ", ex: \n" + throwableToString(e));
        }
    }

    public void wtf(String subTag, String message) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        if (externalLogger != null) {
            externalLogger.wtf(tag, message);
        } else {
            Log.wtf(tag, message);
        }
    }

    public void wtf(String subTag, String message, Throwable e) {
        if (!isEnabled()) {
            return;
        }
        final String tag = getTag(subTag);
        if (externalLogger != null) {
            externalLogger.wtf(tag, message, e);
        } else {
            Log.wtf(tag, message + ", ex: \n" + throwableToString(e));
        }
    }

    public String throwableToString(Throwable e) {
        if (e == null) {
            return "exception ref == null";
        }

        String message = e.getMessage();
        String stackTrace = Log.getStackTraceString(e);

        StringBuilder stringBuilder = new StringBuilder();

        if (!TextUtils.isEmpty(message)) {
            stringBuilder.append(message);
        } else {
            stringBuilder.append("ex message null or empty");
        }

        if (!TextUtils.isEmpty(stackTrace)) {
            stringBuilder.append(", stack trace:");
            stringBuilder.append(stackTrace);
        } else {
            stringBuilder.append(", stack trace is empty");
        }

        return stringBuilder.toString();
    }
}
