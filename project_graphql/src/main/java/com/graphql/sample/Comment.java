package com.graphql.sample;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "COMMENT") // 댓글
@NoArgsConstructor
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLQuery(name = "id")
    protected Long          id;
    @Column(name = "BOARD_ID")
    @GraphQLQuery(name = "boardId")
    private Long          boardId;
    @Column(name = "COMMENT")
    @GraphQLQuery(name = "comment")
    private String        comment;        // 댓글
    @Column(name = "DEL_YN", insertable = false)
    @ColumnDefault("0")
    @GraphQLQuery(name = "isDelete")
    private Boolean       isDelete;       // 삭제여부
    @Column(name = "CREATE_ID", updatable = false)
    @GraphQLQuery(name = "createId")
    protected String        createId;       // 최초 생성자 ID
    @CreationTimestamp
    @Column(name = "CREATE_DT", updatable = false)
    @GraphQLQuery(name = "createDateTime")
    protected LocalDateTime createDateTime; // 최초 생성일자
    @Column(name = "UPDATE_ID", updatable = true)
    @GraphQLQuery(name = "updateId")
    protected String        updateId;       // 최종 수정자 ID
    @UpdateTimestamp
    @Column(name = "UPDATE_DT", updatable = true)
    @GraphQLQuery(name = "updateDateTime")
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
