package com.graphql.sample;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import com.common.jpa.domain.LogEntity;
import lombok.Data;

@Data
@Entity
@Table(name = "BOARD")
public class Board extends LogEntity<Long> {
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
