package ai.agreement.AiProject.ai.structed.output;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ContractAnalysisResult {

    private String contractName;

    private String landlord; // 임대인 (회사 또는 이름)

    private List<String> evaluationContentsByStandard; // 각 항목 기준별 평가 코멘트

    private List<Integer> evaluationScoresByStandard; //  각 항목 기준별 평가 점수

    private String overallReview; // 총평

    private boolean isContract; // 계약서 유무 확인

}
