package com.pushtorefresh.storio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private LogListener externalLogListener;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @NonNull
    private static String getTag() {
        return TAG;
    }

    @NonNull
    private static String getMessageWithTag(@NonNull String message) {
        return TAG + "/" + message;
    }

    public void setLogListener(@Nullable LogListener externalLogListener) {
        this.externalLogListener = externalLogListener;
    }

    public void v(@NonNull String message) {
        if (externalLogListener != null) {
            externalLogListener.v(getMessageWithTag(message));
        } else if (isEnabled) {
            Log.v(getTag(), message);
        }
    }

    public void d(@NonNull String message) {
        if (externalLogListener != null) {
            externalLogListener.d(getMessageWithTag(message));
        } else if (isEnabled) {
            Log.d(getTag(), message);
        }
    }

    public void i(@NonNull String message) {
        if (externalLogListener != null) {
            externalLogListener.i(getMessageWithTag(message));
        } else if (isEnabled) {
            Log.i(getTag(), message);
        }
    }

    public void w(@NonNull String message) {
        if (externalLogListener != null) {
            externalLogListener.w(getMessageWithTag(message));
        } else if (isEnabled) {
            Log.w(getTag(), message);
        }
    }

    public void w(@NonNull String message, @Nullable Throwable e) {
        final String fullMessage = message + ", ex: \n" + throwableToString(e);
        if (externalLogListener != null) {
            externalLogListener.w(getMessageWithTag(fullMessage));
        } else if (isEnabled) {
            Log.w(getTag(), fullMessage);
        }
    }

    public void e(@NonNull String message) {
        if (externalLogListener != null) {
            externalLogListener.e(getMessageWithTag(message));
        } else if (isEnabled) {
            Log.e(getTag(), message);
        }
    }

    public void e(@NonNull Throwable e) {
        final String message = throwableToString(e);
        if (externalLogListener != null) {
            externalLogListener.e(getMessageWithTag(message));
        } else if (isEnabled) {
            Log.e(getTag(), message);
        }
    }

    public void e(@NonNull String message, @Nullable Throwable e) {
        final String fullMessage = message + ", ex: \n" + throwableToString(e);
        if (externalLogListener != null) {
            externalLogListener.e(getMessageWithTag(fullMessage));
        } else if (isEnabled) {
            Log.e(getTag(), fullMessage);
        }
    }

    @NonNull
    private String throwableToString(@Nullable Throwable e) {
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
