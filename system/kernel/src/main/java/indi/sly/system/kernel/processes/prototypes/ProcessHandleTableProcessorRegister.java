package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProcessHandleTableProcessorRegister {
    public ProcessHandleTableProcessorRegister() {
        this.getDate = new ArrayList<>();
    }

    private Function<ProcessEntity, UUID> process;
    private final List<Function5<Map<Long, Long>, Map<Long, Long>, ProcessHandleTableObject,
            ProcessHandleTableDefinition, ProcessObject, UUID>> getDate;
}
