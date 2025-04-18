package com.leon.common.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import org.springframework.util.StreamUtils;

public class ServletHttpLogRequest extends HttpServletRequestWrapper {

    private final byte[] requestBody;
    private final ServletInputStream inputStream;

    public ServletHttpLogRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.requestBody = StreamUtils.copyToByteArray(request.getInputStream());
        this.inputStream = createFromByteArray(this.requestBody);
    }

    public ServletInputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    private static ServletInputStream createFromByteArray(byte[] bytes) {
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return new ServletInputStream() {
            public boolean isFinished() {
                return bais.available() == 0;
            }

            public boolean isReady() {
                return true;
            }

            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException("setReadListener is not supported");
            }

            public int read() {
                return bais.read();
            }
        };
    }


    public Charset getCharset() {
        return Optional.ofNullable(this.getCharacterEncoding()).map(Charset::forName).filter((charset) -> !Objects.equals(charset, StandardCharsets.ISO_8859_1)).orElse(StandardCharsets.UTF_8);
    }

    public String getBodyAsString() {
        return new String(this.requestBody, this.getCharset());
    }

}
