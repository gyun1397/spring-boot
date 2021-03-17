package com.graphql.sample;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Data;

@Data
@Entity
@Table(name = "COMMENT") // 댓글
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long          id;
    @CreationTimestamp
    @Column(name = "CREATE_DT", updatable = false)
    private LocalDateTime createDateTime; // 최초 생성일자
    @Column(name = "BOARD_ID")
    private Long          boardId;
    @Column(name = "COMMENT")
    private String        comment;        // 댓글
    @Column(name = "DEL_YN", insertable = false)
    @ColumnDefault("0")
    private boolean       isDelete;       // 삭제여부
}
