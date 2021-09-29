package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class DumpDefinition extends ADefinition<DumpDefinition> {
    public DumpDefinition() {
        this.date = new HashMap<>();
        this.identifications = new ArrayList<>();
        this.securityDescriptorSummary = new ArrayList<>();
    }

    private final Map<Long, Long> date;
    private UUID processID;
    private UUID accountID;
    private final List<IdentificationDefinition> identifications;
    private InfoOpenDefinition infoOpen;
    private final List<SecurityDescriptorSummaryDefinition> securityDescriptorSummary;

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

    public List<SecurityDescriptorSummaryDefinition> getSecurityDescriptorSummary() {
        return this.securityDescriptorSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DumpDefinition that = (DumpDefinition) o;
        return date.equals(that.date) && Objects.equals(processID, that.processID) && Objects.equals(accountID, that.accountID) && identifications.equals(that.identifications) && Objects.equals(infoOpen, that.infoOpen) && securityDescriptorSummary.equals(that.securityDescriptorSummary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, processID, accountID, identifications, infoOpen, securityDescriptorSummary);
    }

    @Override
    public DumpDefinition deepClone() {
        DumpDefinition definition = new DumpDefinition();

        definition.date.putAll(this.date);
        definition.processID = this.processID;
        definition.accountID = this.accountID;
        definition.identifications.addAll(this.identifications);
        definition.infoOpen = this.infoOpen.deepClone();
        definition.securityDescriptorSummary.addAll(this.securityDescriptorSummary);

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.date.put(NumberUtil.readExternalLong(in), NumberUtil.readExternalLong(in));
        }

        this.processID = UUIDUtil.readExternal(in);
        this.accountID = UUIDUtil.readExternal(in);

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.identifications.add(ObjectUtil.readExternal(in));
        }

        this.infoOpen = ObjectUtil.readExternal(in);

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.securityDescriptorSummary.add(ObjectUtil.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (Map.Entry<Long, Long> pair : this.date.entrySet()) {
            NumberUtil.writeExternalLong(out, pair.getKey());
            NumberUtil.writeExternalLong(out, pair.getValue());
        }

        UUIDUtil.writeExternal(out, this.processID);
        UUIDUtil.writeExternal(out, this.accountID);

        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (IdentificationDefinition pair : this.identifications) {
            ObjectUtil.writeExternal(out, pair);
        }

        ObjectUtil.writeExternal(out, this.infoOpen);

        NumberUtil.writeExternalInteger(out, this.identifications.size());
        for (SecurityDescriptorSummaryDefinition pair : this.securityDescriptorSummary) {
            ObjectUtil.writeExternal(out, pair);
        }
    }
}
