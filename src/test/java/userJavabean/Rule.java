package userJavabean;

import java.io.Serializable;
import java.util.List;

/**
 * User: yfliuyu
 * Date: 12-8-27
 * Time: 上午10:28
 */
public class Rule implements Serializable {
    private String code;

    private String name;

    private String description;

    private String status;

    public boolean isEnable() {
        return status.equalsIgnoreCase("0");
    }

    private List<RuleItem> items;

    public List<RuleItem> getItems() {
        return items;
    }

    public void setItems(List<RuleItem> items) {
        this.items = items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", items=" + items +
                '}';
    }
}
