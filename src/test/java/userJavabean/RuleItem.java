package userJavabean;

import java.io.Serializable;

/**
 * User: yfliuyu
 * Date: 12-8-15
 * Time: 下午3:18
 */
public class RuleItem implements Serializable {
    private String rule_id;
    private String item_id;
    private String warning_value;

    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getWarning_value() {
        return warning_value;
    }

    public void setWarning_value(String warning_value) {
        this.warning_value = warning_value;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    @Override
    public String toString() {
        return "RuleItem{" +
                "rule_id='" + rule_id + '\'' +
                ", item_id='" + item_id + '\'' +
                ", warning_value='" + warning_value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
