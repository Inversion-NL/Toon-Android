package com.toonapps.toon.helper;

import android.content.Context;

import com.toonapps.toon.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ErrorMessage {

    private final Context context;

    public ErrorMessage(Context mContext) {
        this.context = mContext;
    }

    public String getHumanReadableErrorMessage(Exception exception) {
        if (exception instanceof UnknownHostException) return context.getString(R.string.exception_message_hostUnknown);
        else if (exception instanceof SocketTimeoutException) return context.getString(R.string.exception_message_timeout);
        else if (exception instanceof IllegalArgumentException) return context.getString(R.string.exception_message_incorrectHostname);
        else if (exception instanceof ConnectException) return context.getString(R.string.exception_message_noNetwork);
        else if (exception instanceof ToonException) {
            ToonException toonException = (ToonException) exception;
            switch (toonException.getType()) {

                case ToonException.FORBIDDEN:
                    return context.getString(R.string.exception_message_connectionRefused);

                case ToonException.NOT_FOUND:
                    return context.getString(R.string.exception_message_pageNotFound);

                case ToonException.UNAUTHORIZED:
                    return context.getString(R.string.exception_message_notAuthorized);

                case ToonException.UNSUPPORTED:
                    return context.getString(R.string.exception_message_unsupported);

                case ToonException.GETDEVICESERROR:
                    return context.getString(R.string.exception_message_getDevicesError) + " " + exception.getMessage();

                case ToonException.UNHANDLED:
                default:
                    return context.getString(R.string.exception_message_unhandled);
            }
        }
        else return exception.getMessage();
    }
}