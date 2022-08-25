package com.cgw;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

/**
 * Testing Class for a cURL command to the HuggingFace Bloom inference API.
 * Found at: "https://huggingface.co/bigscience/bloom"
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.2
 * @since 0.2
 */
public class Bloom {

    // Token for accessing APIs
    private static String bloomToken = "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";

    /**
     * Main Method for console testing.
     * @param args Arguments passed.
     * @throws Exception Thrown if failed to execute cURL
     */
    public static void main(String[] args) throws Exception {
        //httpClientTest("This is a story about Jason Fetch");
        cURLTest("This is a story about Jason Fetch");
    }

    /**
     * Sends the cURL command with the given text generation prompt. Returns the response.
     * @param prompt Prompt to give AI Text Generator.
     * @return InputStream response.
     * @throws IOException Thrown if Failure to execute.
     */
    public static String cURLTest(String prompt) throws IOException {
        String command = "curl https://api-inference.huggingface.co/models/bigscience/bloom -X POST -d '{\""
                + prompt + "\"}' -H \"Authorization: Bearer " + bloomToken + "\"";

        Process process = Runtime.getRuntime().exec(command);
        try {
            String responseString = readInputStream(process.getInputStream());
            System.out.println(responseString);
            return responseString;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Reads the InputStream and returns a more concise response.
     * Credit to User: B.minnick, at StackOverflow.com
     * https://stackoverflow.com/questions/37573063/executing-curl-command-in-java-application
     * @param inputStream The Response from the API
     * @return String output of response.
     * @throws IOException Thrown if fails to read.
     */
    static private String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream, "UTF-8"));
        String tmp;
        StringBuilder sb = new StringBuilder();
        while ((tmp = reader.readLine()) != null) {
            sb.append(tmp).append("\n");
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }
        reader.close();
        return sb.toString();
    }
}
