package userJavabean;

/**
 * User: biandi
 * Date: 13-7-10
 * Time: 下午3:26
 */
public class Broker {
    private Integer id;
    private String ip;
    private Integer port;
    private Boolean master;

//    private int load;//负载，用来表示当前broker的繁忙程度

//
//    public int compareTo(Broker o) {
//        return this.getId() - o.getId();
//    }
//
//
//    public boolean equals(Object obj) {
//        return this.id == ((Broker) obj).getId();
//    }

    public Broker() {
    }

    public Broker(Integer id, String ip, Integer port, Boolean master) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.master = master;
    }

    public String gnnetIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

//    public int getLoad() {
//        return load;
//    }
//
//    public void setLoad(int load) {
//        this.load = load;
//    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Boolean getMaster() {
        return master;
    }

    public void setMaster(Boolean master) {
        this.master = master;
    }
}
