package site.tteolione.tteolione.client.oauth2.kakao.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class KakaoRevokeReq {

    private String target_id_type;
    private String target_id;
}