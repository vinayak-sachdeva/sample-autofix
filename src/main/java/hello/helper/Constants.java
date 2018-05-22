package hello.helper;

public class Constants {
    public static String USER = "neo4j";
    public static String PASS = "password";
    public static String QUERY1 = "match (n:MethodDeclaration)<-[:member]-(:TypeDeclaration{entity_type:\"class\"})<-[:DEFINE]-(file:File) where not n.modifiers =~ \"\\\\[(public|private|protected)?(, )?(static)?(, )?(abstract)?(, )?(synchronized)?(, )?(transient|volatile)?(, )?(final)?(, )?(native)?(, )?(strictfp)?(, )?\\\\]\" return id(n) as method_id, n.line as line, n.file as file, n.col as columnName, id(file) as file_id, n.modifiers as modifiers";
    public static String QUERY2 = "match (class:TypeDeclaration)-[:member]->(method:MethodDeclaration)-[:SETS]->(name:SimpleName)<-[:SET_BY]-(:VariableDeclarationFragment)<-[:fragment]-(field:FieldDeclaration) where field.modifiers contains \"static\" and not field.modifiers contains \"final\" and not method.modifiers contains \"synchronized\"\n" +
            "with method, class, name\n" +
            "match (file:File)-[:DEFINE]->(class)\n" +
            "return id(method) as method_id, id(file) as file_id, method.line as line, method.file as file, method.col as columnName, name.name as name";
    public static String CODEGEN_URL = "http://codegen-cnu.ey.devfactory.com/api/codegen/";
}
