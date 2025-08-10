package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.processes.values.ThreadStatisticsDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.HashMap;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadStatisticsObject extends AValueProcessObject<ThreadStatisticsDefinition, ThreadObject> {
    public long getDate(long dataTime) {
        this.lock(LockType.READ);
        this.init();

        Long value = this.value.getDate().getOrDefault(dataTime, null);

        this.unlock(LockType.READ);

        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        return value;
    }

    public void setDate(long dataTime, long value) {
        this.lock(LockType.WRITE);
        this.init();

        this.value.getDate().put(dataTime, value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        this.lock(LockType.READ);
        this.init();

        statistics.put("InfoCreate", this.value.getInfoCreate());
        statistics.put("InfoGet", this.value.getInfoGet());
        statistics.put("InfoQuery", this.value.getInfoQuery());
        statistics.put("InfoDelete", this.value.getInfoDelete());
        statistics.put("InfoDump", this.value.getInfoDump());
        statistics.put("InfoOpen", this.value.getInfoOpen());
        statistics.put("InfoClose", this.value.getInfoClose());
        statistics.put("InfoRead", this.value.getInfoRead());
        statistics.put("InfoWrite", this.value.getInfoWrite());
        statistics.put("SharedReadCount", this.value.getSharedReadCount());
        statistics.put("SharedReadBytes", this.value.getSharedReadBytes());
        statistics.put("SharedWriteCount", this.value.getSharedWriteCount());
        statistics.put("SharedWriteBytes", this.value.getSharedWriteBytes());
        statistics.put("PortCount", this.value.getPortCount());
        statistics.put("PortReadCount", this.value.getPortReadCount());
        statistics.put("PortReadBytes", this.value.getPortReadBytes());
        statistics.put("PortWriteCount", this.value.getPortWriteCount());
        statistics.put("PortWriteBytes", this.value.getPortWriteBytes());
        statistics.put("SignalReadCount", this.value.getSignalReadCount());
        statistics.put("SignalWriteCount", this.value.getSignalWriteCount());
        statistics.put("IoCreate", this.value.getIoCreate());
        statistics.put("IoStatus", this.value.getIoStatus());
        statistics.put("IoReadCount", this.value.getIoReadCount());
        statistics.put("IoReadBytes", this.value.getIoReadBytes());
        statistics.put("IoWriteCount", this.value.getIoWriteCount());
        statistics.put("IoWriteBytes", this.value.getIoWriteBytes());

        this.unlock(LockType.READ);

        return CollectionUtil.unmodifiable(statistics);
    }

    public void addInfoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoCreate(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addInfoGet(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoGet(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addInfoQuery(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoQuery(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addInfoDelete(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoDelete(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addInfoDump(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoDump(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public long getInfoOpen() {
        this.init();

        return this.value.getInfoOpen();
    }

    public void addInfoOpen(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoOpen(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addInfoClose(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoClose(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addInfoRead(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoRead(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addInfoWrite(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetInfoWrite(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addSharedReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSharedReadCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addSharedReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSharedReadBytes(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addSharedWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSharedWriteCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addSharedWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSharedWriteBytes(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addPortCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addPortReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortReadCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addPortReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortReadBytes(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addPortWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortWriteCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addPortWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetPortWriteBytes(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addSignalReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSignalReadCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addSignalWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetSignalWriteCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addIoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoCreate(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addIoStatus(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoStatus(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addIoReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoReadCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addIoReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoReadBytes(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addIoWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoWriteCount(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }

    public void addIoWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetIoWriteBytes(value);

        this.fresh();
        this.unlock(LockType.WRITE);
    }
}
