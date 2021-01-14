package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ISerializeCapable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ThreadContextDefinition implements ISerializeCapable<ThreadContextDefinition> {
    private String command;
    private int offset;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ThreadContextDefinition deepClone() {
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }
}
