package indi.sly.system.common.lang;

public class SystemException extends ASystemException {
    public SystemException(ASystemException cause) {
        super(SystemException.class, cause);
    }
}
