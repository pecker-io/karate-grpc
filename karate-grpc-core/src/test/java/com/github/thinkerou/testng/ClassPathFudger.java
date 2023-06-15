package com.github.thinkerou.testng;

import com.github.thinkerou.utils.JarClassLoader;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class ClassPathFudger implements IInvokedMethodListener {

  @Override
  public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    if (method.isConfigurationMethod()) {
      return;
    }
    FudgeClassPath alter = method.getTestMethod().getConstructorOrMethod()
        .getMethod().getAnnotation(FudgeClassPath.class);
    if (alter == null) {
      return;
    }
    JarClassLoader.loadLibrary();
  }
}
