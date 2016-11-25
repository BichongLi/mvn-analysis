package com.ea.eadp.mvn.model.exception;

/**
 * User: BichongLi
 * Date: 11/25/2016
 * Time: 9:05 AM
 */
public class AnalyzeException extends RuntimeException {

    private ExceptionType type;

    public AnalyzeException(ExceptionType type) {
        super();
        this.type = type;
    }

    public AnalyzeException(ExceptionType type, String message) {
        super(message);
        this.type = type;
    }

    public AnalyzeException(ExceptionType type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public AnalyzeException(ExceptionType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public AnalyzeException(ExceptionType type, String messageFormat, Object... args) {
        super(String.format(messageFormat, args));
        this.type = type;
    }

    public ExceptionType getType() {
        return type;
    }
}
