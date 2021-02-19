package org.jf.smalidea.util;

import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.jf.dexlib2.AccessFlags;
import org.jf.smalidea.psi.iface.SmaliModifierListOwner;
import org.jf.smalidea.psi.impl.SmaliModifierList;

import javax.swing.*;

public class IconUtils {
    public static Icon getElementIcon(SmaliModifierListOwner modifierListOwner, Icon leftIcon) {
        SmaliModifierList modifierList = modifierListOwner.getModifierList();
        int accessFlags = modifierList == null ? 0 : modifierList.getAccessFlags();
        Icon rightIcon = IconUtils.getAccessibilityIcon(accessFlags);
        if (rightIcon != null) {
            return new RowIcon(leftIcon, rightIcon);
        } else {
            return leftIcon;
        }
    }

    public static Icon getAccessibilityIcon(int accessFlags) {
        if (AccessFlags.PUBLIC.isSet(accessFlags)) {
            return PlatformIcons.PUBLIC_ICON;
        } else if (AccessFlags.PRIVATE.isSet(accessFlags)) {
            return PlatformIcons.PRIVATE_ICON;
        } else if (AccessFlags.PROTECTED.isSet(accessFlags)) {
            return PlatformIcons.PROTECTED_ICON;
        } else {
            return PlatformIcons.PACKAGE_LOCAL_ICON;
        }
    }
}
