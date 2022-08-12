package com.person.zb.javassist.study;

import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.ServiceLoader;

public class PluginAgentMainExecutor implements IPluginExecuteStrategy {
    /**
     * 扫描加载的plugin，识别出@AgentMainCondition并加载执行
     */
    @Override
    public void execute(String agentArgs, Instrumentation inst) {
        //获取后置执行集合
        List<String> pluginNames = AgentPluginAnnotationHelper.annoProcess(AgentMainCondition.class);
        ServiceLoader<IPluginService> pluginServiceLoader = ServiceLoader.load(IPluginService.class);
        for (IPluginService pluginService : pluginServiceLoader) {
            //只执行带有AgentMainCondition的插件
            if (pluginNames.contains(pluginService.getPluginName())) {
                pluginService.pluginLoad(agentArgs, inst);
            }
        }
    }
}
