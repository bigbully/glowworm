package javabean;

public class InnerBean3 {

    private Inner inner;

    public InnerBean3() {
    }

    public InnerBean3(String name) {
        this.inner = new Inner();
        this.inner.setName(name);
    }

    public Inner getInner() {
        return inner;
    }

    public void setInner(Inner inner) {
        this.inner = inner;
    }

    public String findName() {
        return this.inner.getName();
    }

    private class Inner {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
