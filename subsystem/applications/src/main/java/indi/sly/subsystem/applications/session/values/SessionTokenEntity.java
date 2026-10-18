package indi.sly.subsystem.applications.session.values;

import indi.sly.system.common.values.APersistentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.util.UUID;

//@Entity
//@Table(name = "Session_Token")
public class SessionTokenEntity extends APersistentEntity {
    @SuppressWarnings("NullAway.Init")
    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    protected UUID id;
    @SuppressWarnings("NullAway.Init")
    @Column(name = "sessionId", nullable = false)
    protected UUID sessionId;
    @SuppressWarnings("NullAway.Init")
    @Column(name = "Token", nullable = false)
    protected Long expireTime;


}
