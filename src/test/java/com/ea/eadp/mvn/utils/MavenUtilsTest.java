package com.ea.eadp.mvn.utils;

import com.ea.eadp.mvn.model.dependency.Dependency;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: BichongLi
 * Date: 12/7/2016
 * Time: 10:55 AM
 */
public class MavenUtilsTest {

    @Test
    public void testFillInfoFromMavenRepo() {
        Dependency dependency = new Dependency("org.springframework", "spring-core", "jar", "4.3.1.RELEASE", null);
        MavenUtils.fillDependencyInfoFromMavenRepo(dependency);
        Assert.assertNotNull(dependency.getLatestVersion());
        Assert.assertNotNull(dependency.getReleaseTime());
    }

    @Test
    public void testNonExistDependencyInMavenRepo() {
        Dependency dependency = new Dependency("com.ea.eadp", "catalog.app", "war", "1000.0.0-NNG-SNAPSHOT", null);
        MavenUtils.fillDependencyInfoFromMavenRepo(dependency);
        Assert.assertNull(dependency.getLatestVersion());
        Assert.assertNull(dependency.getReleaseTime());
    }

}
