package userJavabean;

/**
 * Created by IntelliJ IDEA.
 * User: liuyanwei@360buy.com
 * Date: 12-8-11
 * Time: 下午12:38
 * To change this template use File | Settings | File Templates.
 *
 * 用户回忆录实体类
 * 
 */
public class MemoryRecord {

    private String pin;                                 //用户名
    private Integer level;                           //用户级别
    private String regTime;                      //注册时间
    private Integer firstWareId;
    private String firstWare;                   //首次购买商品名称
    private Integer ps1Id;
    private String ps1Name;                      //购买第一多的一级品类名称
    private Integer ps1WareNum;                     //购买第一多的一级品类商品种类数量
    private Integer ps2Id;
    private String ps2Name;                      //购买第二多的一级品类名称
    private Integer ps2WareNum;                     //购买第二多的一级品类商品种类数量
    private Integer ps3Id;
    private String ps3Name;                      //购买第三多的一级品类名称
    private Integer ps3WareNum;                     //购买第三多的一级品类商品种类数量
    private Integer ps4Id;
    private String ps4Name;                      //购买第四多的一级品类名称
    private Integer ps4WareNum;                     //购买第四多的一级品类商品种类数量
    private Integer ps5Id;
    private String ps5Name;                      //购买第五多的一级品类名称
    private Integer ps5WareNum;                     //购买第五多的一级品类商品种类数量
    private Integer allPsWareNum;                  //历史购买所有的商品种类数量
    private Integer commentNum;                     //评价次数
    private Integer showorderNum;                  //晒单次数
    private Integer help4Others;                   //帮助人数
    private Integer orderTotalNum;                //所有订单数
    private double allAmount;                 //所有订单涉及的金额
    private Integer allWareType;                   //所有商品种类数量
    private double consumerRanking;              //消费排名
    private double discountAmount;           //总优惠金额decimal(18,4)
    private String closestFourCreated;       //最接近凌晨4:00的下单时间(yyyy-MM-dd HH:mm:ss)

    public MemoryRecord(){}

    public Integer getFirstWareId() {
        return firstWareId;
    }

    public void setFirstWareId(Integer firstWareId) {
        this.firstWareId = firstWareId;
    }

    public Integer getPs1Id() {
        return ps1Id;
    }

    public void setPs1Id(Integer ps1Id) {
        this.ps1Id = ps1Id;
    }

    public Integer getPs2Id() {
        return ps2Id;
    }

    public void setPs2Id(Integer ps2Id) {
        this.ps2Id = ps2Id;
    }

    public Integer getPs3Id() {
        return ps3Id;
    }

    public void setPs3Id(Integer ps3Id) {
        this.ps3Id = ps3Id;
    }

    public Integer getPs4Id() {
        return ps4Id;
    }

    public void setPs4Id(Integer ps4Id) {
        this.ps4Id = ps4Id;
    }

    public Integer getPs5Id() {
        return ps5Id;
    }

    public void setPs5Id(Integer ps5Id) {
        this.ps5Id = ps5Id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getFirstWare() {
        return firstWare;
    }

    public void setFirstWare(String firstWare) {
        this.firstWare = firstWare;
    }

    public String getPs1Name() {
        return ps1Name;
    }

    public void setPs1Name(String ps1Name) {
        this.ps1Name = ps1Name;
    }

    public Integer getPs1WareNum() {
        return ps1WareNum;
    }

    public void setPs1WareNum(Integer ps1WareNum) {
        this.ps1WareNum = ps1WareNum;
    }

    public String getPs2Name() {
        return ps2Name;
    }

    public void setPs2Name(String ps2Name) {
        this.ps2Name = ps2Name;
    }

    public Integer getPs2WareNum() {
        return ps2WareNum;
    }

    public void setPs2WareNum(Integer ps2WareNum) {
        this.ps2WareNum = ps2WareNum;
    }

    public String getPs3Name() {
        return ps3Name;
    }

    public void setPs3Name(String ps3Name) {
        this.ps3Name = ps3Name;
    }

    public Integer getPs3WareNum() {
        return ps3WareNum;
    }

    public void setPs3WareNum(Integer ps3WareNum) {
        this.ps3WareNum = ps3WareNum;
    }

    public String getPs4Name() {
        return ps4Name;
    }

    public void setPs4Name(String ps4Name) {
        this.ps4Name = ps4Name;
    }

    public Integer getPs4WareNum() {
        return ps4WareNum;
    }

    public void setPs4WareNum(Integer ps4WareNum) {
        this.ps4WareNum = ps4WareNum;
    }

    public String getPs5Name() {
        return ps5Name;
    }

    public void setPs5Name(String ps5Name) {
        this.ps5Name = ps5Name;
    }

    public Integer getPs5WareNum() {
        return ps5WareNum;
    }

    public void setPs5WareNum(Integer ps5WareNum) {
        this.ps5WareNum = ps5WareNum;
    }

    public Integer getAllPsWareNum() {
        return allPsWareNum;
    }

    public void setAllPsWareNum(Integer allPsWareNum) {
        this.allPsWareNum = allPsWareNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public Integer getShoworderNum() {
        return showorderNum;
    }

    public void setShoworderNum(Integer showorderNum) {
        this.showorderNum = showorderNum;
    }

    public Integer getHelp4Others() {
        return help4Others;
    }

    public void setHelp4Others(Integer help4Others) {
        this.help4Others = help4Others;
    }

    public Integer getOrderTotalNum() {
        return orderTotalNum;
    }

    public void setOrderTotalNum(Integer orderTotalNum) {
        this.orderTotalNum = orderTotalNum;
    }

    public double getAllAmount() {
        return allAmount;
    }

    public void setAllAmount(double allAmount) {
        this.allAmount = allAmount;
    }

    public Integer getAllWareType() {
        return allWareType;
    }

    public void setAllWareType(Integer allWareType) {
        this.allWareType = allWareType;
    }

    public double getConsumerRanking() {
        return consumerRanking;
    }

    public void setConsumerRanking(double consumerRanking) {
        this.consumerRanking = consumerRanking;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getClosestFourCreated() {
        return closestFourCreated;
    }

    public void setClosestFourCreated(String closestFourCreated) {
        this.closestFourCreated = closestFourCreated;
    }
}
