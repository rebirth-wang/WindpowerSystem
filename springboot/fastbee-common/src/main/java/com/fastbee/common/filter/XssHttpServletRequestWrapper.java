//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.html.EscapeUtil;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return super.getParameterValues(name);
        } else {
            int length = values.length;
            String[] escapesValues = new String[length];

            for(int i = 0; i < length; ++i) {
                escapesValues[i] = EscapeUtil.clean(values[i]).trim();
            }

            return escapesValues;
        }
    }

    public ServletInputStream getInputStream() throws IOException {
        if (!this.isJsonRequest()) {
            return super.getInputStream();
        } else {
            String json = IOUtils.toString(super.getInputStream(), "utf-8");
            if (StringUtils.isEmpty(json)) {
                return super.getInputStream();
            } else {
                json = EscapeUtil.clean(json).trim();
                final byte[] jsonBytes = json.getBytes("utf-8");
                final ByteArrayInputStream bis = new ByteArrayInputStream(jsonBytes);
                return new ServletInputStream() {
                    public boolean isFinished() {
                        return true;
                    }

                    public boolean isReady() {
                        return true;
                    }

                    public int available() throws IOException {
                        return jsonBytes.length;
                    }

                    public void setReadListener(ReadListener readListener) {
                    }

                    public int read() throws IOException {
                        return bis.read();
                    }
                };
            }
        }
    }

    public boolean isJsonRequest() {
        String header = super.getHeader("Content-Type");
        return StringUtils.startsWithIgnoreCase(header, "application/json");
    }
}
