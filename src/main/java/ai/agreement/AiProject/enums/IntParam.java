package ai.agreement.AiProject.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IntParam { // 요청 파라미터 검증용 enum

    ZERO(0),

    ONE(1);

    private final Integer code;

    public static IntParam fromValue(Integer value) {
        for (IntParam intParam : values()) {
            if (intParam.getCode().intValue() == value) {
                return intParam;
            }
        }
        throw new IllegalArgumentException("Illegal Param");
    }
}
