package indi.sly.system.boot.test;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.values.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@RestController
@Transactional
public class TestController {

    @Autowired
    private FactoryManager factoryManager;

    @RequestMapping(value = {"/T.action", "/T.do"}, method = {RequestMethod.GET})
    @Transactional
    public String T(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        System.out.println("----Start----");
        String ret = "finished...";

        this.factoryManager.startup(StartupType.STEP_INIT_SELF);
        KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();


        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        System.out.println(memoryManager != null);
        memoryManager.startup(StartupType.STEP_INIT_SELF);

        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();
        System.out.println(processRepository != null);
        assert processRepository != null;

        ProcessEntity process = new ProcessEntity();
        process.setID(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);
        process.setStatus(ProcessStatusType.RUNNING);
        process.setSessionID(UUIDUtil.getEmpty());
        process.setCommunication(ObjectUtil.transferToByteArray(new ProcessCommunicationDefinition()));
        process.setContext(ObjectUtil.transferToByteArray(new ProcessContextDefinition()));
        process.setInfoTable(ObjectUtil.transferToByteArray(new ProcessInfoTableDefinition()));
        process.setStatistics(ObjectUtil.transferToByteArray(new ProcessStatisticsDefinition()));
        process.setToken(ObjectUtil.transferToByteArray(new ProcessTokenDefinition()));

        processRepository.add(process);

        return ret;
    }
}
