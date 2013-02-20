package userJavabean;

public class User8 {

    public User8() {
        this.innerUser = new InnerUser();
    }

    private InnerUser innerUser;

    public void putInnerBoolean(Boolean b) {
        innerUser.setB(b);
    }

    public InnerUser getInnerUser() {
        return innerUser;
    }

    public void setInnerUser(InnerUser innerUser) {
        this.innerUser = innerUser;
    }

    public boolean findInnerBoolean() {
        return innerUser.getB();
    }

    private class InnerUser {
        private boolean b;

        public boolean getB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }
    }
}
