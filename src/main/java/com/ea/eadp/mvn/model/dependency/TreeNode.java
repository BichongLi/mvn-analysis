package com.ea.eadp.mvn.model.dependency;

import java.util.HashSet;
import java.util.Set;

/**
 * User: BichongLi
 * Date: 12/2/2016
 * Time: 10:08 AM
 */
public class TreeNode {

    private Dependency dependency;

    private Set<TreeNode> dependencies = new HashSet<>();

    public TreeNode(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public Set<TreeNode> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<TreeNode> dependencies) {
        this.dependencies = dependencies;
    }
}
