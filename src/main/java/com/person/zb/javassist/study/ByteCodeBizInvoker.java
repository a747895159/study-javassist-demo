package com.person.zb.javassist.study;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.expr.ExprEditor;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

@Slf4j
public class ByteCodeBizInvoker implements ClassFileTransformer {
    /**
     * 在此处加载tprd-ut并利用类加载器加载
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //java自带的方法不进行处理
        if (loader == null) {
            return null;
        }        //增强spring5的componetscan，将org.tiny路径塞入
        if (className.contains("ComponentScanBeanDefinitionParser")) {
            try {
                System.out.println("增强spring");
                ClassPool classPool = new ClassPool(true);
                classPool.appendClassPath(ByteCodeBizInvoker.class.getName());
                CtClass ctClass = classPool.get(className.replace("/", "."));
                ClassFile classFile = ctClass.getClassFile();
                MethodInfo methodInfo = classFile.getMethod("parse");
                CtMethod ctMethod = ctClass.getDeclaredMethod("parse");
                addComponentScanPackage(methodInfo, ctMethod);
                return ctClass.toBytecode();
            } catch (Exception e) {
                log.error("handle spring 5 ComponentScanBeanDefinitionParser error", e);
            }
        }
    }

    /**
     * 遍历method，直至找到ReportTracer标记类     *     * @param ctMethod
     */
    private void addComponentScanPackage(MethodInfo methodInfo, CtMethod ctMethod) throws CannotCompileException {
        final boolean[] success = {false};
        CodeAttribute ca = methodInfo.getCodeAttribute();
        CodeIterator codeIterator = ca.iterator();
        //行遍历方法体
        while (codeIterator.hasNext()) {
            ExprEditor exprEditor = new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    String methodCallName = m.getMethodName();
                    if (methodCallName.equals("getAttribute")) {
                        //将org.tiny追加进去
                        m.replace("{ $_ = $proceed($$); $_ = $_ +  \",org.tiny.upgrade\";  }");
                        success[0] = true;
                    }
                }
            };
            ctMethod.instrument(exprEditor);
            if (success[0]) {
                break;
            }
        }
    }
}
