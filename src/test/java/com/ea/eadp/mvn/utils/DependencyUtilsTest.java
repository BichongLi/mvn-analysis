package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.TreeBaseTest;
import com.ea.eadp.mvn.model.dependency.Dependency;
import com.ea.eadp.mvn.model.dependency.TreeNode;
import com.ea.eadp.mvn.model.exception.AnalyzeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * User: BichongLi
 * Date: 12/7/2016
 * Time: 9:32 AM
 */
public class DependencyUtilsTest extends TreeBaseTest {

    @Test
    public void testSuccessfullyGenerateDependency() {
        Dependency dependency = DependencyUtils.generateDependency("org.springframework:spring-core:jar:4.3.1.RELEASE:compile");
        Assert.assertEquals("org.springframework", dependency.getGroupId());
        Assert.assertEquals("spring-core", dependency.getArtifactId());
        Assert.assertEquals("jar", dependency.getType());
        Assert.assertEquals("4.3.1.RELEASE", dependency.getVersion());
        Assert.assertEquals("compile", dependency.getScope());
        Assert.assertEquals("https://mvnrepository.com/artifact/org.springframework/spring-core", dependency.getReference());
    }

    @Test(expected = AnalyzeException.class)
    public void testWrongGenerateDependency() {
        DependencyUtils.generateDependency("wrong dependency string");
    }

    @Test
    public void testSuccessfullyGenerateTree() {
        List<String> edges = genCorrectTreeEdges();
        TreeNode root = DependencyUtils.generateDependencyTree(edges);
        validateTree(root, "com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT", 4, 4);
    }

    private List<String> genCorrectTreeEdges() {
        List<String> edges = new ArrayList<>();
        edges.add("\"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\" -> \"com.ea.eadp:catalog.web2:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\" -> \"com.ea.eadp:catalog.watchdog:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\" -> \"com.ea.eadp:catalog.rest:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\" -> \"com.ea.eadp:catalog.acl:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.rest:jar:1000.0.0-NNG-SNAPSHOT:compile\" -> \"com.ea.eadp:catalog.service:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.rest:jar:1000.0.0-NNG-SNAPSHOT:compile\" -> \"com.ea.eadp:catalog.common:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.service:jar:1000.0.0-NNG-SNAPSHOT:compile\" -> \"com.ea.eadp:catalog.model:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        return edges;
    }

    @Test(expected = AnalyzeException.class)
    public void testWrongGenerateTree() {
        List<String> edges = genTreeEdgesWithCircle();
        DependencyUtils.generateDependencyTree(edges);
    }

    private List<String> genTreeEdgesWithCircle() {
        List<String> edges = new ArrayList<>();
        edges.add("\"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\" -> \"com.ea.eadp:catalog.web2:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\" -> \"com.ea.eadp:catalog.watchdog:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\" -> \"com.ea.eadp:catalog.rest:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\" -> \"com.ea.eadp:catalog.acl:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.rest:jar:1000.0.0-NNG-SNAPSHOT:compile\" -> \"com.ea.eadp:catalog.service:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.rest:jar:1000.0.0-NNG-SNAPSHOT:compile\" -> \"com.ea.eadp:catalog.common:jar:1000.0.0-NNG-SNAPSHOT:compile\"");
        edges.add("\"com.ea.eadp:catalog.service:jar:1000.0.0-NNG-SNAPSHOT:compile\" -> \"com.ea.eadp:catalog.app:war:1000.0.0-NNG-SNAPSHOT\"");
        return edges;
    }

}
