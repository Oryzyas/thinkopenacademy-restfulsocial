package com.thinkopen.restful.api.factories.responses;

import java.util.HashMap;
import java.util.Map;

public class XmlResponse implements Response {
    private String rootTag;
    private Map<String, String> values;

    public XmlResponse() {
        values = new HashMap<>();
    }

    @Override
    public void setRootTag(String name) {
        this.rootTag = name;
    }

    @Override
    public void addAttribute(String name, String value) {
        this.values.put(name, value);
    }

    @Override
    public String get() {
        final StringBuilder sb = new StringBuilder();

        sb.append("<");
        sb.append(rootTag);
        sb.append(">");

        for(Map.Entry<String, String> entry : values.entrySet()) {
            sb.append("<");
            sb.append(entry.getKey());
            sb.append(">");

            sb.append(entry.getValue());

            sb.append("</");
            sb.append(entry.getKey());
            sb.append(">");
        }

        sb.append("</");
        sb.append(rootTag);
        sb.append(">");

        return sb.toString();
    }

    @Override
    public String toString() {
        return get();
    }
}
