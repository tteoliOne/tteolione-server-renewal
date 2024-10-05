package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindServiceLoginIdReq {

    private String username;

    private String email;

    @Builder
    public FindServiceLoginIdReq(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
