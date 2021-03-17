package com.graphql.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Query implements GraphQLQueryResolver {
    private BoardRepository   boardRepository;
    private CommentRepository commentRepository;

    @Autowired
    public Query(BoardRepository boardRepository, CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    public Iterable<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    public Iterable<Comment> findAllComments() {
        return commentRepository.findAll();
    }

    public long countBoards() {
        return boardRepository.count();
    }
}
