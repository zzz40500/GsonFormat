package org.gsonformat.intellij.process;

import com.intellij.psi.*;
import org.gsonformat.intellij.entity.ConvertLibrary;
import org.gsonformat.intellij.entity.ClassEntity;


/**
 * Created by dim on 16/11/7.
 */
public class ClassProcess {

    private PsiElementFactory factory;
    private PsiClass cls;
    private Processor processor;

    public ClassProcess(PsiElementFactory factory, PsiClass cls) {
        this.factory = factory;
        this.cls = cls;
        processor = Processor.getProcessor(ConvertLibrary.from());
    }

    public void generate(ClassEntity classEntity) {
        processor.process(classEntity, factory, cls);
    }
}
