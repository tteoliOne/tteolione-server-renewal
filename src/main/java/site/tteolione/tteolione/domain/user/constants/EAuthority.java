package site.tteolione.tteolione.domain.user.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EAuthority{
    ROLE_ADMIN("관리자 권한"),
    ROLE_USER("유저 권한"),
    ROLE_WITHDRAW_USER("탈퇴");
    private final String text;
}
