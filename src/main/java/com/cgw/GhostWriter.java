// Attempt at integrating OpenAI API onto app.

//package com.cgw;
//
//import java.util.*;
//import java.util.function.Supplier;
//import java.util.ArrayList;
//import com.theokanning.openai.OpenAiService;
//import com.theokanning.openai.completion.CompletionRequest;
//import com.theokanning.openai.engine.Engine;
//import com.theokanning.openai.completion.CompletionChoice;
//import com.theokanning.openai.model.Model;
//
//public class GhostWriter {
//    public static void main(String... args) {
//        String token = System.getenv("OPENAI_TOKEN");
//        OpenAiService service = new OpenAiService(token);
//
//        System.out.println("\nCreating completion...");
//        CompletionRequest completionRequest = CompletionRequest.builder()
//                .prompt("Somebody once told me the world is gonna roll me")
//                .echo(true)
//                .user("testing")
//                .build();
//                service.createCompletion("ada", completionRequest).getChoices().forEach(System.out::println);
//    }
//}
