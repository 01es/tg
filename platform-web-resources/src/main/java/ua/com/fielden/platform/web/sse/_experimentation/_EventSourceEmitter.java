package ua.com.fielden.platform.web.sse._experimentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletOutputStream;

import org.eclipse.jetty.continuation.Continuation;

/**
 * This EventSource emitter is taken from Jetty source in the attempt to make it working with Restlet.
 * TODO: Most likely it should be open sourced. The same goes about a corresponding Restlet web resource.
 *
 * @author TG Team
 *
 */
public class _EventSourceEmitter implements _EventSource.Emitter, Runnable {

    private static final byte[] CRLF = new byte[] { '\r', '\n' };
    private static final byte[] EVENT_FIELD = "event: ".getBytes(StandardCharsets.UTF_8);
    private static final byte[] DATA_FIELD = "data: ".getBytes(StandardCharsets.UTF_8);
    private static final byte[] COMMENT_FIELD = ": ".getBytes(StandardCharsets.UTF_8);

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private int heartBeatPeriod = 1;

    private final _EventSource eventSource;
    private final Continuation continuation;
    private final ServletOutputStream output;
    private Future<?> heartBeat;
    private boolean closed;

    public _EventSourceEmitter(final _EventSource eventSource, final Continuation continuation) throws IOException {
        this.eventSource = eventSource;
        this.continuation = continuation;
        this.output = continuation.getServletResponse().getOutputStream();
    }

    @Override
    public void event(final String name, final String data) throws IOException {
        synchronized (this) {
            output.write(EVENT_FIELD);
            output.write(name.getBytes(StandardCharsets.UTF_8));
            output.write(CRLF);
            data(data);
        }
    }

    @Override
    public void data(final String data) throws IOException {
        synchronized (this) {
            final BufferedReader reader = new BufferedReader(new StringReader(data));
            String line;
            while ((line = reader.readLine()) != null) {
                output.write(DATA_FIELD);
                output.write(line.getBytes(StandardCharsets.UTF_8));
                output.write(CRLF);
            }
            output.write(CRLF);
            flush();
        }
    }

    @Override
    public void comment(final String comment) throws IOException {
        synchronized (this) {
            output.write(COMMENT_FIELD);
            output.write(comment.getBytes(StandardCharsets.UTF_8));
            output.write(CRLF);
            output.write(CRLF);
            flush();
        }
    }

    @Override
    public void run() {
        // If the other peer closes the connection, the first
        // flush() should generate a TCP reset that is detected
        // on the second flush()
        try {
            synchronized (this) {
                output.write('\r');
                flush();
                output.write('\n');
                flush();
            }
            // We could write, reschedule heartbeat
            scheduleHeartBeat();
        } catch (final IOException x) {
            // The other peer closed the connection
            x.printStackTrace();
            close();
            eventSource.onClose();
        }
    }

    protected void flush() throws IOException {
        continuation.getServletResponse().flushBuffer();
    }

    @Override
    public void close() {
        synchronized (this) {
            closed = true;
            heartBeat.cancel(false);
            if (scheduler != null) {
                scheduler.shutdown();
            }
        }
        continuation.complete();
    }

    public void scheduleHeartBeat() {
        synchronized (this) {
            if (!closed) {
                heartBeat = scheduler.schedule(this, heartBeatPeriod, TimeUnit.SECONDS);
            }
        }
    }
}
