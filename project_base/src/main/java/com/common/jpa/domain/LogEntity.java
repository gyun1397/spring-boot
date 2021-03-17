package com.common.jpa.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;

@Data
@MappedSuperclass
public class LogEntity<ID> extends BasicEntity<ID> {
    @Column(name = "CREATE_ID", updatable = false)
    protected String    createId;   // 최초 생성자 ID
    @CreationTimestamp
//    @JsonSerialize(using = CustomDateSerializer.class)
    // @CreatedDate
    // @Temporal(TemporalType.TIMESTAMP) // java.util.Date이므로 @Temporal을 붙여준다.
    @Column(name = "CREATE_DT", updatable = false)
    protected LocalDateTime createDateTime; // 최초 생성일자
    @Column(name = "UPDATE_ID", updatable = true)
    protected String    updateId;   // 최종 수정자 ID
    @UpdateTimestamp
//    @JsonSerialize(using = CustomDateSerializer.class)
    // @LastModifiedDate
    // @Temporal(TemporalType.TIMESTAMP) // java.util.Date이므로 @Temporal을 붙여준다.
    @Column(name = "UPDATE_DT", updatable = true)
    protected LocalDateTime updateDateTime; // 최종 수정일자
    
    @PrePersist
    protected void onCreate() {
        createId = createId == null ? "system" : createId;
        updateId = updateId == null ? "system" : updateId;
        createDateTime = LocalDateTime.now();
        updateDateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateId = updateId == null ? "system" : updateId;
        updateDateTime = LocalDateTime.now();
    }
}
