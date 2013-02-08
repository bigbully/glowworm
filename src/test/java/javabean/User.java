package javabean;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private List<Group> groups = new ArrayList<Group>();
    private User reportTo;

    public User() {
    }

    public User getReportTo() {
        return reportTo;
    }

    public void setReportTo(User reportTo) {
        this.reportTo = reportTo;
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String toString() {
        return this.name;
    }
}
