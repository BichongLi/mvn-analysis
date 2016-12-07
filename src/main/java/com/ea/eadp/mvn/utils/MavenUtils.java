package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.model.dependency.Dependency;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

/**
 * User: BichongLi
 * Date: 12/6/2016
 * Time: 4:37 PM
 */
public class MavenUtils {

    private static final String QUERY_DEPENDENCY_PATTERN =
            "http://search.maven.org/solrsearch/select?q=g:%%22%1$s%%22+AND+a:%%22%2$s%%22&wt=json";

    public static void fillDependencyInfoFromMavenRepo(Dependency dependency) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                String.format(QUERY_DEPENDENCY_PATTERN, dependency.getGroupId(), dependency.getArtifactId())
        );
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new AnalyzeException(ExceptionType.INTERNAL_ERROR,
                        "Error retrieving information from maven repository for dependency %1$s.", dependency.toString());
            }
            HttpEntity entity = response.getEntity();
            JSONObject obj = new JSONObject(IOUtils.inputStreamToString(entity.getContent()));
            JSONObject result = (JSONObject) obj.get("response");
            if (!result.get("numFound").equals(0)) {
                JSONObject doc = (JSONObject) result.getJSONArray("docs").get(0);
                dependency.setLatestVersion((String) doc.get("latestVersion"));
                dependency.setReleaseTime(new Date((Long) doc.get("timestamp")));
            }
        } catch (IOException e) {
            throw new AnalyzeException(ExceptionType.INTERNAL_ERROR, "Error retrieving information from maven repository", e);
        }
    }

}
