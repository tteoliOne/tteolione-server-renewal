package site.tteolione.tteolione.domain.file.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EPhotoType {
    eProduct("상품 사진"),
    eReceipt("영수증 사진");
    private final String text;
}
