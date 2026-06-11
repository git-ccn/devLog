package com.example.devlogjava.entity.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class NoteOptionPo {
    private String id;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
}
