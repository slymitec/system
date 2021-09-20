package indi.sly.system.boot.test;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.processes.ProcessManager;
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
@Transactional
public class ProcessController extends AController {
    @RequestMapping(value = {"/ProcessSession.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object processSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Map<String, Object> result = new HashMap<>();
        Object ret = result;

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessSessionObject processSession = process.getSession();

        if (!ValueUtil.isAnyNullOrEmpty(processSession.getID())) {
            processSession.close();
        }

        ret = processSession.getID();

        return ret;
    }

    @RequestMapping(value = {"/ProcessShut.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object processShut(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        Object ret;

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        UUID processID = processManager.getCurrent().getID();

        if (processID.equals(this.kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID)) {
            String processIDText = request.getParameter("override");
            if (!"true".equals(processIDText)) {
                throw new ConditionRefuseException();
            }
        }

        processManager.endCurrent();

        ret = processID;

        return ret;
    }

    @RequestMapping(value = {"/ProcessDisplay.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object processDisplay(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

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

    @RequestMapping(value = {"/ProcessStatus.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object processStatus(int i, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessStatusObject processStatus = process.getStatus();

        if (i == 3) {
            processStatus.interrupt();
        } else if (i == 2) {
            processStatus.run();
        }

        return "finished";
    }

    @RequestMapping(value = {"/ProcessSignal.action"}, method = {RequestMethod.GET})
    @Transactional
    public Object processSignal(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        this.init(request, response, session);
        Object ret = "finished";

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();
        ProcessCommunicationObject processCommunication = process.getCommunication();

        ret = processCommunication.receiveSignals();

        return ret;
    }
}
