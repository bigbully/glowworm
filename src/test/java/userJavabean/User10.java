package userJavabean;

public class User10 {

    public User10() {
        this.innerUser = new InnerUser();
    }

    private InnerUser innerUser;

    public void putInnerArray(User1[] user1s) {
        innerUser.setUser1s(user1s);
    }

    public User1[] findInnerArray() {
        return innerUser.getUser1s();
    }

    public InnerUser getInnerUser() {
        return innerUser;
    }

    public void setInnerUser(InnerUser innerUser) {
        this.innerUser = innerUser;
    }

    private class InnerUser {
        private User1[] user1s;

        public User1[] getUser1s() {
            return user1s;
        }

        public void setUser1s(User1[] user1s) {
            this.user1s = user1s;
        }
    }
}
