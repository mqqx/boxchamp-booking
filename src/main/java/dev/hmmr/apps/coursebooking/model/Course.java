package dev.hmmr.apps.coursebooking.model;

// TODO generate from api
public enum Course {
    ENDURANCE("Endurance"),
    FITNESS("Fitness"),
    GYMNASTICS("Gymnastics"),
    MASTERS("Masters"),
    MOBILITY("Mobility"),
    OLYMPIC_WEIGHTLIFTING("Olympic Weightlifting"),
    OPEN_BOX("Open Box"),
    PERFORMANCE("Performance"),
    RUNNING("Running");

    private String name;

    Course(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
