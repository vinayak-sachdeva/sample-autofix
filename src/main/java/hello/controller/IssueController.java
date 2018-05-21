package hello.controller;

import com.google.gson.Gson;
import hello.entity.Fixes;
import hello.repo.FixesRepo;
import hello.repo.IssueRepo;
import javassist.compiler.CodeGen;
import okhttp3.*;
import org.aspectj.apache.bcel.classfile.Code;
import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    class PostResponse {
        private Integer fixId;
        private String status;
        public PostResponse(Integer fixId, String status) {
            this.fixId = fixId;
            this.status = status;
        }
    }

    class GetResponseIssue {
        private Integer id;
        private String issueType;
        private String line;
        private String file;
        private String columnName;
        private Boolean isFixed;

        GetResponseIssue(Integer id, String issueType, String line, String file, String columnName, Boolean isFixed) {
            this.id = id;
            this.issueType = issueType;
            this.line = line;
            this.file = file;
            this.columnName = columnName;
            this.isFixed = isFixed;
        }
    }

    class GetResponse {
        private Integer fixId;
        private List<GetResponseIssue> issues;
        private String s3Link;
        private String status;

        public GetResponse(Integer fixId, String s3Link, String status) {
            this.fixId = fixId;
            this.s3Link = s3Link;
            this.status = status;
            this.issues = new ArrayList<GetResponseIssue>();
        }

        public GetResponse(Integer fixId, String status) {
            this.fixId = fixId;
            this.status = status;
            this.s3Link = "";
            this.issues = new ArrayList<GetResponseIssue>();
        }

        public List<GetResponseIssue> getIssues() {
            return issues;
        }
    }

    class CodeGenPostResponse {
        Integer id;
        CodeGenPostResponse(Integer id) {
            this.id = id;
        }
    }

    class CodeGenPostRequest {
        String url;
        String username;
        String password;
        Set<Integer> fileIds;

        CodeGenPostRequest(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.fileIds = new HashSet<Integer>();
        }
    }

    class CodeGenGetResponse {
        String status;
        String Url;

        CodeGenGetResponse(String status, String Url) {
            this.status = status;
            this.Url = Url;
        }
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomAlphaNumeric() {
        int count = 10;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
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
        String USER = "neo4j";
        String PASS = "password";
        CodeGenPostRequest codeGenPostRequest = new CodeGenPostRequest(sandBoxUrl, USER, PASS);
        Session session = driver.session();
        List<Record> result;
        switch (issueId) {
            case 1:
                result = session.run("match (n:MethodDeclaration)<-[:member]-(:TypeDeclaration{entity_type:\"class\"})<-[:DEFINE]-(file:File) where not n.modifiers =~ \"\\\\[(public|private|protected)?(, )?(static)?(, )?(abstract)?(, )?(synchronized)?(, )?(transient|volatile)?(, )?(final)?(, )?(native)?(, )?(strictfp)?(, )?\\\\]\" return id(n) as method_id, n.line as line, n.file as file, n.col as columnName, id(file) as file_id, n.modifiers as modifiers").list();
                for(Record record : result) {
                    codeGenPostRequest.fileIds.add(record.get("file_id").asInt());
                    fixIssue1(record.get("method_id").asInt(), record.get("modifiers").asString());
                }

                Integer fixId = sendPostRequestCodeGen("http://codegen-cnu.ey.devfactory.com/api/codegen/", new Gson().toJson(codeGenPostRequest)).id;

                for(Record record : result) {
                    fixesRepo.save(new Fixes(fixId, issueRepo.findById(issueId).get(), sandBoxUrl, Integer.toString(record.get("line").asInt()), record.get("file").asString(), Integer.toString(record.get("columnName").asInt()), false));
                }
                session.close();
                driver.close();
                return new Gson().toJson(new PostResponse(fixId, "200"));
            case 2:
                return "2";
            case 3:
                result = session.run("match (class:TypeDeclaration)-[:member]->(method:MethodDeclaration)-[:SETS]->(name:SimpleName)<-[:SET_BY]-(:VariableDeclarationFragment)<-[:fragment]-(field:FieldDeclaration) where field.modifiers contains \"static\" and not field.modifiers contains \"final\" and not method.modifiers contains \"synchronized\"\n" +
                        "with method, class, name\n" +
                        "match (file:File)-[:DEFINE]->(class)\n" +
                        "return id(method) as method_id, id(file) as file_id, method.line as line, method.file as file, method.col as columnName, name.name as name").list();
                for(Record record : result) {
                    codeGenPostRequest.fileIds.add(record.get("file_id").asInt());
                    fixIssue3(record.get("method_id").asInt(), record.get("modifiers").asString());
                }

                fixId = sendPostRequestCodeGen("http://codegen-cnu.ey.devfactory.com/api/codegen/", new Gson().toJson(codeGenPostRequest)).id;

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
