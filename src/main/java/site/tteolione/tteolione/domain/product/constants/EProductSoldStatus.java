package site.tteolione.tteolione.domain.product.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EProductSoldStatus {

    eNew("새로운 상품"),
    eReservation("예약중인 상품"),
    eSoldOut("판매 완료");

    private final String text;
}
