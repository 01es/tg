package ua.com.fielden.platform.web.view.master.api.centre.impl;

import static java.lang.String.format;

import java.util.Optional;

import ua.com.fielden.platform.basic.IValueMatcherWithContext;
import ua.com.fielden.platform.dom.DomElement;
import ua.com.fielden.platform.dom.InnerTextElement;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.utils.ResourceLoader;
import ua.com.fielden.platform.web.centre.EntityCentre;
import ua.com.fielden.platform.web.interfaces.IRenderable;
import ua.com.fielden.platform.web.view.master.api.IMaster;

/**
 * An entity master that represents a single Entity Centre.
 *
 * @author TG Team
 *
 * @param <T>
 */
public class MasterWithCentreConfig<T extends AbstractEntity<?>> implements IMaster<T> {

    private final IRenderable renderable;

        public MasterWithCentreConfig(final Class<T> entityType, final boolean saveOnActivate, final EntityCentre<?> entityCentre) {
//            +"        detail.getMasterEntity = function () {\n"
//            +"            console.log('GET_MASTER_ENTITY!', savingInfoHolder);\n"
//            +"            return savingInfoHolder;\n" // savedEntity, context
//            +"        };\n"







            final StringBuilder attrs = new StringBuilder();
            attrs.append("{");
            if (entityCentre.isRunAutomatically()) {
                attrs.append("\"autoRun\": true, ");
            }
            if (entityCentre.eventSourceUri().isPresent()) {
                attrs.append(format("\"uri\": \"%s\"", entityCentre.eventSourceUri().get()));
            }

//            if (conf().entityCentrePrefDim.isPresent()) {
//                final PrefDim prefDim = conf().entityCentrePrefDim.get();
//                attrs.append(format("    prefDim: {'width': function() {return %s}, 'height': function() {return %s}, 'unit': '%s'},\n", prefDim.width, prefDim.height, prefDim.unit.value));
//            }
            attrs.append("}");

        final String entityMasterStr = ResourceLoader.getText("ua/com/fielden/platform/web/master/tg-entity-master-template.html")
                .replace("<!--@imports-->", "<link rel='import' href='/app/tg-element-loader.html'>\n")
                .replace("@entity_type", entityType.getSimpleName())
                .replace("<!--@tg-entity-master-content-->",
                        format(""
                        + "<tg-element-loader id='loader' context='[[_createContextHolderForEmbeddedViews]]' context-property='getMasterEntity' "
                        + "    import='/centre_ui/%s' "
                        + "    element-name='tg-%s-centre' "
                        + "    attrs='%s'>"
                        + "</tg-element-loader>",
                        entityCentre.getMenuItemType().getName(), entityCentre.getMenuItemType().getSimpleName(), attrs.toString()))
                .replace("//@ready-callback", "")
                .replace("@noUiValue", "false")
                .replace("@saveOnActivationValue", saveOnActivate + "");

        renderable = new IRenderable() {
            @Override
            public DomElement render() {
                return new InnerTextElement(entityMasterStr);
            }
        };

    }

    @Override
    public IRenderable render() {
        return renderable;
    }

    @Override
    public Optional<Class<? extends IValueMatcherWithContext<T, ?>>> matcherTypeFor(final String propName) {
        return Optional.empty();
    }

}
