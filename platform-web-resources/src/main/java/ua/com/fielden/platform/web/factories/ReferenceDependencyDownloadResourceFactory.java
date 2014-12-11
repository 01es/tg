package ua.com.fielden.platform.web.factories;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.web.resources.ReferenceDependencyDownloadResource;
import ua.com.fielden.platform.web.resources.RestServerUtil;

import com.google.inject.Injector;

/**
 * A factory for {@link ReferenceDependencyDownloadResource}.
 *
 * @author TG Team
 *
 */
public class ReferenceDependencyDownloadResourceFactory extends Restlet {
    private final String dependencyLocation;
    private final RestServerUtil restUtil;

    public ReferenceDependencyDownloadResourceFactory(final String dependencyLocation, final Injector injector) {
        this.dependencyLocation = dependencyLocation;
        this.restUtil = injector.getInstance(RestServerUtil.class);
    }

    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);
        if (Method.GET == request.getMethod()) {
            final ReferenceDependencyDownloadResource resource = new ReferenceDependencyDownloadResource(dependencyLocation, restUtil, getContext(), request, response);
            resource.handle();
        }
    }
}