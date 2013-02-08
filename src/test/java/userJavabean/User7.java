package userJavabean;

public class User7 {

    public User7(){
        this.innerUser = new InnerUser();
    }

    private InnerUser innerUser;

    public void putInnerBoolean(Boolean b){
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

    private class InnerUser{
        private Boolean b;

        public Boolean getB() {
            return b;
        }

        public void setB(Boolean b) {
            this.b = b;
        }
    }
}
