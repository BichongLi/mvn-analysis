package com.ea.eadp.mvn.model.dependency;

import com.ea.eadp.mvn.model.common.Constants;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.ArrayList;
import java.util.List;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 10:08 AM
 */
@XStreamAlias("node")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class TreeNode implements Comparable<TreeNode> {

    private Dependency dependency;

    @XStreamOmitField
    @JsonIgnore
    private TreeNode parent;

    private List<TreeNode> children = new ArrayList<>();

    public TreeNode(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    @Override
    public int compareTo(TreeNode t) {
        if (Constants.GROUPID_TO_ANALYZE.contains(this.dependency.getGroupId())) {
            if (!Constants.GROUPID_TO_ANALYZE.contains(t.getDependency().getGroupId())) return -1;
            else if (this.getDependency().getGroupId().equals(t.getDependency().getGroupId())) {
                return this.getDependency().getArtifactId().compareTo(t.getDependency().getArtifactId());
            } else return this.getDependency().getGroupId().compareTo(t.getDependency().getGroupId());
        } else {
            if (Constants.GROUPID_TO_ANALYZE.contains(t.getDependency().getGroupId())) return 1;
            else return this.dependency.getGroupId().compareTo(t.getDependency().getGroupId());
        }
    }
}
