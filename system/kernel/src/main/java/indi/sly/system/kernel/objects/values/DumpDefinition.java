package indi.sly.system.kernel.objects.values;

import indi.sly.system.kernel.objects.Identification;

import java.util.*;

public class DumpDefinition {
    private final Map<Long, Long> date;
    private UUID processID;
    private UUID accountID;
    private final List<Identification> identifications;
    private InfoStatusOpenDefinition open;

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

    public InfoStatusOpenDefinition getOpen() {
        return this.open;
    }

    public void setOpen(InfoStatusOpenDefinition open) {
        this.open = open;
    }
}
