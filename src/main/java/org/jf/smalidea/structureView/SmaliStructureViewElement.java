package org.jf.smalidea.structureView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jf.smalidea.psi.impl.SmaliClass;
import org.jf.smalidea.psi.impl.SmaliFile;

import java.util.ArrayList;
import java.util.List;

public class SmaliStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private final NavigatablePsiElement element;

    public SmaliStructureViewElement(NavigatablePsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        element.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return element.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element.canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        String name = element.getName();
        return name != null ? name : "";
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        ItemPresentation presentation = element.getPresentation();
        return presentation != null ? presentation : new PresentationData();
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof SmaliFile) {
            SmaliFile smaliFile = (SmaliFile) element;
            SmaliClass[] classes = smaliFile.getClasses();
            TreeElement[] treeElements = new TreeElement[classes.length];
            for (int i = 0; i < classes.length; i++) {
                treeElements[i] = new SmaliStructureViewElement(classes[i]);
            }
            return treeElements;
        } else if (element instanceof SmaliClass) {
            SmaliClass smaliClass = (SmaliClass) element;
            PsiField[] fields = smaliClass.getFields();
            PsiMethod[] methods = smaliClass.getMethods();

            List<TreeElement> treeElements = new ArrayList<>(fields.length + methods.length);
            for (PsiField field : fields) {
                treeElements.add(new SmaliStructureViewElement(field));
            }
            for (PsiMethod method : methods) {
                treeElements.add(new SmaliStructureViewElement(method));
            }
            return treeElements.toArray(new TreeElement[0]);
        } else {
            return EMPTY_ARRAY;
        }
    }
}