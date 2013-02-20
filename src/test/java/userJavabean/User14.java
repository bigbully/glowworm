package userJavabean;

import java.util.LinkedList;
import java.util.List;

public class User14 {

    public User14() {
        innerUser = new InnerUser();
    }

    private InnerUser innerUser;

    public void putList(List<User1> list) {
        innerUser.setList(list);
    }

    public List<User1> findList() {
        return innerUser.getList();
    }

    public void putLinkedList(LinkedList linkedList) {
        innerUser.setLinkedList(linkedList);
    }

    public LinkedList findLinkedList() {
        return innerUser.getLinkedList();
    }

    public InnerUser getInnerUser() {
        return innerUser;
    }

    public void setInnerUser(InnerUser innerUser) {
        this.innerUser = innerUser;
    }

    public void putList2(List list) {
        this.innerUser.setList2(list);
    }

    public List findList2() {
        return this.innerUser.getList2();
    }

    public void putList3(List<Person1> list) {
        this.innerUser.setList3(list);
    }

    public List<Person1> findList3() {
        return innerUser.getList3();
    }

    private class InnerUser {
        private List<User1> list;
        private LinkedList linkedList;
        private List list2;
        private List<Person1> list3;

        public List<User1> getList() {
            return list;
        }

        public void setList(List<User1> list) {
            this.list = list;
        }

        public LinkedList getLinkedList() {
            return linkedList;
        }

        public void setLinkedList(LinkedList linkedList) {
            this.linkedList = linkedList;
        }

        public List getList2() {
            return list2;
        }

        public void setList2(List list2) {
            this.list2 = list2;
        }

        public List<Person1> getList3() {
            return list3;
        }

        public void setList3(List<Person1> list3) {
            this.list3 = list3;
        }
    }
}
