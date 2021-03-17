package com.graphql.sample;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Mutation implements GraphQLMutationResolver {
    private BoardRepository   boardRepository;
    private CommentRepository commentRepository;

    @Autowired
    public Mutation(BoardRepository boardRepository, CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    public Board createBoard(String subject, String contents) {
        Board board = new Board();
        board.setSubject(subject);
        board.setContents(contents);
        boardRepository.save(board);
        return board;
    }
    public Board readBoard(Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        board.setReadCount(board.getReadCount()+1);
        boardRepository.save(board);
        return board;
    }

    public Board updateBoard(Long id, String contents) throws Exception  {
        Optional<Board> optBoard = boardRepository.findById(id);
        if (optBoard.isPresent()) {
            Board board = optBoard.get();
            if (contents != null)
                board.setContents(contents);
            boardRepository.save(board);
            return board;
        }
        throw new Exception("Not found Board to update!");
    }
    
    public boolean deleteBoard(Long id) {
        boardRepository.deleteById(id);
        return true;
    }
    public Comment createComment(String text, Long boardId) {
        Comment comment = new Comment();
        comment.setComment(text);
        comment.setBoardId(boardId);
        commentRepository.save(comment);
        return comment;
    }

    public Comment updateComment(Long id, String text) throws Exception {
        Optional<Comment> optComment = commentRepository.findById(id);
        if (optComment.isPresent()) {
            Comment comment = optComment.get();
            if (text != null)
                comment.setComment(text);
            commentRepository.save(comment);
            return comment;
        }
        throw new Exception("Not found Comment to update!");
    }
    
    public boolean deleteComment(Long id) {
        commentRepository.deleteById(id);
        return true;
    }

}