package com.graphql.sample.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.common.exception.BadRequestException;
import com.common.util.ObjectUtil;
import com.graphql.sample.Board;
import com.graphql.sample.BoardRepository;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;

@Service
@GraphQLApi
@RequiredArgsConstructor
public class BoardService {
    
    private final BoardRepository boardRepository;
    
    @GraphQLQuery(name = "findAllBoards")
    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }
    
    @GraphQLQuery(name = "getBoard")
    public Optional<Board> getBoard(Long id){
        return boardRepository.findById(id);
    }
    
    @GraphQLMutation(name = "createBoard")
    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }
    
    @GraphQLMutation(name = "updateBoard")
    public Board updateBoard(Long id, Map<String, Object> value) throws Exception {
        value.remove("id");
        Board board = getBoard(id).orElse(null);
        board = ObjectUtil.populate(board, value);
        return boardRepository.save(board);
    }
    
    @GraphQLMutation(name = "readBoard")
    public Board readBoard(Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board == null) throw new BadRequestException();
        board.setReadCount(board.getReadCount()+1);
        return boardRepository.save(board);
    }
    
    @GraphQLMutation(name = "deleteBoard")
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
    
}
