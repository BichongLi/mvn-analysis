package com.ea.eadp.mvn.model.dependency;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.HashSet;
import java.util.Set;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 10:08 AM
 */
@XStreamAlias("node")
public class TreeNode {

    private Dependency dependency;

    @XStreamOmitField
    private TreeNode parent;

    private Set<TreeNode> children = new HashSet<>();

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

    public Set<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(Set<TreeNode> children) {
        this.children = children;
    }
}
