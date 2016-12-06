package com.ea.eadp.mvn.model.dependency;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * User: BichongLi
 * Date: 12/6/2016
 * Time: 10:54 AM
 */
@XStreamAlias("diffResult")
public class DiffResult {

    private List<Diff> differences = new ArrayList<>();

    private List<Diff> leftOnly = new ArrayList<>();

    private List<Diff> rightOnly = new ArrayList<>();

    public DiffResult(List<Diff> differences, List<Diff> leftOnly, List<Diff> rightOnly) {
        this.differences = differences;
        this.leftOnly = leftOnly;
        this.rightOnly = rightOnly;
    }

    public List<Diff> getDifferences() {
        return differences;
    }

    public void setDifferences(List<Diff> differences) {
        this.differences = differences;
    }

    public List<Diff> getLeftOnly() {
        return leftOnly;
    }

    public void setLeftOnly(List<Diff> leftOnly) {
        this.leftOnly = leftOnly;
    }

    public List<Diff> getRightOnly() {
        return rightOnly;
    }

    public void setRightOnly(List<Diff> rightOnly) {
        this.rightOnly = rightOnly;
    }
}
