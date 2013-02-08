package userJavabean;

import java.util.LinkedList;
import java.util.List;

public class User14 {

    public User14(){
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

    private class InnerUser {
        private List<User1> list;
        private LinkedList linkedList;

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
    }
}
