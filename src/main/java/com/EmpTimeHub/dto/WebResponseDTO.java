package com.EmpTimeHub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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

    public WebResponseDTO() {
    }

    public WebResponseDTO(Boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public WebResponseDTO(Boolean flag, String message, T response) {
        this(flag,message);
        this.response = response;
    }

    public WebResponseDTO(Boolean flag, String message, Integer status) {
        this(flag,message);
        this.status = status;
    }

    public WebResponseDTO(Boolean flag, String message, Integer status, T response) {
        this(flag,message,response);
        this.flag = flag;
    }

    public WebResponseDTO(Boolean flag, String message, Integer status, T response, Long totalRecords) {
        this(flag,message,status,response);
        this.totalRecords = totalRecords;
    }

    public WebResponseDTO(Boolean flag, String message, Integer status, T response, Long totalRecords, Object otherInfo) {
        this(flag,message,status,response);
        this.otherInfo = otherInfo;
    }
}

