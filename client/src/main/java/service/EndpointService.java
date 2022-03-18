package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.BusinessException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import util.CommandParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class EndpointService {
    private final RestTemplate restTemplate;
    private final CommandParser parser;
    private String serverResource;

    public EndpointService(final String uri) {
        final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        this.restTemplate = restTemplateBuilder
                .errorHandler(new ErrorResponseHandler())
                .build();
        this.serverResource = uri;
        this.parser = new CommandParser(getCommands());
    }

    public String execute(final String line) {
        try {
            return send(
                    parser.parseCommand(line)
            );
        } catch (final BusinessException e) {
            return send("help");
        }
    }

    private HashMap<String, ArrayList<Integer>> getCommands() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, ArrayList<Integer>>>(){}.getType();
        String commandsString = send("commands");
        return gson.fromJson(commandsString, type);
    }

    private String send(final String command) {
        final HttpEntity<String> requestEntity = new HttpEntity<>(command);
        final ResponseEntity<String> response = restTemplate
                .exchange(serverResource + "/" + command, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
    }

    public String auth(String login, String password) {
        final HttpEntity<String> requestEntity = new HttpEntity<>(password);
        final ResponseEntity<String> response = restTemplate
                .exchange(serverResource + "/" + login, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
    }

    public String exit() {
        final HttpEntity<String> requestEntity = new HttpEntity<>("password");
        final ResponseEntity<String> response = restTemplate
                .exchange(serverResource + "/" + "exit", HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
    }
}
