package com.godlife.authservice.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    /**
     * 상태 (success / error)
     */
    private String status;

    /**
     * 데이터
     */
    private T data;

    /**
     * 오류 코드
     */
    private Integer code;

    /**
     * 오류 메시지
     */
    private String message;

    public ApiResponse(ResponseCode resultCode, T data) {
        this.status = resultCode.getStatus();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }
}
