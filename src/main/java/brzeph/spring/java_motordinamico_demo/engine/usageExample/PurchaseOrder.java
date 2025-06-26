package brzeph.spring.java_motordinamico_demo.engine.usageExample;

import brzeph.spring.java_motordinamico_demo.engine.core.annotation.apiCallType.Create;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.apiCallType.Read;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.validations.NotBlank;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.validations.Required;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.apiCallType.Update;

import java.time.LocalDateTime;
import java.util.List;
public class PurchaseOrder {

    @Read
    private Long id;

    @Read @Create
    @Required @NotBlank
    private String customerName;

    @Read @Update @Create
    @Required
    private LocalDateTime orderDate;

    @Read @Create @Update
    @Required
    private List<OrderItem> items;

    @Read
    private LocalDateTime createdAt;

    @Read @Create @Update
    private String notes;

    @Read @Update
    private String internalComments;

    private String secretData;

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", orderDate=" + orderDate +
                ", items=" + items +
                ", createdAt=" + createdAt +
                ", notes='" + notes + '\'' +
                ", internalComments='" + internalComments + '\'' +
                ", secretData='" + secretData + '\'' +
                '}';
    }

    public String getSecretData() {
        return secretData;
    }

    public void setSecretData(String secretData) {
        this.secretData = secretData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInternalComments() {
        return internalComments;
    }

    public void setInternalComments(String internalComments) {
        this.internalComments = internalComments;
    }
}
