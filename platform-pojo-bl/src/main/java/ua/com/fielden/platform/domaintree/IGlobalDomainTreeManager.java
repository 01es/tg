package ua.com.fielden.platform.domaintree;

import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.master.IMasterDomainTreeManager;
import ua.com.fielden.platform.security.user.IUserProvider;

/**
 * This interface defines how domain tree can be managed in the global client application scope. It manages all entity-centres as well as locator managers in entity-masters.
 * To manage default type-related locators, please use {@link IGlobalDomainTreeRepresentation} member by accessing it with {@link #getGlobalRepresentation()} method.<br><br>
 *
 * The only single instance of this interface should be used for client application and persisted.
 *
 * @author TG Team
 *
 */
public interface IGlobalDomainTreeManager {
    /**
     * Returns a user provider. The user is a part of domain tree context. Some domain tree actions is permitted only for base users.
     * Some behaviour also can differ for different types of users.
     *
     * @return
     */
    IUserProvider getUserProvider();

    /**
     * Returns a global domain tree representation.
     *
     * @return
     */
    IGlobalDomainTreeRepresentation getGlobalRepresentation();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////// ENTITY CENTRE MANAGERS //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets a current version of <b>entity-centre manager</b> for domain type <b>root</b> with specified <b>name</b>.
     * The <b>name</b> should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre. <br><br>
     *
     * <b>User-driven constraints</b>: Base or non-base users can do nothing with non-visible (or non-existent) reports (throws {@link IllegalArgumentException}).
     * Non-base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports + its base user's reports including principle), but cannot save/remove base user's reports (throws {@link IllegalArgumentException}).
     * Base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports including principle), but cannot remove principle report (throws {@link IllegalArgumentException}). <br><br>
     *
     * The current version of a entity-centre manager should be initialised before usage ({@link #initEntityCentreManager(Class, String)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityCentreManager(Class, String)}), saved as non-principle entity-centre ({@link #saveAsEntityCentreManager(Class, String, String)}) or discarded ({@link #discardEntityCentreManager(Class, String)}).
     * After that it can be removed ({@link #removeEntityCentreManager(Class, String)}) and asked "if changed" ({@link #isChangedEntityCentreManager(Class, String)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-centre manager.
     * @param name -- should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre.
     * @return
     */
    ICentreDomainTreeManagerAndEnhancer getEntityCentreManager(final Class<?> root, final String name);

    /**
     * Freezes all the changes of a current version of <b>entity-centre manager</b> for domain type <b>root</b> with specified <b>name</b>.
     * The <b>name</b> should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre. <br><br>
     *
     * <b>User-driven constraints</b>: Base or non-base users can do nothing with non-visible (or non-existent) reports (throws {@link IllegalArgumentException}).
     * Non-base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports + its base user's reports including principle), but cannot save/remove base user's reports (throws {@link IllegalArgumentException}).
     * Base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports including principle), but cannot remove principle report (throws {@link IllegalArgumentException}). <br><br>
     *
     * The current version of a entity-centre manager should be initialised before usage ({@link #initEntityCentreManager(Class, String)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityCentreManager(Class, String)}), saved as non-principle entity-centre ({@link #saveAsEntityCentreManager(Class, String, String)}) or discarded ({@link #discardEntityCentreManager(Class, String)}).
     * After that it can be removed ({@link #removeEntityCentreManager(Class, String)}) and asked "if changed" ({@link #isChangedEntityCentreManager(Class, String)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-centre manager.
     * @param name -- should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre.
     * @return
     */
    void freezeEntityCentreManager(final Class<?> root, final String name);

    /**
     * Initialises a brand new <b>entity-centre manager</b> for domain type <b>root</b> with specified <b>name</b>.
     * The <b>name</b> should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre. <br><br>
     *
     * <b>User-driven constraints</b>: Base or non-base users can do nothing with non-visible (or non-existent) reports (throws {@link IllegalArgumentException}).
     * Non-base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports + its base user's reports including principle), but cannot save/remove base user's reports (throws {@link IllegalArgumentException}).
     * Base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports including principle), but cannot remove principle report (throws {@link IllegalArgumentException}). <br><br>
     *
     * The current version of a entity-centre manager should be initialised before usage ({@link #initEntityCentreManager(Class, String)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityCentreManager(Class, String)}), saved as non-principle entity-centre ({@link #saveAsEntityCentreManager(Class, String, String)}) or discarded ({@link #discardEntityCentreManager(Class, String)}).
     * After that it can be removed ({@link #removeEntityCentreManager(Class, String)}) and asked "if changed" ({@link #isChangedEntityCentreManager(Class, String)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-centre manager.
     * @param name -- should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre.
     */
    void initEntityCentreManager(final Class<?> root, final String name);

    /**
     * Discards a current version of <b>entity-centre manager</b> for domain type <b>root</b> with specified <b>name</b>.
     * If a current version of <b>entity-centre manager</b> was freezed then it just "discards" the changes after freezing.
     * The <b>name</b> should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre. <br><br>
     *
     * <b>User-driven constraints</b>: Base or non-base users can do nothing with non-visible (or non-existent) reports (throws {@link IllegalArgumentException}).
     * Non-base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports + its base user's reports including principle), but cannot save/remove base user's reports (throws {@link IllegalArgumentException}).
     * Base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports including principle), but cannot remove principle report (throws {@link IllegalArgumentException}). <br><br>
     *
     * Throws {@link IllegalArgumentException} when entity-centre was not initialised.<br><br>
     *
     * The current version of a entity-centre manager should be initialised before usage ({@link #initEntityCentreManager(Class, String)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityCentreManager(Class, String)}), saved as non-principle entity-centre ({@link #saveAsEntityCentreManager(Class, String, String)}) or discarded ({@link #discardEntityCentreManager(Class, String)}).
     * After that it can be removed ({@link #removeEntityCentreManager(Class, String)}) and asked "if changed" ({@link #isChangedEntityCentreManager(Class, String)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-centre manager.
     * @param name -- should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre.
     */
    void discardEntityCentreManager(final Class<?> root, final String name);

    /**
     * Saves a current version of <b>entity-centre manager</b> for domain type <b>root</b> with specified <b>name</b>.
     * If a current version of <b>entity-centre manager</b> was freezed then it just "applies" the changes after freezing.
     * The <b>name</b> should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre. <br><br>
     *
     * <b>User-driven constraints</b>: Base or non-base users can do nothing with non-visible (or non-existent) reports (throws {@link IllegalArgumentException}).
     * Non-base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports + its base user's reports including principle), but cannot save/remove base user's reports (throws {@link IllegalArgumentException}).
     * Base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports including principle), but cannot remove principle report (throws {@link IllegalArgumentException}). <br><br>
     *
     * Throws {@link IllegalArgumentException} when entity-centre was not initialised.<br><br>
     *
     * The current version of a entity-centre manager should be initialised before usage ({@link #initEntityCentreManager(Class, String)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityCentreManager(Class, String)}), saved as non-principle entity-centre ({@link #saveAsEntityCentreManager(Class, String, String)}) or discarded ({@link #discardEntityCentreManager(Class, String)}).
     * After that it can be removed ({@link #removeEntityCentreManager(Class, String)}) and asked "if changed" ({@link #isChangedEntityCentreManager(Class, String)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-centre manager.
     * @param name -- should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre.
     */
    void saveEntityCentreManager(final Class<?> root, final String name);

    /**
     * Duplicates an <b>entity-centre manager</b> for domain type <b>root</b> with <b>originalName</b> to an <b>entity-centre manager</b> with <b>newName</b> and then saves a manager copy.
     * The <b>originalName</b> and <b>newName</b> should represent a names of non-principle entity-centres or <code>null</code> for principle entity-centres. <br><br>
     *
     * <b>User-driven constraints</b>: Base or non-base users can do nothing with non-visible (or non-existent) reports (throws {@link IllegalArgumentException}).
     * Non-base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports + its base user's reports including principle), but cannot save/remove base user's reports (throws {@link IllegalArgumentException}).
     * Base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports including principle), but cannot remove principle report (throws {@link IllegalArgumentException}). <br><br>
     *
     * Throws {@link IllegalArgumentException} when entity-centre was not initialised.<br><br>
     *
     * The current version of a entity-centre manager should be initialised before usage ({@link #initEntityCentreManager(Class, String)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityCentreManager(Class, String)}), saved as non-principle entity-centre ({@link #saveAsEntityCentreManager(Class, String, String)}) or discarded ({@link #discardEntityCentreManager(Class, String)}).
     * After that it can be removed ({@link #removeEntityCentreManager(Class, String)}) and asked "if changed" ({@link #isChangedEntityCentreManager(Class, String)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-centre manager.
     * @param originalName -- should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre, which should be copied.
     * @param newName -- should represent a not empty (if empty -- throws {@link IllegalArgumentException}) name of new entity-centre.
     */
    void saveAsEntityCentreManager(final Class<?> root, final String originalName, final String newName);

    /**
     * Returns <code>true</code> if the current version of <b>entity-centre manager</b> for domain type <b>root</b> with specified <b>name</b> has been changed since last saving/discard (or since the beginning of manager history).
     * The <b>name</b> should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre. <br><br>
     *
     * <b>User-driven constraints</b>: Base or non-base users can do nothing with non-visible (or non-existent) reports (throws {@link IllegalArgumentException}).
     * Non-base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports + its base user's reports including principle), but cannot save/remove base user's reports (throws {@link IllegalArgumentException}).
     * Base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports including principle), but cannot remove principle report (throws {@link IllegalArgumentException}). <br><br>
     *
     * Throws {@link IllegalArgumentException} when entity-centre was not initialised.<br><br>
     *
     * The current version of a entity-centre manager should be initialised before usage ({@link #initEntityCentreManager(Class, String)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityCentreManager(Class, String)}), saved as non-principle entity-centre ({@link #saveAsEntityCentreManager(Class, String, String)}) or discarded ({@link #discardEntityCentreManager(Class, String)}).
     * After that it can be removed ({@link #removeEntityCentreManager(Class, String)}) and asked "if changed" ({@link #isChangedEntityCentreManager(Class, String)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-centre manager.
     * @param name -- should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre.
     */
    boolean isChangedEntityCentreManager(final Class<?> root, final String name);

    /**
     * Removes the <b>entity-centre manager</b> for domain type <b>root</b> with specified <b>name</b>. Throws {@link IllegalArgumentException} when manager does not exist. The manager to be removed can be persisted or not (but should exist - at least locally).
     * The <b>name</b> should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre. <br><br>
     *
     * <b>User-driven constraints</b>: Base or non-base users can do nothing with non-visible (or non-existent) reports (throws {@link IllegalArgumentException}).
     * Non-base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports + its base user's reports including principle), but cannot save/remove base user's reports (throws {@link IllegalArgumentException}).
     * Base users can init, access, modify, saveAs, ask for the changes etc. for all reports that are visible to him (its own reports including principle), but cannot remove principle report (throws {@link IllegalArgumentException}). <br><br>
     *
     * Throws {@link IllegalArgumentException} when entity-centre was not initialised.<br><br>
     *
     * The current version of a entity-centre manager should be initialised before usage ({@link #initEntityCentreManager(Class, String)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityCentreManager(Class, String)}), saved as non-principle entity-centre ({@link #saveAsEntityCentreManager(Class, String, String)}) or discarded ({@link #discardEntityCentreManager(Class, String)}).
     * After that it can be removed ({@link #removeEntityCentreManager(Class, String)}) and asked "if changed" ({@link #isChangedEntityCentreManager(Class, String)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-centre manager.
     * @param name -- should represent a name of non-principle entity-centre or <code>null</code> for principle entity-centre.
     */
    void removeEntityCentreManager(final Class<?> root, final String name);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////// ENTITY MASTER MANAGERS //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets a current version of <b>entity-master manager</b> for domain type <b>root</b>. <br><br>
     *
     * The current version of a entity-master manager should be initialised before usage ({@link #initEntityMasterManager(Class)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityMasterManager(Class)}) or discarded ({@link #discardEntityMasterManager(Class)}).
     * After that it can be asked "if changed" ({@link #isChangedEntityMasterManager(Class)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-master manager.
     * @return
     */
    IMasterDomainTreeManager getEntityMasterManager(final Class<?> root);

    /**
     * Initialises a brand new <b>entity-master manager</b> for domain type <b>root</b>. The initialisation uses own configuration (if exists) or base configuration (if exists) or raw instance creation -- for non-base user,
     * it uses own configuration (if exists) or raw instance creation -- for base user. <br><br>
     *
     * The current version of a entity-master manager should be initialised before usage ({@link #initEntityMasterManager(Class)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityMasterManager(Class)}) or discarded ({@link #discardEntityMasterManager(Class)}).
     * After that it can be asked "if changed" ({@link #isChangedEntityMasterManager(Class)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-master manager.
     */
    void initEntityMasterManager(final Class<?> root);

    /**
     * Discards a current version of <b>entity-master manager</b> for domain type <b>root</b>. <br><br>
     *
     * The current version of a entity-master manager should be initialised before usage ({@link #initEntityMasterManager(Class)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityMasterManager(Class)}) or discarded ({@link #discardEntityMasterManager(Class)}).
     * After that it can be asked "if changed" ({@link #isChangedEntityMasterManager(Class)}).<br><br>
     *
     * Throws {@link IllegalArgumentException} when entity-master was not initialised.<br><br>
     *
     * @param root -- a domain type relevant to an entity-master manager.
     */
    void discardEntityMasterManager(final Class<?> root);

    /**
     * Saves a current version of <b>entity-master manager</b> for domain type <b>root</b>. Non-base user can modify and save to perform base user's configuration overriding,
     * the base user can change and save "base" configuration. "Load default" action can be used by non-base users to retrieve and apply base configuration. <br><br>
     *
     * The current version of a entity-master manager should be initialised before usage ({@link #initEntityMasterManager(Class)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityMasterManager(Class)}) or discarded ({@link #discardEntityMasterManager(Class)}).
     * After that it can be asked "if changed" ({@link #isChangedEntityMasterManager(Class)}).<br><br>
     *
     * Throws {@link IllegalArgumentException} when entity-master was not initialised.<br><br>
     *
     * @param root -- a domain type relevant to an entity-master manager.
     */
    void saveEntityMasterManager(final Class<?> root);

//    /**
//     * Returns <code>true</code> if the current version of <b>entity-master manager</b> for domain type <b>root</b> has been changed since last saving/discard (or since the beginning of manager history). <br><br>
//     *
//     * The current version of a entity-master manager should be initialised before usage ({@link #initEntityMasterManager(Class)}),
//     * then can be altered by its methods, and then saved ({@link #saveEntityMasterManager(Class)}) or discarded ({@link #discardEntityMasterManager(Class)}).
//     * After that it can be asked "if changed" ({@link #isChangedEntityMasterManager(Class)}).<br><br>
//     *
//     * Throws {@link IllegalArgumentException} when entity-master was not initialised.<br><br>
//     *
//     * @param root -- a domain type relevant to an entity-master manager.
//     */
//    boolean isChangedEntityMasterManager(final Class<?> root);

    /**
     * Initialises a brand new <b>entity-master manager</b> for domain type <b>root</b> from default configuration. The initialisation uses base configuration (if exists) or raw instance creation -- for non-base user,
     * it uses raw instance creation -- for base user. <br><br>
     *
     * The current version of a entity-master manager should be initialised before usage ({@link #initEntityMasterManager(Class)}),
     * then can be altered by its methods, and then saved ({@link #saveEntityMasterManager(Class)}) or discarded ({@link #discardEntityMasterManager(Class)}).
     * After that it can be asked "if changed" ({@link #isChangedEntityMasterManager(Class)}).<br><br>
     *
     * @param root -- a domain type relevant to an entity-master manager.
     */
    void initEntityMasterManagerByDefault(final Class<?> root);
}
