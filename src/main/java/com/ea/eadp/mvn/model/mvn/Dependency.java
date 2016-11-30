package com.ea.eadp.mvn.model.mvn;

import com.ea.eadp.mvn.model.common.StringPatterns;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import com.ea.eadp.mvn.model.exception.ExceptionType;

import java.util.regex.Matcher;

/**
 * User: BichongLi
 * Date: 11/29/2016
 * Time: 11:17 AM
 */
public class Dependency {

    private String groupId;

    private String artifactId;

    private String packaging;

    private String version;

    private String scope;

    public Dependency(String groupId, String artifactId,
                      String packaging, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.packaging = packaging;
        this.version = version;
        this.scope = scope;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return String.format("%1$s:%2$s:%3$s:%4$s:%5$s", groupId, artifactId, packaging, version, scope);
    }
}
