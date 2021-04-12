package com.graphql.sample;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "BOARD")
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GraphQLQuery(name = "id")
    protected Long          id;
    @Column(name = "SUBJECT")
    @GraphQLQuery(name = "subject")
    private String          subject;        // 제목
    @Column(name = "CONTENT")
    @GraphQLQuery(name = "contents")
    private String          contents;       // 내용
    @Column(name = "READ_CNT")
    @GraphQLQuery(name = "readCount")
    private Long            readCount;      // 조회수
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
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "BOARD_ID", insertable = false, updatable = false)
    @OrderBy("id asc")
    @GraphQLQuery(name = "comments")
    protected List<Comment> comments;       // 댓글

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
