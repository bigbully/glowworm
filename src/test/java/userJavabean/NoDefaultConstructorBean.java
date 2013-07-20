package userJavabean;

/**
 * User: bigbully
 * Date: 13-7-20
 * Time: 下午10:21
 */
public class NoDefaultConstructorBean {

    private String id;

    public NoDefaultConstructorBean(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
