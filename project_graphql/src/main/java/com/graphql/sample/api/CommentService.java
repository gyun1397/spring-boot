package com.graphql.sample.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.common.util.ObjectUtil;
import com.graphql.sample.Comment;
import com.graphql.sample.CommentRepository;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.RequiredArgsConstructor;

@Service
@GraphQLApi
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;

    
    @GraphQLQuery(name = "findAllComments")
    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }
    
    @GraphQLQuery(name = "getComment")
    public Optional<Comment> getComment(Long id){
        return commentRepository.findById(id);
    }
    
    @GraphQLMutation(name = "createComment")
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }
    
    @GraphQLMutation(name = "updateComment")
    public Comment updateComment(Long id, Map<String, Object> value) throws Exception {
        value.remove("id");
        Comment comment = getComment(id).orElse(null);
        comment = ObjectUtil.populate(comment, value);
        return commentRepository.save(comment);
    }
    
    @GraphQLMutation(name = "deleteComment")
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
    
}
