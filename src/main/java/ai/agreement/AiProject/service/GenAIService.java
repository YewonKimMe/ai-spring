package ai.agreement.AiProject.service;

import ai.agreement.AiProject.ai.StructuredContractAnalysisGenerator;
import ai.agreement.AiProject.ai.structed.output.ContractAnalysisResult;

public interface GenAIService {

    String createChatRequest(String message);

    ContractAnalysisResult createAnalysisResult(String message);
}
