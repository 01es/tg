package ua.com.fielden.platform.web.resources.webui;

import java.io.ByteArrayInputStream;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Encoding;
import org.restlet.engine.application.EncodeRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.common.base.Charsets;

import ua.com.fielden.platform.web.app.ISourceController;
import ua.com.fielden.platform.web.resources.RestServerUtil;

/**
 * Resource for tg-element-loader component.
 *
 * @author TG Team
 *
 * @param <T>
 * @param <DAO>
 */
public class TgElementLoaderComponentResource extends DeviceProfileDifferentiatorResource {
    public TgElementLoaderComponentResource(final ISourceController sourceController, final RestServerUtil restUtil, final Context context, final Request request, final Response response) {
        super(sourceController, restUtil, context, request, response);
    }


    /**
     * Handles sending of generated tg-element-loader to the Web UI client (GET method).
     */
    @Override
    protected Representation get() throws ResourceException {
        final String source = sourceController().loadSource("/app/tg-element-loader.html", deviceProfile());
        return new EncodeRepresentation(Encoding.GZIP, new InputRepresentation(new ByteArrayInputStream(source.getBytes(Charsets.UTF_8))));
    }
}
