package org.gsonformat.intellij.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import org.gsonformat.intellij.entity.ClassEntity;

/**
 * Created by dim on 16/11/8.
 */
public interface IProcessor {

    void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls);

    void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls);

    void onStartGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass);

    void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass);
}
