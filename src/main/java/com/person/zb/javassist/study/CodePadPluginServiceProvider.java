package com.person.zb.javassist.study;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

@AgentMainCondition
@Slf4jpublic
class CodePadPluginServiceProvider implements IPluginService {
    @Override
    public String getPluginName() {
        return "增强插件";
    }

    @Override
    public void pluginLoad(String agentArgs, Instrumentation inst) {
        //获取已加载的所有类
        Class<?>[] classes = inst.getAllLoadedClasses();
        if (classes == null || classes.length == 0) {
            return;
        }
        //需要将业务类进行retransform一下，这样可以避免在transform执行的时候，找不到此类的情况
        for (Class<?> clazz : classes) {
            if (clazz.getName().contains(entity.getClassName())) {
                try {
                    inst.retransformClasses(clazz);
                } catch (UnmodifiableClassException e) {
                    log.error("retransform class fail:" + clazz.getName(), e);
                }
            }
        }
        //进行增强操作
        inst.addTransformer(new ByteCodeBizInvoker(), true);
    }

    @Override
    public void pluginUnload() {
    }
}
