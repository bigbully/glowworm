
package com.jd.dd.glowworm;


public class PBException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PBException() {
        super();
    }

    public PBException(String message) {
        super(message);
    }

    public PBException(String message, Throwable cause) {
        super(message, cause);
    }
}
