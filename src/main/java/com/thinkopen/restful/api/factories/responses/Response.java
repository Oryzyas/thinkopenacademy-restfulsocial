package com.thinkopen.restful.api.factories.responses;

public interface Response {

    void setRootTag(String name);
    void addAttribute(String name, String value);
    String get();
}
