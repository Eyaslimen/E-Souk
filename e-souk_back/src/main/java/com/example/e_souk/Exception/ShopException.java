package com.example.e_souk.Exception;

public class ShopException extends RuntimeException {
    private final String code;

    public ShopException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ShopException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
