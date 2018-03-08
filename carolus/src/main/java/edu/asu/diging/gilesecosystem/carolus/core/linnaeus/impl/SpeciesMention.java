package edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl;

public class SpeciesMention {

    private int start;
    private int end;
    private String id;
    private String foundText;
    
    public SpeciesMention(int start, int end, String id, String foundText) {
        super();
        this.start = start;
        this.end = end;
        this.id = id;
        this.foundText = foundText;
    }
    
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getEnd() {
        return end;
    }
    public void setEnd(int end) {
        this.end = end;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFoundText() {
        return foundText;
    }
    public void setFoundText(String foundText) {
        this.foundText = foundText;
    }
    
}
