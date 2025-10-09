package com.EmpTimeHub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebResponseDTO<T> {
    private Boolean flag;
    private String message;
    public Integer status;
    public T response;
    private Long totalRecords;
    public Object otherInfo;
}

