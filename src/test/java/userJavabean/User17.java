package userJavabean;

public class User17 {

    public User17() {
        this.inner = new Inner();
    }

    private Inner inner;

    public Inner getInner() {
        return inner;
    }

    public void setInner(Inner inner) {
        this.inner = inner;
    }

    public void putException(Exception e) {
        this.inner.setException(e);
    }

    public Exception findException() {
        return this.inner.getException();
    }


    private class Inner {

        private Exception exception;

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }
}
