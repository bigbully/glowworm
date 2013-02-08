package javabean;

import userJavabean.Person2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UserGeneric {

    private ArrayList<Person2> list;
    private HashSet<Person2> set;
    private Set<Person2> set1;

    public ArrayList<Person2> getList() {
        return list;
    }

    public void setList(ArrayList<Person2> list) {
        this.list = list;
    }

    public HashSet<Person2> getSet() {
        return set;
    }

    public void setSet(HashSet<Person2> set) {
        this.set = set;
    }

    public Set<Person2> getSet1() {
        return set1;
    }

    public void setSet1(Set<Person2> set1) {
        this.set1 = set1;
    }
}
