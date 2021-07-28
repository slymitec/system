package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.ADefinition;

import java.util.*;

public class DumpDefinition extends ADefinition<DumpDefinition> {
    private final Map<Long, Long> date;
    private UUID processID;
    private UUID accountID;
    private final List<IdentificationDefinition> identifications;
    private InfoOpenDefinition infoOpen;

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

    public List<IdentificationDefinition> getIdentifications() {
        return this.identifications;
    }

    public InfoOpenDefinition getInfoOpen() {
        return this.infoOpen;
    }

    public void setInfoOpen(InfoOpenDefinition infoOpen) {
        this.infoOpen = infoOpen;
    }
}
