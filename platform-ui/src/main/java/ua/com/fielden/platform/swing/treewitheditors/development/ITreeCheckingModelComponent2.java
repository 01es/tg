package ua.com.fielden.platform.swing.treewitheditors.development;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.QuadristateCheckbox;

import javax.swing.tree.TreePath;

/**
 * Represents the component on the {@link MultipleCheckboxTree2} item
 * 
 * @author TG Team
 * 
 */
public interface ITreeCheckingModelComponent2 {

    /**
     * Returns the component that must be placed on the tree item
     * 
     * @return
     */
    QuadristateCheckbox getComponent();

    /**
     * Updates the components state according to the changes made in the {@link MultipleCheckboxTree2}
     * 
     * @param treePath
     */
    void updateComponent(final TreePath treePath);
}