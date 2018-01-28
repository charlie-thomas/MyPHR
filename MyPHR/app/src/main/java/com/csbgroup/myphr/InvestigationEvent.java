package com.csbgroup.myphr;

class InvestigationEvent {

    private String title;
    private String date;
    private String notes;

    public InvestigationEvent(String title, String date, String notes) {
        this.title = title;
        this.date = date;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }
}
