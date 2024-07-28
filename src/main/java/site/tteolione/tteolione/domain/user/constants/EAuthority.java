package site.tteolione.tteolione.domain.user.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EAuthority{
    eRoleDisabledUser("ROLE_DISABLED_USER"),
    eRoleUser("ROLE_USER"),
    eWithdrawalUser("ROLE_WITHDRAW_USER");
    private final String text;
}
