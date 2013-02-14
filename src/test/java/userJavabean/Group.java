package userJavabean;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String name;
    private List<User> members = new ArrayList<User>();

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public String toString() {
        return this.name;
    }
}
