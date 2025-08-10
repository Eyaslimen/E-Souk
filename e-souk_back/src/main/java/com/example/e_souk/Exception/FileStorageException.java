package com.example.e_souk.Exception;

/**
 * Exception personnalisée pour les erreurs liées au stockage de fichiers.
 */
public class FileStorageException extends RuntimeException {

    private final String code;

    public FileStorageException(String code, String message) {
        super(message);
        this.code = code;
    }

    public FileStorageException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
