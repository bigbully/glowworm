package userJavabean;

public class User9 {

    public User9(){
        this.innerUser = new InnerUser();
    }

    private InnerUser innerUser;

    public void putInnerBean(User1 user1){
        innerUser.setUser1(user1);
    }

    public InnerUser getInnerUser() {
        return innerUser;
    }

    public void setInnerUser(InnerUser innerUser) {
        this.innerUser = innerUser;
    }

    public User1 findInnerUser1() {
        return innerUser.getUser1();
    }

    private class InnerUser{
        private User1 user1;

        public User1 getUser1() {
            return user1;
        }

        public void setUser1(User1 user1) {
            this.user1 = user1;
        }
    }
}
