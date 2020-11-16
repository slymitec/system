package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.kernel.objects.Identification;

import java.util.*;

public class DumpDefinition {
    private final Map<Long, Long> date;
    private UUID processID;
    private UUID accountID;
    private final List<Identification> identifications;
    private StatusOpenDefinition open;

    public DumpDefinition() {
        this.date = new HashMap<>();
        this.identifications = new ArrayList<>();
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }

    public UUID getAccountID() {
        return this.accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    public List<Identification> getIdentifications() {
        return this.identifications;
    }

    public StatusOpenDefinition getOpen() {
        return this.open;
    }

    public void setOpen(StatusOpenDefinition open) {
        this.open = open;
    }
}
