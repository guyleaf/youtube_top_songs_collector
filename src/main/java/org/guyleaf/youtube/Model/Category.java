package org.guyleaf.youtube.Model;

public class Category {
    private final int id;
    private final String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }
}
