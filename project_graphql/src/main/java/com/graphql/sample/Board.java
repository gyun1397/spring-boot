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
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Data;

@Data
@Entity
@Table(name = "BOARD")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @Column(name = "CREATE_DT", updatable = false)
    private LocalDateTime createDateTime; // 최초 생성일자
    @Column(name = "SUBJECT")
    private String subject;   // 제목
    @Column(name = "CONTENT")
    private String contents;  // 내용
    @Column(name = "READ_CNT")
    private Long   readCount; // 조회수
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "BOARD_ID", insertable = false, updatable = false)
    @OrderBy("id asc")
    protected List<Comment>     comments;           // 댓글
}
