package site.tteolione.tteolione.common.util;

import lombok.*;
import site.tteolione.tteolione.domain.user.constants.EAuthority;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public class SecurityUserDto {
    private Long userId;
    private String email;
    private EAuthority userRole;
    private ELoginType loginType;

}
