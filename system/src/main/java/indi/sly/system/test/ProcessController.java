package indi.sly.system.test;

import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.services.faces.AController;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class ProcessController extends AController {
    @RequestMapping(value = {"/ProcessDisplay.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object processDisplay(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        UserSpaceDefinition userSpace = new UserSpaceDefinition();
        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        kernelSpace.getUserSpace().set(userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        UUID processID = null;
        String processIDText = request.getParameter("ProcessID");
        if (!ValueUtil.isAnyNullOrEmpty(processIDText)) {
            processID = ObjectUtil.transferFromStringOrDefaultProvider(UUID.class, processIDText, () -> {
                throw new StatusUnreadableException();
            });
        }
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            threadManager.create(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);
        } else {
            threadManager.create(processID);
        }

        //--Start--

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        ProcessStatusObject processStatus = process.getStatus();
        ProcessCommunicationObject processCommunication = process.getCommunication();
        ProcessContextObject processContext = process.getContext();
        ProcessInfoTableObject processInfoTable = process.getInfoTable();
        ProcessSessionObject processSession = process.getSession();
        ProcessStatisticsObject processStatistics = process.getStatistics();
        ProcessTokenObject processToken = process.getToken();

        Map<String, Object> processObject = new HashMap<>();
        Map<String, Object> processStatusObject = new HashMap<>();
        Map<String, Object> processCommunicationObject = new HashMap<>();
        Map<String, Object> processContextObject = new HashMap<>();
        Map<String, Object> processInfoTableObject = new HashMap<>();
        Map<String, Object> processSessionObject = new HashMap<>();
        Map<String, Object> processStatisticsObject = new HashMap<>();
        Map<String, Object> processTokenObject = new HashMap<>();

        processObject.put("id", process.getID());
        processObject.put("parentID", process.getParentID());
        processObject.put("status", processStatusObject);
        processObject.put("communication", processCommunicationObject);
        processObject.put("context", processContextObject);
        processObject.put("infoTable", processInfoTableObject);
        processObject.put("session", processSessionObject);
        processObject.put("statistics", processStatisticsObject);
        processObject.put("token", processTokenObject);

        processStatusObject.put("get", processStatus.get());

        processCommunicationObject.put("getShared", processCommunication.getShared());
        processCommunicationObject.put("getPortIDs", processCommunication.getPortIDs());
        processCommunicationObject.put("getSignalID", processCommunication.getSignalID());

        processContextObject.put("getType", processContext.getType());
        String[] identificationName = new String[processContext.getIdentifications().size()];
        for (int i = 0; i < processContext.getIdentifications().size(); i++) {
            identificationName[i] = processContext.getIdentifications().get(i).toString();
        }
        processContextObject.put("getIdentifications", identificationName.length == 0 ? "" : "/" + String.join("/", identificationName));
        processContextObject.put("getApplication", processContext.getApplication());
        processContextObject.put("getEnvironmentVariables", processContext.getEnvironmentVariables());
        processContextObject.put("getParameters", processContext.getParameters());
        processContextObject.put("getWorkFolder", processContext.getWorkFolder());

        processInfoTableObject.put("list", processInfoTable.list());

        processSessionObject.put("getID", processSession.getID());

        processStatisticsObject.put("getDate(Create)", processStatistics.getDate(DateTimeType.CREATE));
        processStatisticsObject.put("getDate(Access)", processStatistics.getDate(DateTimeType.ACCESS));
        processStatisticsObject.put("getStatistics", processStatistics.getStatistics());

        processTokenObject.put("getAccountID", processToken.getAccountID());
        processTokenObject.put("getLimits", processToken.getLimits());
        processTokenObject.put("getRoles", processToken.getRoles());

        return processObject;
    }

    @RequestMapping(value = {"/ProcessShutdown.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object processShutdown(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        UserSpaceDefinition userSpace = new UserSpaceDefinition();
        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        kernelSpace.getUserSpace().set(userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        UUID processID = null;
        String processIDText = request.getParameter("ProcessID");
        if (!ValueUtil.isAnyNullOrEmpty(processIDText)) {
            processID = ObjectUtil.transferFromStringOrDefaultProvider(UUID.class, processIDText, () -> {
                throw new StatusUnreadableException();
            });
        }
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            throw new StatusNotReadyException();
        } else {
            threadManager.create(processID);
        }

        //--Start--

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        processManager.endCurrent();

        return "Finished";
    }

    @RequestMapping(value = {"/ProcessStatus.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object processStatus(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init();

        UserSpaceDefinition userSpace = new UserSpaceDefinition();
        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        kernelSpace.getUserSpace().set(userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        UUID processID = null;
        String processIDText = request.getParameter("ProcessID");
        if (!ValueUtil.isAnyNullOrEmpty(processIDText)) {
            processID = ObjectUtil.transferFromStringOrDefaultProvider(UUID.class, processIDText, () -> {
                throw new StatusUnreadableException();
            });
        }
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            throw new StatusNotReadyException();
        } else {
            threadManager.create(processID);
        }

        //--Start--

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        ProcessStatusObject processStatus = process.getStatus();

        int status = 0;
        String statusText = request.getParameter("Status");
        if (!ValueUtil.isAnyNullOrEmpty(statusText)) {
            status = Integer.parseInt(statusText);
        }
        if (status == 2) {
            processStatus.run();
        } else if (status == 3) {
            processStatus.interrupt();
        }

        return "Finished";
    }
}
