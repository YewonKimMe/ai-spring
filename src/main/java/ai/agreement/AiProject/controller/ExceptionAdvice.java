package ai.agreement.AiProject.controller;

import ai.agreement.AiProject.dto.response.FailAndData;
import ai.agreement.AiProject.dto.response.ResultAndData;
import ai.agreement.AiProject.exception.IllegalExtensionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(IllegalExtensionException.class)
    public ResponseEntity<ResultAndData> illegalExtensionException(IllegalExtensionException e) {
        log.debug("IllegalExtensionException={}", e.getMessage());
        ResultAndData errorResultAndMessage = new FailAndData(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResultAndMessage);
    }
}
