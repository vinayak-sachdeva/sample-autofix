package hello.controller;

import com.google.gson.Gson;
import hello.entity.Fixes;
import hello.repo.FixesRepo;
import hello.repo.IssueRepo;
import okhttp3.*;
import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import hello.helper.*;

import java.io.IOException;
import java.util.*;

@RestController
public class IssueController {

    @Autowired
    private FixesRepo fixesRepo;
    @Autowired
    private IssueRepo issueRepo;

    private Driver driver;

    private void setNeo4jDriver(String url) {
        final String username = "neo4j";
        final String password = "password";
        driver = GraphDatabase.driver( url, AuthTokens.basic( username, password ) );
    }

    private CodeGenPostResponse sendPostRequestCodeGen(String url, String json) {
        // send post request to codegen
        try {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = client.newCall(request).execute();

            return new Gson().fromJson(response.body().string(), CodeGenPostResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new CodeGenPostResponse(-1);
        }
    }

    private CodeGenGetResponse sendGetRequestCodeGen(String url) {
        // send get request to codegen
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            return new Gson().fromJson(response.body().string(), CodeGenGetResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new CodeGenGetResponse("Failure", "");
        }
    }

    private void fixIssue1(Integer methodId, String modifiers) {
        String [] modifierList = {"public", "protected", "private", "static", "abstract", "synchronized", "transient", "volatile", "final", "native", "strictfp"};
        String newModifiers = "";
        for(String modifier : modifierList) {
            if (modifiers.contains(modifier)) {
                newModifiers += modifier + ", ";
            }
        }
        if (newModifiers.endsWith(", ")) {
            newModifiers = newModifiers.substring(0, newModifiers.length() - 2);
        }
        newModifiers = "[" + newModifiers + "]";
        Session session = driver.session();
        session.run("match (n) where id(n)=" + methodId + " set n.modifiers=\"" + newModifiers + "\"");
    }

    private void fixIssue3(Integer methodId, String modifiers) {
        String [] modifierList = {"public", "protected", "private", "static", "abstract", "synchronized", "transient", "volatile", "final", "native", "strictfp"};
        String newModifiers = "";
        for(String modifier : modifierList) {
            if (modifiers.contains(modifier) || modifier.equals("synchronized")) {
                newModifiers += modifier + ", ";
            }
        }
        if (newModifiers.endsWith(", ")) {
            newModifiers = newModifiers.substring(0, newModifiers.length() - 2);
        }
        newModifiers = "[" + newModifiers + "]";
        Session session = driver.session();
        session.run("match (n) where id(n)=" + methodId + " set n.modifiers=\"" + newModifiers + "\"");
    }

    @GetMapping(path="/fix", produces = "application/json")
    public String fixIssue(@RequestParam(value="issueId") Integer issueId, @RequestParam(value="sandBoxUrl") String sandBoxUrl) {
        setNeo4jDriver(sandBoxUrl);
        CodeGenPostRequest codeGenPostRequest = new CodeGenPostRequest(sandBoxUrl, Constants.USER, Constants.PASS);
        Session session = driver.session();
        List<Record> result;
        switch (issueId) {
            case 1:
                result = session.run(Constants.QUERY1).list();
                for(Record record : result) {
                    codeGenPostRequest.fileIds.add(record.get("file_id").asInt());
                    fixIssue1(record.get("method_id").asInt(), record.get("modifiers").asString());
                }

                Integer fixId = sendPostRequestCodeGen(Constants.CODEGEN_URL, new Gson().toJson(codeGenPostRequest)).id;

                for(Record record : result) {
                    fixesRepo.save(new Fixes(fixId, issueRepo.findById(issueId).get(), sandBoxUrl, Integer.toString(record.get("line").asInt()), record.get("file").asString(), Integer.toString(record.get("columnName").asInt()), false));
                }

                session.close();
                driver.close();
                return new Gson().toJson(new PostResponse(fixId, "200"));
            case 2:
                return "2";
            case 3:
                result = session.run(Constants.QUERY2).list();
                for(Record record : result) {
                    codeGenPostRequest.fileIds.add(record.get("file_id").asInt());
                    fixIssue3(record.get("method_id").asInt(), record.get("modifiers").asString());
                }

                fixId = sendPostRequestCodeGen(Constants.CODEGEN_URL, new Gson().toJson(codeGenPostRequest)).id;

                for(Record record : result) {
                    fixesRepo.save(new Fixes(fixId, issueRepo.findById(issueId).get(), sandBoxUrl, Integer.toString(record.get("line").asInt()), record.get("file").asString(), Integer.toString(record.get("columnName").asInt()), false));
                }
                session.close();
                driver.close();
                return new Gson().toJson(new PostResponse(fixId, "200"));
            default:
                return "Invalid Issue Id";
        }
    }

    @GetMapping(path="/fix/{fixId}", produces = "application/json")
    public String getIssueStatus(@PathVariable(value="fixId") Integer fixId) {
        try {
            List<Fixes> fixesList = fixesRepo.findAllByFixId(fixId);

            if (fixesList.isEmpty()) {
                return new Gson().toJson(new GetResponse(fixId, "404"));
            }

            CodeGenGetResponse codeGenGetResponse = sendGetRequestCodeGen("http://codegen-cnu.ey.devfactory.com/api/codegen/status/" + fixId);

            if (codeGenGetResponse.status.equals("Success")) {
                fixesRepo.updateAllByFixID(codeGenGetResponse.Url, fixId);
                fixesList = fixesRepo.findAllByFixId(fixId);
            }

            GetResponse getResponse = new GetResponse(fixId, fixesList.get(0).getS3Link(), "200");

            for (Fixes fix : fixesList) {
                GetResponseIssue getResponseIssue = new GetResponseIssue(
                        fix.getissueId(),
                        issueRepo.findById(fix.getissueId()).get().getIssueType(),
                        fix.getLine(),
                        fix.getFile(),
                        fix.getColumnName(),
                        fix.isFixed()
                );
                getResponse.getIssues().add(getResponseIssue);
            }
            return new Gson().toJson(getResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new Gson().toJson(new GetResponse(fixId, "404"));
        }
    }
}
