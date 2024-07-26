package site.tteolione.tteolione.domain.user.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ELoginType {
    eGoogle("구글"),
    eNaver("네이버"),
    eKakao("카카오"),
    eApple("애플"),
    eApp("앱");
    private final String text;
}
