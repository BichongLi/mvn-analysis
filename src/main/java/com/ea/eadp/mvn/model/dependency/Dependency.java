package com.ea.eadp.mvn.model.dependency;

/**
 * User: BichongLi
 * Date: 11/29/2016
 * Time: 11:17 AM
 */
public class Dependency {

    private String groupId;

    private String artifactId;

    private String type;

    private String version;

    private String scope;

    public Dependency(String groupId, String artifactId,
                      String type, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.type = type;
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

    @Override
    public String toString() {
        String dependency = String.format("%1$s:%2$s:%3$s:%4$s", groupId, artifactId, type, version);
        return scope == null ? dependency : dependency + ":" + scope;
    }
}
