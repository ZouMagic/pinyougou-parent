package entity;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Excel implements Serializable {

    private String orderId;

    private String userId;

    private String receiver;

    private String receiverMobile;

    private String payment;

    private String paymentType;

    private String sourceType;

    private String status;

    private static String sourceTypeList[] = {"", "app端", "pc端", "M端", "微信端", "手机qq端"};
    private static String statusList[] = {"", "未付款", "已付款", "未发货", "已发货", "交易成功", "交易关闭", "待评价"};

    public Excel() {
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPaymentType() {
        return paymentType;
    }

    //支付类型，1、在线支付，2、货到付款
    public void setPaymentType(String paymentType) {
        if ("1".equals(paymentType)) {
            this.paymentType = "在线支付";
        } else {
            this.paymentType = "货到付款";
        }

    }

    public String getSourceType() {
        return sourceType;
    }

    //订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
    public void setSourceType(String sourceType) {
        this.sourceType = sourceTypeList[Integer.parseInt(sourceType)];
    }


    public String getStatus() {
        return status;
    }

    //状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
    public void setStatus(String status) {
        this.status = statusList[Integer.parseInt(status)];
    }

    @Override
    public String toString() {
        return "Excel{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", receiver='" + receiver + '\'' +
                ", receiverMobile='" + receiverMobile + '\'' +
                ", payment='" + payment + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
