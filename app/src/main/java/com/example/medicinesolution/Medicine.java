package com.example.medicinesolution;

public class Medicine {
    String key;
    String value;

    public Medicine() {
    }

    public Medicine(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
