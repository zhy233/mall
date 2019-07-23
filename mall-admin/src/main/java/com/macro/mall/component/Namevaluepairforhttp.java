package com.macro.mall.component;

import org.apache.http.NameValuePair;

public class Namevaluepairforhttp implements NameValuePair {

    String name;
    String value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
