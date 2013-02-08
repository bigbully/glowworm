package userJavabean;

public class User11 {

    public User11(){
        this.innerUser = new InnerUser();
    }

    private InnerUser innerUser;

    public void putInnerArray(Object[] user1s){
        innerUser.setUser1s(user1s);
    }

    public Object[] findInnerArray(){
        return innerUser.getUser1s();
    }

    public InnerUser getInnerUser() {
        return innerUser;
    }

    public void setInnerUser(InnerUser innerUser) {
        this.innerUser = innerUser;
    }

    private class InnerUser{
        private Object[] user1s;

        public Object[] getUser1s() {
            return user1s;
        }

        public void setUser1s(Object[] user1s) {
            this.user1s = user1s;
        }
    }
}
