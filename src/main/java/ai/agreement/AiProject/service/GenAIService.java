package ai.agreement.AiProject.service;

import ai.agreement.AiProject.ai.result.structed.ContractAnalysisResult;

public interface GenAIService {

    String createChatRequest(String message);

    ContractAnalysisResult createAnalysisResult(String message);
}
