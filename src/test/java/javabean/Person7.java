package javabean;

public class Person7 {

    private Long id;
    private String yName;
    private Person9 person9;

    public Person7() {
        this.id = 1L;
        this.yName = "123232";
        this.person9 = new Person9();
        this.person9.setId(1);
        this.person9.setName("123");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYName() {
        return yName;
    }

    public void setYName(String yName) {
        this.yName = yName;
    }

    public Person9 getPerson9() {
        return person9;
    }

    public void setPerson9(Person9 person9) {
        this.person9 = person9;
    }

    public int findPerson9Id() {
        return this.person9.getId();
    }

    public String findPerson9Name() {
        return this.person9.getName();
    }

    private class Person9 {
        private int id;
        private String name;

        private Person9() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
