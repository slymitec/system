package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ThreadRunDefinition extends ADefinition<ThreadRunDefinition> {
    private Class<?> clazz;
    private String name;
    private ISerializeCapable<?>[] arguments;
    private ISerializeCapable<?>[] results;
    private AKernelException exception;

    public Class<?> getClazz() {
        return this.clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public void setArguments(ISerializeCapable<?>[] arguments) {
        this.arguments = arguments;
    }

    public ISerializeCapable<?>[] getResults() {
        return this.results;
    }

    public void setResults(ISerializeCapable<?>[] results) {
        this.results = results;
    }

    public AKernelException getException() {
        return this.exception;
    }

    public void setException(AKernelException exception) {
        this.exception = exception;
    }

    @Override
    public ThreadRunDefinition deepClone() {
        ThreadRunDefinition definition = new ThreadRunDefinition();

        definition.clazz = this.clazz;
        definition.name = this.name;
        if (ObjectUtil.notNull(this.arguments)) {
            definition.arguments = new ISerializeCapable[this.arguments.length];
            for (int i = 0; i < this.arguments.length; i++) {
                definition.arguments[i] = (ISerializeCapable<?>) this.arguments[i].deepClone();
            }
        }
        if (ObjectUtil.notNull(this.results)) {
            definition.results = new ISerializeCapable[this.results.length];
            for (int i = 0; i < this.results.length; i++) {
                definition.results[i] = (ISerializeCapable<?>) this.results[i].deepClone();
            }
        }

        definition.exception = this.exception;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.clazz = ClassUtil.readExternal(in);
        this.name = StringUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        if (valueInteger < 0) {
            this.arguments = null;
        } else {
            this.arguments = new ISerializeCapable[valueInteger];
            for (int i = 0; i < valueInteger; i++) {
                this.arguments[i] = ObjectUtil.readExternal(in);
            }
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        if (valueInteger < 0) {
            this.results = null;
        } else {
            this.results = new ISerializeCapable[valueInteger];
            for (int i = 0; i < valueInteger; i++) {
                this.results[i] = ObjectUtil.readExternal(in);
            }
        }

        this.exception = ExceptionUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ClassUtil.writeExternal(out, this.clazz);
        StringUtil.writeExternal(out, this.name);

        if (ObjectUtil.notNull(this.arguments)) {
            NumberUtil.writeExternalInteger(out, this.arguments.length);

            for (ISerializeCapable<?> argument : this.arguments) {
                ObjectUtil.writeExternal(out, argument);
            }
        } else {
            NumberUtil.writeExternalInteger(out, -1);
        }

        if (ObjectUtil.notNull(this.results)) {
            NumberUtil.writeExternalInteger(out, this.results.length);

            for (ISerializeCapable<?> result : this.results) {
                ObjectUtil.writeExternal(out, result);
            }
        } else {
            NumberUtil.writeExternalInteger(out, -1);
        }

        ExceptionUtil.writeExternal(out, this.exception);
    }
}
