package ua.com.fielden.platform.web.master.api;

import ua.com.fielden.platform.basic.IValueMatcher;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.sample.domain.TgWorkOrder;
import ua.com.fielden.platform.web.interfaces.ILayout.Device;
import ua.com.fielden.platform.web.interfaces.ILayout.Orientation;
import ua.com.fielden.platform.web.master.api.actions.EnabledState;
import ua.com.fielden.platform.web.master.api.actions.pre.IPreAction;
import ua.com.fielden.platform.web.master.api.helpers.IPropertySelector;
import ua.com.fielden.platform.web.minijs.JsCode;

/**
 * This contract is an entry point for Simple Master API -- an embedded domain specific language for constructing simple entity masters.
 *
 * @see <a href="https://github.com/fieldenms/tg/wiki/Web-UI-Design:-Entity-Masters">Entity Masters Wiki</a>
 *
 * @author TG Team
 *
 */
public interface ISimpleMaster {
    public static class ShowMessageDlg implements IPreAction {
        public ShowMessageDlg(final String msg) {
        }

        @Override
        public JsCode build() {
            return null;
        }
    }

    public static class ToastUserWithMessage implements IPreAction {

        public ToastUserWithMessage(final String toastMsg) {
        }

        @Override
        public JsCode build() {
            return null;
        }
    }

    public static class StartInfiniteBlockingPane implements IPreAction {
        public StartInfiniteBlockingPane(final String msg, final String blockingPanelName) {
        }

        @Override
        public JsCode build() {
            return null;
        }
    }

    <T extends AbstractEntity<?>> IPropertySelector<T> forEntity(Class<T> type);

    // TODO Needs to be removed in a fullness of time. This method exists here purely to demonstrate API fluency as part of the development.
    public static void apiExample(final ISimpleMaster sm) {
        sm.forEntity(TgWorkOrder.class)
                .addProp("vehicle").asAutocompleter().byDesc()
                .withAction(TgWorkOrder.class) // should really except only functional entities
                .preAction(new StartInfiniteBlockingPane("Executing message...", "pane-name"))
                .postActionSuccess(new ToastUserWithMessage("Completed successfully")) // there is no need to imperatively state "stop infinite blocking pane", as this should be automatically understood from the context
                .postActionError(new ShowMessageDlg("The action has completed with error: {{error}}"))
                .enabledWhen(EnabledState.EDIT)
                .useIcon("icon name").shortDesc("could be used as title").longDesc("this description appeares in as a ")

                .also()
                .addProp("status").asAutocompleter().withMatcher(IValueMatcher.class).byDescOnly().also()
                .addDivider().withTitle("Section Header").atLevel1().also()
                .addDivider().atLevel2() // a subsection with no title
                .setLayoutFor(Device.DESKTOP, Orientation.LANDSCAPE, "[][flex]")
                .setLayoutFor(Device.TABLET, Orientation.LANDSCAPE, "[][flex]")
                .setLayoutFor(Device.TABLET, Orientation.PORTRAIT, "[][flex]")
                .done();
    }
}
