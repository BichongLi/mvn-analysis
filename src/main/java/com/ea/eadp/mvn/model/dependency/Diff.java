package com.ea.eadp.mvn.model.dependency;

import com.ea.eadp.mvn.model.common.DependencyDiff;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * User: BichongLi
 * Date: 12/4/2016
 * Time: 6:25 PM
 */
@XStreamAlias("diff")
public class Diff {

    private DependencyDiff diffType;

    private String leftDependency;

    private String rightDependency;

    private String reference;

    public Diff(DependencyDiff diffType, String leftDependency,
                String rightDependency, String reference) {
        this.diffType = diffType;
        this.leftDependency = leftDependency;
        this.rightDependency = rightDependency;
        this.reference = reference;
    }

    public DependencyDiff getDiffType() {
        return diffType;
    }

    public void setDiffType(DependencyDiff diffType) {
        this.diffType = diffType;
    }

    public String getLeftDependency() {
        return leftDependency;
    }

    public void setLeftDependency(String leftDependency) {
        this.leftDependency = leftDependency;
    }

    public String getRightDependency() {
        return rightDependency;
    }

    public void setRightDependency(String rightDependency) {
        this.rightDependency = rightDependency;
    }

    @Override
    public String toString() {
        if (diffType == null) {
            if (rightDependency == null) {
                return String.format("Dependency only exists in left: %1$s", leftDependency);
            }
            if (leftDependency == null) {
                return String.format("Dependency only exists in right: %1$s", rightDependency);
            }
        } else {
            switch (diffType) {
                case DIFFERENT_VERSION:
                    return String.format("Different versions left: %1$s    right: %2$s", leftDependency, rightDependency);
                case SIMILAR:
                    return String.format("Similar dependency needs check left: %1$s    right: %2$s", leftDependency, rightDependency);
            }
        }
        return String.format("Diff type: %1$s, left: %2$s, right: %3$s", diffType, leftDependency, rightDependency);
    }
}
