package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.support.ISerializable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

public class ThreadStatisticsDefinition implements ISerializable<ThreadStatisticsDefinition> {
    public ThreadStatisticsDefinition() {
        this.date = new HashMap<>();
    }

    private Map<Long, Long> date;

    public Map<Long, Long> getDate() {
        return this.date;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public ThreadStatisticsDefinition deepClone() {
        return null;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }
}
