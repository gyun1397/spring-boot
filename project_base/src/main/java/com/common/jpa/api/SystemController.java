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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.common.exception.BadRequestException;
import com.common.jpa.domain.BasicEntity;
import com.common.util.ResponseEntityUtil;

public abstract class SystemController<T extends BasicEntity<ID>, ID> extends RestrictedController<T, ID> {
    @Autowired
    private BaseService<T, ID> baseService;
    
    @Override
    @GetMapping("/list")
    public @ResponseBody ResponseEntity<?> search(
            @RequestParam(required = false) Map<String, Object> map,
            @SortDefault(sort="id", direction=Direction.DESC) Pageable pageable) throws Exception {
        setParam(map);
        Page<T> page = baseService.search(map, pageable);
        return ResponseEntityUtil.pages(page);
    }
    
    @GetMapping("/dynamicSerch")
    public @ResponseBody ResponseEntity<?> search(
            @RequestParam(required = false) Map<String, Object> map,
            @RequestParam(required = true) List<String> fields,
            @SortDefault(sort="id", direction=Direction.DESC) Pageable pageable) throws Exception {
        map.remove("fields");
        setParam(map);
        Page<Map<String, Object>> page = baseService.search(map, fields, pageable);
        return ResponseEntityUtil.pages(page);
    }

    @Override
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
    
    @Override
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> findOne(
            @PathVariable("id") ID id) throws Exception {
        T entity = getOne(id);
        return ResponseEntityUtil.entity(entity);
    }

    @GetMapping({"/scheme","/schema"})
    public ResponseEntity<?> getScheme() throws Exception {
        Class<?> classType = baseService.getClassType();
        return ResponseEntityUtil.ok(classType.getDeclaredConstructor().newInstance());
    }
    
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
    }
}
