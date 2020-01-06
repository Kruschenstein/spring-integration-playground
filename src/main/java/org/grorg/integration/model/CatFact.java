package org.grorg.integration.model;

public class CatFact {
    private String fact;

    public CatFact(String fact) {
        this.fact = fact;
    }

    public String getFact() {
        return fact;
    }

    @Override
    public String toString() {
        return fact;
    }
}
