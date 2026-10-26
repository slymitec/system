package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.Map;

public interface IProcessStatisticsProxy extends IProxy {
    Map<Long, Long> getDate();

    Map<String, Long> getStatistics();
}