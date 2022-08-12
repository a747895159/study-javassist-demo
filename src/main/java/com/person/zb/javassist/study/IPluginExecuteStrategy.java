package com.person.zb.javassist.study;

import java.lang.instrument.Instrumentation;

public interface IPluginExecuteStrategy {
    /**
     * 执行方法     * @param agentArgs     * @param inst
     */
    void execute(String agentArgs, Instrumentation inst);
}
