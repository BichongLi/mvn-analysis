package com.ea.eadp.mvn.model.dependency;

import com.ea.eadp.mvn.utils.MavenUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * User: BichongLi
 * Date: 12/6/2016
 * Time: 11:51 AM
 */
@XStreamAlias("dependencies")
public class DependencyWrapper {

    @XStreamImplicit(itemFieldName = "dependency")
    private List<Dependency> dependencies;

    public DependencyWrapper(List<Dependency> dependencies) {
        this.dependencies = dependencies;
        this.dependencies.forEach(MavenUtils::fillDependencyInfoFromMavenRepo);
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
}
