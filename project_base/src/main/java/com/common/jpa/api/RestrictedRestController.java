package com.common.jpa.api;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.common.exception.BadRequestException;

public abstract class RestrictedRestController<T, ID> {

    @DeleteMapping
    public @ResponseBody ResponseEntity<?> deleteAll(@RequestParam("ids") List<ID> ids) throws Exception {
        throw new BadRequestException();
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> delete(@PathVariable("id") ID id) throws Exception {
        throw new BadRequestException();
    }
    
    @GetMapping("/list")
    public @ResponseBody ResponseEntity<?> search(
            @RequestParam(required = false) Map<String, Object> map,
            PersistentEntityResourceAssembler resourceAssembler,
            Pageable pageable) throws Exception {
        throw new BadRequestException();
    }
    
    @GetMapping
    public @ResponseBody ResponseEntity<?> findAll(
            @RequestParam(required = false) Map<String, Object> map,
            PersistentEntityResourceAssembler resourceAssembler) throws Exception {
        throw new BadRequestException();
    }
    
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> findOne(
            @PathVariable("id") ID id,
            PersistentEntityResourceAssembler resourceAssembler) throws Exception {
        throw new BadRequestException();
    }
    

    @PostMapping({"","/request"})
    public @ResponseBody ResponseEntity<?> createRequest(
            @RequestBody(required = false) T entity) throws Exception {
        throw new BadRequestException();
    }
    
    @PatchMapping({"/{id}","/{id}/request"})
    public @ResponseBody ResponseEntity<?> updateRequest(
            @PathVariable("id") ID id,
            @RequestBody(required = false) Map<String, Object> map) throws Exception {
        throw new BadRequestException();
    }
    
}
