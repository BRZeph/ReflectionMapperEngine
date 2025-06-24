package brzeph.spring.java_motordinamico_demo.motor.usageExample;

import brzeph.spring.java_motordinamico_demo.motor.core.annotation.apiCallType.Create;
import brzeph.spring.java_motordinamico_demo.motor.core.annotation.apiCallType.Read;
import brzeph.spring.java_motordinamico_demo.motor.core.annotation.validations.Required;
import brzeph.spring.java_motordinamico_demo.motor.core.annotation.apiCallType.Update;
import java.util.List;

public class OrderItem {

    @Read @Create
    @Required
    private String productCode;

    @Read @Create @Update
    @Required
    private Integer quantity;

    @Read @Create @Update
    private List<Payment> payments;

    @Read @Create @Update
    private Double discount;

    @Read @Create
    private Boolean backordered;

    @Override
    public String toString() {
        return "OrderItem{" +
                "productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                ", payments=" + payments +
                ", discount=" + discount +
                ", backordered=" + backordered +
                '}';
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Boolean getBackordered() {
        return backordered;
    }

    public void setBackordered(Boolean backordered) {
        this.backordered = backordered;
    }
}
