package com.ea.eadp.mvn.model.dependency;

import com.ea.eadp.mvn.model.common.DependencyDiff;
import com.ea.eadp.mvn.model.common.StringPatterns;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Date;

/**
 * User: BichongLi
 * Date: 11/29/2016
 * Time: 11:17 AM
 */
@XStreamAlias("dependency")
public class Dependency {

    private String groupId;

    private String artifactId;

    private String type;

    private String version;

    private String scope;

    private String reference;

    private String latestVersion;

    private Date releaseTime;

    public Dependency(String groupId, String artifactId,
                      String type, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.type = type;
        this.version = version;
        this.scope = scope;
        this.reference = String.format(StringPatterns.MVN_REPOSITORY_REFERENCE, this.groupId, this.artifactId);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    @Override
    public String toString() {
        String dependency = String.format("%1$s:%2$s:%3$s:%4$s", groupId, artifactId, type, version);
        return scope == null ? dependency : dependency + ":" + scope;
    }
    
    public DependencyDiff compare(Dependency dependency) {
        if (this.getGroupId().equals(dependency.getGroupId()) &&
                this.getArtifactId().equals(dependency.getArtifactId()) &&
                this.getVersion().equals(dependency.getVersion())) {
            return DependencyDiff.SAME;
        } else if (this.getGroupId().equals(dependency.getGroupId()) &&
                this.getArtifactId().equals(dependency.getArtifactId()) &&
                !this.getVersion().equals(dependency.getVersion())) return DependencyDiff.DIFFERENT_VERSION;
        else if (this.getArtifactId().equals(dependency.getArtifactId()) &&
                !this.getGroupId().equals(dependency.getGroupId())) return DependencyDiff.SIMILAR;
        else return DependencyDiff.DIFFERENT_DEPENDENCY;
    }
}
