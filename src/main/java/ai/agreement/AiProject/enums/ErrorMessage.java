package ai.agreement.AiProject.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    NOT_CONTRACT("계약서 양식이 아닙니다."),

    ILLEGAL_EXTENSION("올바르지 않은 파일 확장자 입니다."),

    IMAGE_OCR("이미지 감지 중 오류가 발생했습니다."),

    TEXT_NOT_FOUND("텍스트가 감지되지 않았습니다.\n올바른 계약서 이미지인지 확인해 주세요.");

    private final String errorMessage;

}
