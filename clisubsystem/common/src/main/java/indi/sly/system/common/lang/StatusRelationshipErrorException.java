package indi.sly.system.common.lang;

import java.io.Serial;

public class StatusRelationshipErrorException extends AKernelException {
	@Serial
	private static final long serialVersionUID = -1455316393879121309L;

	public StatusRelationshipErrorException() {
		super(StatusRelationshipErrorException.class);
	}
}
