package site.tteolione.tteolione.common.config.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum Code {

    OK(200, HttpStatus.OK, "Ok"),
    EXISTS_USER(101, HttpStatus.BAD_REQUEST, "이미 회원가입한 회원입니다."),
    EXISTS_KAKAO(102, HttpStatus.BAD_REQUEST, "이미 카카오로 로그인한 회원입니다."),
    EXISTS_GOOGLE(103, HttpStatus.BAD_REQUEST, "이미 구글로 로그인한 회원입니다."),
    EXISTS_NAVER(104, HttpStatus.BAD_REQUEST, "이미 네이버로 로그인한 회원입니다."),
    EXISTS_APPLE(105, HttpStatus.BAD_REQUEST, "이미 애플로 로그인한 회원입니다."),
    IDENTIFY_EMAIL(106, HttpStatus.BAD_REQUEST, "본인인증 버튼을 눌러주세요."),
    VALIDATION_EMAIL(107, HttpStatus.BAD_REQUEST, "이메일 인증이 되어있지 않습니다."),
    NOT_EXISTS_AUTHCODE(108, HttpStatus.BAD_REQUEST, "인증코드를 찾을 수 없습니다."),
    VALIDATION_AUTHCODE(109, HttpStatus.BAD_REQUEST, "인증코드 유효한 시간인 5분을 초과했습니다."),
    NOT_EXISTS_LOGIN_ID_PW(110, HttpStatus.BAD_REQUEST, "아이디나 비밀번호를 다시 확인해주세요."),
    DISABLED_USER(111, HttpStatus.BAD_REQUEST, "비활성화 유저입니다."),
    VALIDATION_REFRESH_TOKEN(112, HttpStatus.BAD_REQUEST, "유효하지 않은 Refresh Token입니다."),
    WRONG_REFRESH_TOKEN(113, HttpStatus.BAD_REQUEST, "잘못된 Refresh Token입니다."),
    NOT_EXISTS_REFRESH_TOKEN(114, HttpStatus.BAD_REQUEST, "일치하는 Refresh Token이 없습니다."),
    NOT_EXISTS_USER(115, HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
    NOT_FOUND_CATEGORY(116, HttpStatus.BAD_REQUEST, "카테고리를 찾을 수 없습니다."),
    EXISTS_LOGIN_ID(117, HttpStatus.BAD_REQUEST, "중복된 아이디입니다."),
    NOT_FOUND_USER_INFO(118, HttpStatus.BAD_REQUEST, "회원정보가 일치하지 않습니다."),
    FOUND_APPLE_USER(119, HttpStatus.BAD_REQUEST, "네이버 로그인 회원입니다."),
    FOUND_KAKAO_USER(120, HttpStatus.BAD_REQUEST, "카카오 로그인 회원입니다."),
    FOUND_NAVER_USER(121, HttpStatus.BAD_REQUEST, "네이버 로그인 회원입니다."),
    FOUND_GOOGLE_USER(122, HttpStatus.BAD_REQUEST, "구글 로그인 회원입니다."),
    VERIFY_EMAIL_CODE(123, HttpStatus.BAD_REQUEST, "이메일 검증 실패"),
    EMPTY_QUERY(124, HttpStatus.BAD_REQUEST, "검색어를 입력해주세요"),
    VERIFY_ATK(125, HttpStatus.BAD_REQUEST, "검색어를 입력해주세요"),
    SOLD_OUT_PRODUCT(126, HttpStatus.BAD_REQUEST, "판매된 상품입니다."),
    NOT_EXISTS_CHAT_ROOM(127, HttpStatus.BAD_REQUEST, "일치하는 채팅방이 없습니다."),
    RESERVATION_OR_SOLD_OUT(128, HttpStatus.BAD_REQUEST, "예약중이거나 판매 완료된 상품입니다."),
    NOT_MATCH_BUYER(129, HttpStatus.BAD_REQUEST, "구매자가 일치하지 않습니다."),
    NEW_PRODUCT_OR_SOLD_OUT(130, HttpStatus.BAD_REQUEST, "새 상품이거나 판매 완료된 상품입니다."),
    PRODUCT_SOLD_OUT(131, HttpStatus.BAD_REQUEST, "판매 완료된 상품입니다."),
    EXISTS_REVIEW(132, HttpStatus.BAD_REQUEST, "이미 존재하는 리뷰입니다."),
    NOT_MATCH_USER(133, HttpStatus.BAD_REQUEST, "일치하지 않는 회원입니다."),
    WITH_DRAW_USER(134, HttpStatus.BAD_REQUEST, "탈퇴한 회원입니다."),
    NOT_EXISTS_REPORT_TYPE(135, HttpStatus.BAD_REQUEST, "존재하지 않는 신고 유형입니다."),
    NOT_EXISTS_REPORT_CATEGORY(136, HttpStatus.BAD_REQUEST, "존재하지 않는 신고 카테고리입니다."),
    EQUAL_SELLER_BUYER(137, HttpStatus.BAD_REQUEST, "구매자와 판매자가 동일 회원입니다."),
    APPLE_FEIGN_API_ERROR(138, HttpStatus.BAD_REQUEST, "애플 소셜 로그인 Feign API Feign Client 호출 오류"),
    EMPTY_APPLE_AUTHORZITION_CODE(139, HttpStatus.BAD_REQUEST, "애플의 인가코드를 입력해 주세요."),
    EMPTY_APPLE_REFRESH_TOKEN(140, HttpStatus.BAD_REQUEST, "애플의 리프레쉬 토큰을 입력해 주세요."),
    EMPTY_FILE_EXCEPTION(141, HttpStatus.BAD_REQUEST, "업로드 이미지가 비어있거나 파일 이름이 없습니다."),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(142, HttpStatus.BAD_REQUEST, "이미지 업로드 중 입출력 예외가 발생했습니다."),
    NO_FILE_EXTENTION(143, HttpStatus.BAD_REQUEST, "파일 확장자가 없습니다."),
    INVALID_FILE_EXTENTION(144, HttpStatus.BAD_REQUEST, "허용되지 않은 파일 확장자입니다.(jpg, jpeg, png, gif 허용)"),
    PUT_OBJECT_EXCEPTION(145, HttpStatus.BAD_REQUEST, "S3에 이미지를 업로드하는 동안 예외가 발생했습니다."),
    IO_EXCEPTION_ON_IMAGE_DELETE(145, HttpStatus.BAD_REQUEST, "이미지 삭제 중 입출력 예외가 발생했습니다."),
    EXPIRED_ACCESS_TOKEN(146, HttpStatus.BAD_REQUEST, "액세스 토큰이 만료되었습니다."),




    NOT_MATCH_PRODUCT_USER(2000, HttpStatus.BAD_REQUEST, "판매자와 일치하지 않습니다."),
    MATCH_EXIST_PW(3000, HttpStatus.BAD_REQUEST, "기존 비밀번호와 일치합니다."),
    EXIST_NICKNAME(3001, HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    NOT_MATCH_PW(3002, HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    NOT_MATCH_NEW_PW(3003, HttpStatus.BAD_REQUEST, "새로운 비밀번호가 일치하지 않습니다."),
    EQUALS_PASSWORD_NEW_PASSWORD(3004, HttpStatus.BAD_REQUEST, "기존 비밀번호 입력과 새로운 비밀번호 입력이 일치합니다."),
    EQUALS_NICKNAME(4000, HttpStatus.BAD_REQUEST, "기존 닉네임과 일치합니다."),
    NOT_EXISTS_PRODUCT(5000, HttpStatus.BAD_REQUEST, "상품이 존재하지 않습니다."),

    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "Bad request"),
    VALIDATION_ERROR(400, HttpStatus.BAD_REQUEST, "Validation error"),
    NOT_FOUND(403, HttpStatus.NOT_FOUND, "Requested resource is not found"),
    METHOD_NOT_ALLOWED(405, HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),

    INTERNAL_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
    DATA_ACCESS_ERROR(20001, HttpStatus.INTERNAL_SERVER_ERROR, "Data access error"),

    UNAUTHORIZED(40000, HttpStatus.UNAUTHORIZED, "User unauthorized");


    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }

    public static Code valueOf(HttpStatus httpStatus) {
        if (httpStatus == null) {
            throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if (httpStatus.is4xxClientError()) {
                        return Code.BAD_REQUEST;
                    } else if (httpStatus.is5xxServerError()) {
                        return Code.INTERNAL_ERROR;
                    } else {
                        return Code.OK;
                    }
                });
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", this.name(), this.getCode());
    }
}
