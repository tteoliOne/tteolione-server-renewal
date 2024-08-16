package site.tteolione.tteolione.common.config.exception;

import lombok.Getter;

@Getter
public class BaseResponse<T> extends ResponseDto {

    private final T data;

    private BaseResponse(T data) {
        super(true, Code.OK.getCode(), Code.OK.getMessage());
        this.data = data;
    }

    private BaseResponse(T data, String message) {
        super(true, Code.OK.getCode(), message);
        this.data = data;
    }

    public static <T> BaseResponse<T> of(T data) {
        return new BaseResponse<>(data);
    }

    public static <T> BaseResponse<T> of(T data, String message) {
        return new BaseResponse<>(data, message);
    }

    public static <T> BaseResponse<T> empty() {
        return new BaseResponse<>(null);
    }
}
