package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AChildDefinitionObject;
import indi.sly.system.kernel.processes.values.ThreadStatisticsDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.HashMap;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadStatisticsObject extends AChildDefinitionObject<ThreadStatisticsDefinition, ThreadObject> {
    public long getDate(long dataTime) {
        Long value = this.definition.getDate().getOrDefault(dataTime, null);

        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        return value;
    }

    public void setDate(long dataTime, long value) {
        this.definition.getDate().put(dataTime, value);
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        statistics.put("InfoCreate", this.definition.getInfoCreate());
        statistics.put("InfoGet", this.definition.getInfoGet());
        statistics.put("InfoQuery", this.definition.getInfoQuery());
        statistics.put("InfoDelete", this.definition.getInfoDelete());
        statistics.put("InfoDump", this.definition.getInfoDump());
        statistics.put("InfoOpen", this.definition.getInfoOpen());
        statistics.put("InfoClose", this.definition.getInfoClose());
        statistics.put("InfoRead", this.definition.getInfoRead());
        statistics.put("InfoWrite", this.definition.getInfoWrite());
        statistics.put("SharedReadCount", this.definition.getSharedReadCount());
        statistics.put("SharedReadBytes", this.definition.getSharedReadBytes());
        statistics.put("SharedWriteCount", this.definition.getSharedWriteCount());
        statistics.put("SharedWriteBytes", this.definition.getSharedWriteBytes());
        statistics.put("PortCount", this.definition.getPortCount());
        statistics.put("PortReadCount", this.definition.getPortReadCount());
        statistics.put("PortReadBytes", this.definition.getPortReadBytes());
        statistics.put("PortWriteCount", this.definition.getPortWriteCount());
        statistics.put("PortWriteBytes", this.definition.getPortWriteBytes());
        statistics.put("SignalReadCount", this.definition.getSignalReadCount());
        statistics.put("SignalWriteCount", this.definition.getSignalWriteCount());
        statistics.put("IoCreate", this.definition.getIoCreate());
        statistics.put("IoStatus", this.definition.getIoStatus());
        statistics.put("IoReadCount", this.definition.getIoReadCount());
        statistics.put("IoReadBytes", this.definition.getIoReadBytes());
        statistics.put("IoWriteCount", this.definition.getIoWriteCount());
        statistics.put("IoWriteBytes", this.definition.getIoWriteBytes());

        return CollectionUtil.unmodifiable(statistics);
    }

    public void addInfoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoCreate(value);
    }

    public void addInfoGet(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoGet(value);
    }

    public void addInfoQuery(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoQuery(value);
    }

    public void addInfoDelete(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoDelete(value);
    }

    public void addInfoDump(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoDump(value);
    }

    public long getInfoOpen() {
        return this.definition.getInfoOpen();
    }

    public void addInfoOpen(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoOpen(value);
    }

    public void addInfoClose(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoClose(value);
    }

    public void addInfoRead(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoRead(value);
    }

    public void addInfoWrite(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetInfoWrite(value);
    }

    public void addSharedReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetSharedReadCount(value);
    }

    public void addSharedReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetSharedReadBytes(value);
    }

    public void addSharedWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetSharedWriteCount(value);
    }

    public void addSharedWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }


        this.definition.offsetSharedWriteBytes(value);


    }

    public void addPortCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetPortCount(value);
    }

    public void addPortReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetPortReadCount(value);
    }

    public void addPortReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetPortReadBytes(value);
    }

    public void addPortWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetPortWriteCount(value);
    }

    public void addPortWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetPortWriteBytes(value);
    }

    public void addSignalReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetSignalReadCount(value);
    }

    public void addSignalWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }


        this.definition.offsetSignalWriteCount(value);


    }

    public void addIoCreate(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetIoCreate(value);
    }

    public void addIoStatus(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetIoStatus(value);
    }

    public void addIoReadCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetIoReadCount(value);
    }

    public void addIoReadBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetIoReadBytes(value);
    }

    public void addIoWriteCount(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetIoWriteCount(value);
    }

    public void addIoWriteBytes(long value) {
        if (value < 0) {
            throw new ConditionParametersException();
        }

        this.definition.offsetIoWriteBytes(value);
    }
}
