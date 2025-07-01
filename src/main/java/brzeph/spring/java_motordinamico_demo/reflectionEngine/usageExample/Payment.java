package brzeph.spring.java_motordinamico_demo.reflectionEngine.usageExample;

import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.apiCallType.Create;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.apiCallType.Read;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.identity.MergeId;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.validations.NotBlank;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.validations.Required;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.apiCallType.Update;

public class Payment {

    @Create @Read @Update
    @Required @NotBlank @MergeId
    private String paymentId;

    @Create @Read @Update
    @Required
    private Double amount;

    @Read @Create
    private String paymentMethod;

    @Read @Create @Update
    private String transactionCode;

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionCode='" + transactionCode + '\'' +
                '}';
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
}
