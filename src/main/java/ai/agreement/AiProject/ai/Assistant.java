package ai.agreement.AiProject.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface Assistant {

    @SystemMessage({
            "다음 계약서에 대한 평가를 수행해 주세요. 계약서 내용: {text_data}\n\n" +
                    "기준:\n" +
                    "1. 기본 정보의 정확성 (20점 만점)\n" +
                    "2. 법적 효력 (20점 만점)\n" +
                    "3. 특약 사항의 완성도 (20점 만점)\n" +
                    "4. 양측 권리와 의무의 명확성 (20점 만점)\n" +
                    "5. 기타 중요한 조항 (20점 만점)\n" +
                    "각 항목의 점수를 구체적으로 설명 및 제공해 주세요. 각 항목의 점수는 매우 엄격하게 부여하고 그리고 설명이 끝난 이후, ; 구분자 이후로 각 1~5번 점수를 ;로 구별하여 붙여주세요. 예를 들면, ~설명;15;20;20;30;20"
    })

    String chat(String message);

    TokenStream chatStream(String userMessage);
}
