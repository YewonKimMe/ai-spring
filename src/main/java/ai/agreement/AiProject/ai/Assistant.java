package ai.agreement.AiProject.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface Assistant {

    @SystemMessage({
            "다음 계약서에 대한 평가를 수행해 주세요.\n" +
                    "기준:\n" +
                    "1. 기본 정보의 정확성 (20점 만점)\n" +
                    "2. 법적 효력 (20점 만점)\n" +
                    "3. 특약 사항의 완성도 (20점 만점)\n" +
                    "4. 양측 권리와 의무의 명확성 (20점 만점)\n" +
                    "5. 기타 중요한 조항 (20점 만점)\n" +
                    "출력 방법 설명:"+
                    "계약서 제목을 간단하게 언급하고, 임대인은(회사)는 누구인지 언급해주세요. 각 항목의 점수를 구체적으로 설명 및 제공해 주세요. 평가내용은 높임말을 사용합니다." +
                    "만약 계약서가 아닌 것 같으면 클라이언트에서 처리하기 위해서 '-1' 문자열을 응답해 주세요." +
                    "응답 양식은 다음과 같습니다. 응답 양식을 철저하게 지켜주세요. AI가 생성한 응답은 {} 내에 위치하고, 중괄호'{}'는 출력하지 않습니다. 계약서 OCR 내용은 출력하지 않습니다." +
                    "{계약서 제목}\n" +
                    "{임대인 정보}\n" +
                    "1. 기본 정보의 정확성 (20점 만점)\n{1항 평가내용}" +
                    "2. 법적 효력 (20점 만점)\n{2항 평가내용}" +
                    "3. 특약 사항의 완성도 (20점 만점)\n{3항 평가내용}" +
                    "4. 양측 권리와 의무의 명확성 (20점 만점)\n{4항 평가내용}" +
                    "5. 기타 중요한 조항 (20점 만점)\n{5항 평가내용}" +
                    "총평: {총평 내용}\n" +
                    "@{1항 평가 점수;2항 평가 점수;3항 평가 점수;4항 평가 점수;5항 평가 점수}"
    })

    String chat(String message);

    //TokenStream chatStream(String userMessage);
}
