package userJavabean;

import java.util.HashMap;

public class LoopPerson7 {

    private LoopPerson7 bro;
    private HashMap<String, LoopPerson7> map;

    public LoopPerson7 getBro() {
        return bro;
    }

    public void setBro(LoopPerson7 bro) {
        this.bro = bro;
    }

    public HashMap<String, LoopPerson7> getMap() {
        return map;
    }

    public void setMap(HashMap<String, LoopPerson7> map) {
        this.map = map;
    }
}
