package com.imsimarket.b2b.client.exceptions;

public class HttpNetworkException extends RuntimeException {

    public HttpNetworkException(String message) {
        super(message);
    }

    public HttpNetworkException(Throwable cause) {
        super(cause);
    }
}
