package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;

public record ChangeNicknameServiceReq(String nickname) {

    @Builder
    public ChangeNicknameServiceReq {
    }
}
