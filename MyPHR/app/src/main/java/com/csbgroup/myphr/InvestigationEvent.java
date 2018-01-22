package com.csbgroup.myphr;

class InvestigationEvent {

    private String title;
    private String date;

    public InvestigationEvent(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
