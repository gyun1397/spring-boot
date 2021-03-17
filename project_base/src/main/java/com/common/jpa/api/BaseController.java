package com.common.jpa.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
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
import com.common.jpa.domain.BasicEntity;
import com.common.util.DataUtil;
import com.common.util.ObjectUtil;
import com.common.util.ResponseEntityUtil;

public abstract class BaseController<T extends BasicEntity<ID>, ID> {
    @Autowired
    protected BaseService<T, ID> baseService;
    
    @DeleteMapping
    public @ResponseBody ResponseEntity<?> deleteAll(@RequestParam(name = "ids", required = true) List<ID> ids,
            @RequestParam(required = false) Map<String, Object> map) throws Exception {
        setParam(map);
        map.put("ids", ids);
        baseService.deleteAll(map);
        return ResponseEntityUtil.noContent();
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> delete(@PathVariable("id") ID id) throws Exception {
        T entity = getOne(id);
        setKey(entity);
        baseService.deleteByEntity(entity);
        return ResponseEntityUtil.noContent();
    }

    @GetMapping({"/list", "/search"})
    public @ResponseBody ResponseEntity<?> search(
            @RequestParam(required = false) Map<String, Object> map,
            @SortDefault(sort="id", direction=Direction.DESC) Pageable pageable) throws Exception {
        setParam(map);
        Page<T> pages = baseService.search(map, pageable);
        return ResponseEntityUtil.pages(pages);
    }
    
    @GetMapping("/dynamicSearch")
    public @ResponseBody ResponseEntity<?> search(
            @RequestParam(required = false) Map<String, Object> map,
            @RequestParam(required = true) List<String> fields,
            @SortDefault(sort="id", direction=Direction.DESC) Pageable pageable) throws Exception {
        map.remove("fields");
        setParam(map);
        Page<Map<String, Object>> page = baseService.search(map, fields, pageable);
        return ResponseEntityUtil.pages(page);
    }
    
    @GetMapping
    public @ResponseBody ResponseEntity<?> findAll(
            @RequestParam(required = false) Map<String, Object> map) throws Exception {
        setParam(map);
        List<T> list = baseService.findAll(map);
        return ResponseEntityUtil.list(list);
    }
    
    @GetMapping({"/dynamic", "/dynamicList"})
    public @ResponseBody ResponseEntity<?> findAll(
            @RequestParam(required = false) Map<String, Object> map,
            @RequestParam(required = true) List<String> fields) throws Exception {
        map.remove("fields");
        setParam(map);
        List<Map<String, Object>> list = baseService.findAll(map, fields);
        return ResponseEntityUtil.list(list);
    }
    
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> findOne(
            @PathVariable("id") ID id) throws Exception {
        T entity = getOne(id);
        return ResponseEntityUtil.entity(entity);
    }
    
    @GetMapping("/{id}/detail")
    public @ResponseBody ResponseEntity<?> findDetail(
            @PathVariable("id") ID id) throws Exception {
        T entity = getOne(id);
        return ResponseEntityUtil.entity(entity);
    }

    @PostMapping({"","/request"})
    public @ResponseBody ResponseEntity<?> createRequest(
            @RequestBody(required = false) T entity) throws Exception {
        setKey(entity);
        entity = baseService.createRequest(entity);
        return ResponseEntityUtil.entity(entity);
    }
    
    @PatchMapping({"/{id}","/{id}/request"})
    public @ResponseBody ResponseEntity<?> updateRequest(
            @PathVariable("id") ID id,
            @RequestBody(required = false) Map<String, Object> map) throws Exception {
        T entity = getOne(id);
        setKey(entity);
        setMap(map);
        map = DataUtil.updateMapPrameterConvert(map);
        ObjectUtil.convertMap(map, baseService.getClassType());
        entity = baseService.updateRequestByEntity(entity, map);
        return ResponseEntityUtil.entity(entity);
    }
    
    @GetMapping({"/scheme","/schema"})
    public ResponseEntity<?> getScheme() throws Exception {
        Class<?> classType = baseService.getClassType();
        return ResponseEntityUtil.ok(classType.getDeclaredConstructor().newInstance());
    }
    
    protected void setKey(T entity)  throws Exception {};
    protected void setMap(Map<String, Object> map)  throws Exception {};
    protected void setParam(Map<String, Object> map)  throws Exception {};
    protected T getOne(ID id)  throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        setParam(map);
        T t = baseService.findOne(map);
        if (t == null) {
            throw new BadRequestException("대상을 찾을 수 없습니다.");
        }
        return t;
//        return baseService.findOne(id);
    };
}
