package service;

import exception.BusinessException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ErrorResponseHandler implements ResponseErrorHandler {
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getRawStatusCode() != 200;
    }

    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        int status = httpResponse.getRawStatusCode();
        if (status == 400) {
            throw new BusinessException("");
        } else if (status == 404) {
            //TODO add handlers
            System.out.println("...");
        } else {
            throw new BusinessException("");
        }
    }
}
