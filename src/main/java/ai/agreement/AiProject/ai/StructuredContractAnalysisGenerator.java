package ai.agreement.AiProject.ai;

import ai.agreement.AiProject.ai.result.structed.ContractAnalysisResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StructuredContractAnalysisGenerator {

    @SystemMessage({
        "부동산 계약서를 주어진 '기준' 에 따라 분석하고, ContractAnalysisResult 에 매핑하여 반환합니다."
    })

    @UserMessage("다음 계약서에 대한 분석과 평가를 다음 기준에 따라 수행 후, ContractAnalysisResult 형식에 맞게 반환해 주세요. 분석 및 평가는 주어진 계약서를 기반으로 이루어지며, 계약서 내의 요소를 기반으로 분석 근거를 제시해야 합니다.\n평가 기준은 다음과 같습니다." +
            "기준:\n" +
            "1. 기본 정보의 정확성 (20점 만점)\n" +
            "2. 법적 효력 (20점 만점)\n" +
            "3. 특약 사항의 완성도 (20점 만점)\n" +
            "4. 양측 권리와 의무의 명확성 (20점 만점)\n" +
            "5. 기타 중요한 조항 (20점 만점)\n" +
            "응답 클래스 타입 ContractAnalysisResult 정보: " +
            "contractName: 주어진 계약서 이름" +
            "landlord: 주어진 계약서 상의 임대인 정보(회사 또는 이름)" +
            "evaluationContentsByStandard: 주어진 1~5번 기준에 따라 주어진 계약서를 평가한 의견, 높임말을 사용할 것" +
            "evaluationScoresByStandard: 주어진 1~5번 기준에 따라 주어진 계약서를 평가한 점수" +
            "overallReview: 주어진 계약서의 분석 및 평가 총평" +
            "isContract: 주어진 문자열 요청의 계약서인지 구별하는 여부, 계약서라면 true, 계약서가 아니라면 false" +
            "추출된 계약서 텍스트 본문: {{ocrContract}}")
    ContractAnalysisResult generateStructuredResult(@V("ocrContract") String ocrContract);
}
