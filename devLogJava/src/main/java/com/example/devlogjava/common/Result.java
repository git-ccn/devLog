package com.example.devlogjava.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long total;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageNum;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageSize;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pages;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <E> Result<List<E>> successPage(IPage<E> page) {
        if (page == null) return Result.success(Collections.emptyList());
        List<E> records = page.getRecords() == null ? Collections.emptyList() : page.getRecords();
        Result<List<E>> result = Result.success(records);
        result.setTotal(page.getTotal());
        result.setPageNum((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setPages((int) page.getPages());
        return result;
    }
}
