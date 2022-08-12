package com.person.zb.javassist.study;

import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.ServiceLoader;

public class PluginPreMainExecutor implements IPluginExecuteStrategy {
    /**
     * 扫描加载的plugin，识别出@PreMainCondition并加载执行
     */
    @Override
    public void execute(String agentArgs, Instrumentation inst) {
        //获取前置执行集合
        List<String> pluginNames = AgentPluginAnnotationHelper.annoProcess(PreMainCondition.class);
        ServiceLoader<IPluginService> pluginServiceLoader = ServiceLoader.load(IPluginService.class);
        //只执行带有PreMainCondition的插件
        for (IPluginService pluginService : pluginServiceLoader) {
            if (pluginNames.contains(pluginService.getPluginName())) {
                pluginService.pluginLoad(agentArgs, inst);
            }
        }
    }
}
