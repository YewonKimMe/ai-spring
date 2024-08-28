package ai.agreement.AiProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FailAndData<T> implements ResultAndData {

    private String httpMessage;

    private T data;
}
