package brzeph.spring.java_motordinamico_demo.engine.usageExample;

import brzeph.spring.java_motordinamico_demo.engine.core.annotation.apiCallType.Read;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.apiCallType.Update;
import brzeph.spring.java_motordinamico_demo.engine.core.mapper.impl.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final Map<Long, PurchaseOrder> database = new HashMap<>();
    private Long idCounter = 1L;

    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@RequestBody PurchaseOrder incomingOrder) {
        System.out.println("Incoming order: " + incomingOrder);
        incomingOrder.setId(idCounter++);
        incomingOrder.setCreatedAt(LocalDateTime.now());
        incomingOrder.setSecretData("Se isso apareceu no front, estou triste.");
        database.put(incomingOrder.getId(), incomingOrder);
        System.out.println("Created order: " + database.get(incomingOrder.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(incomingOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getOrder(@PathVariable Long id) {
        System.out.println("Incoming order: " + id);
        PurchaseOrder order = database.get(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        System.out.println("Found order: " + order);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrder> updateOrder(@PathVariable Long id, @RequestBody PurchaseOrder updatedOrder) {
        PurchaseOrder existingOrder = database.get(id);
        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }
        PurchaseOrder mergedOrder = Mapper.mergeForX(existingOrder, updatedOrder, Update.class);
        System.out.println("Existing order: " + existingOrder);
        System.out.println("Merged order: " + mergedOrder);
        System.out.println("Updated order: " + updatedOrder);
        database.put(id, mergedOrder);

        return ResponseEntity.ok(mergedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        System.out.println("Incoming order: " + id);
        if (database.remove(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
