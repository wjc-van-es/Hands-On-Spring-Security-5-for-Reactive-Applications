package nl.vea.samples.springsecurity.app.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * {@link Filter} implementation that logs the HTTP Request body and response body of all traffic provided.
 * A logger is defined for this class at DEBUG level in {@code logback-spring.xml}.
 * <p>
 * We extend the class from {@link GenericFilterBean} so we can inject dependencies. If we simply implement
 * the {@link Filter} interface, we need some more effort to inject dependencies.
 */
@Component
public class TrafficContentLogger extends GenericFilterBean {

    private final Logger logger = LoggerFactory.getLogger(TrafficContentLogger.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        // Premature check whether debugging is enabled. We don't need to generate
        // too much cruft when in acceptance or production by default.
        if (logger.isDebugEnabled()) {
            // Request logging
            final ReusableStreamHttpServletRequest request = new ReusableStreamHttpServletRequest((HttpServletRequest) servletRequest);

            String requestCharset = request.getCharacterEncoding();
            if (requestCharset == null || requestCharset.isEmpty()) {
                requestCharset = "UTF-8";
            }

            String requestData = new String(request.rawData, requestCharset);
            String headers = mapToString(request.getHeaders());
            if (requestData.equals("")) {
                logger.debug("Request {} {}\n{}",
                        request.getMethod(), request.getRequestURI(), headers);
            } else {
                // Else, mention we have content and write it after a newline.
                logger.debug("Request {} {}\n{}\n\n{}",
                        request.getMethod(), request.getRequestURI(), headers, requestData);
            }

            // The response wrapper will intercept the response body as in-memory content to be available for logging
            final ReusableStreamHttpServletResponse response = new ReusableStreamHttpServletResponse((HttpServletResponse) servletResponse);

            // Set both the request and response wrapper to the filter chain
            filterChain.doFilter(request, response);

            //getting the response body as in-memory content to log it
            headers = mapToString(response.getHeaders());
            response.customServletOutputStream.baos.flush();
            final String responseBody = new String(response.customServletOutputStream.baos.toByteArray(), requestCharset);
            if (responseBody.equals("")) {
                logger.debug("Response {} {} ({})\n{}",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), headers);
            } else {
                logger.debug("Response {} {} ({})\n{}\n\n{}",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), headers, responseBody);
            }

            // necessary to push the response body in-memory content to the client as well as
            servletResponse.getWriter().write(responseBody);

        } else {
            //Only use our ReusableStreamHttpServletRequest when logging is set to debug level
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    /**
     * Returns a string representation of a map by separating the keys and values with an equals sign, and the entry
     * itself with a newline. For example:
     * <pre>
     * Set-Cookie=whatever
     * X-Application-Context=application:tst:8444
     * </pre>
     *
     * @param map The map to generate a String representation from.
     * @return The String.
     */
    private String mapToString(Map<String, String> map) {
        StringBuilder b = new StringBuilder();
        int currentSize = 0;
        for (String key : map.keySet()) {
            b.append(key);
            b.append("=");
            b.append(map.get(key));
            // prevent writing newline on last entry:
            if (currentSize < map.size() - 1) {
                b.append("\n");
            }
            currentSize++;
        }
        return b.toString();
    }

    private byte[] readBytesIntoMemory(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int read;
        byte[] data = new byte[1024];
        while ((read = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, read);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private class ReusableStreamHttpServletRequest extends
            HttpServletRequestWrapper {

        //In-memory representation of the whole request body
        private final byte[] rawData;

        public ReusableStreamHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            rawData = readBytesIntoMemory(request.getInputStream());
        }

        /**
         * Returns the request headers as a sorted map ({@link TreeMap}).
         *
         * @return The map with header names and their values.
         */
        public Map<String, String> getHeaders() {
            Map<String, String> map = new TreeMap<>();
            Enumeration<String> headerNames = getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                map.put(header, getHeader(header));
            }
            return map;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            //every call renders a new ServletInputStream with the same in-memory data
            //therefore this method never renders an InputStream that has already been read
            return new CustomServletInputStream(rawData);
        }

        @Override
        public BufferedReader getReader() throws IOException {

            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }

        private class CustomServletInputStream extends ServletInputStream {


            private ByteArrayInputStream buffer;

            public CustomServletInputStream(byte[] contents) {
                this.buffer = new ByteArrayInputStream(contents);
            }

            @Override
            public int read() throws IOException {
                return buffer.read();
            }

            @Override
            public boolean isFinished() {
                return buffer.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new RuntimeException("Not implemented");
            }
        }

    }

    private class ReusableStreamHttpServletResponse extends HttpServletResponseWrapper {
        //contains In-memory representation of the whole response body
        protected final CustomServletOutputStream customServletOutputStream;

        protected PrintWriter writer;

        protected boolean getOutputStreamCalled;

        protected boolean getWriterCalled;

        public ReusableStreamHttpServletResponse(HttpServletResponse response) {
            super(response);
            customServletOutputStream = new CustomServletOutputStream(new ByteArrayOutputStream());
        }


        /**
         * Returns the response headers as a sorted map ({@link TreeMap}).
         *
         * @return The map with header names and their values.
         */
        public Map<String, String> getHeaders() {
            Map<String, String> map = new TreeMap<>();
            for (String s : getHeaderNames()) {
                map.put(s, getHeader(s));
            }
            return map;
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (getWriterCalled) {
                throw new IllegalStateException("getWriter already called");
            }
            getOutputStreamCalled = true;
            return customServletOutputStream;
        }

        public PrintWriter getWriter() throws IOException {
            if (writer != null) {
                return writer;
            }
            if (getOutputStreamCalled) {
                throw new IllegalStateException("getOutputStream already called");
            }
            getWriterCalled = true;
            writer = new PrintWriter(new OutputStreamWriter(customServletOutputStream, getCharacterEncoding()));
            return writer;
        }

        private class CustomServletOutputStream extends ServletOutputStream {

            //In-memory representation of the whole response body
            protected final ByteArrayOutputStream baos;

            public CustomServletOutputStream(final ByteArrayOutputStream baos) {
                this.baos = baos;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                throw new RuntimeException("Not implemented");
            }

            @Override
            public void write(int b) throws IOException {
                baos.write(b);
            }
        }
    }

}
