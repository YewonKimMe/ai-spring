package ai.agreement.AiProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessAndData implements ResultAndData {

    private String httpMessage;

    private Object data;
}
