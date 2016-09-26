package ua.com.fielden.platform.ui.config;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.DynamicEntityKey;
import ua.com.fielden.platform.entity.annotation.CompanionObject;
import ua.com.fielden.platform.entity.annotation.CompositeKeyMember;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Title;
import ua.com.fielden.platform.entity.validation.annotation.EntityExists;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.security.user.User;
import ua.com.fielden.platform.ui.config.api.IMainMenuItemInvisibility;

/**
 * This type controls visibility of the main menu items to users based on the specified base user, who is the <code>owner</code> of a {@link MainMenuItemInvisibility} instance. <br>
 * Simple existence of an instance of this type (mainly this means persisted in some way) indicates visibility of a corresponding main menu item to all users based on the specified
 * base user.
 * <p>
 * Instances of this type can be deleted only by base users controlling the main menu configuration for the derived users.
 *
 * @author TG Team
 *
 */
@KeyType(DynamicEntityKey.class)
@CompanionObject(IMainMenuItemInvisibility.class)
@MapEntityTo("MAIN_MENU_INVISIBLE")
public class MainMenuItemInvisibility extends AbstractEntity<DynamicEntityKey> {
    private static final long serialVersionUID = 1L;

    protected MainMenuItemInvisibility() {
        setKey(new DynamicEntityKey(this));
    }

    @IsProperty
    @CompositeKeyMember(1)
    @Title(value = "User", desc = "Application user owning this configuration.")
    @MapTo("ID_CRAFT")
    private User owner;

    @IsProperty
    @CompositeKeyMember(2)
    @Title(value = "Menu item URI", desc = "Menu item URI invisible to the user that is based on owning user.")
    @MapTo("MENU_ITEM_URI")
    private String menuItemUri;

    public String getMenuItemUri() {
        return menuItemUri;
    }

    @Observable
    public void setMenuItemUri(final String menuItemUri) {
        this.menuItemUri = menuItemUri;
    }

    public User getOwner() {
        return owner;
    }

    @Observable
    @EntityExists(User.class)
    public void setOwner(final User owner) {
        if (owner != null && !owner.isBase()) {
            throw new Result(this, new IllegalArgumentException("Only base users are allowed to be used for a base configuration."));
        }
        this.owner = owner;
    }

}
