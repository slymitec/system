package indi.sly.system.common.exceptions;

public class StatusRelationshipErrorException extends AKernelException {
	private static final long serialVersionUID = -1455316393879121309L;

	public StatusRelationshipErrorException(long status, String message) {
		super(StatusRelationshipErrorException.class, message);
	}

	public StatusRelationshipErrorException() {
		super(StatusRelationshipErrorException.class);
	}
}
