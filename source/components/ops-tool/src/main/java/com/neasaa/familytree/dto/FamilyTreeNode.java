package com.neasaa.familytree.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FamilyTreeNode {

    private ExcelFamilyMember familyMember;
    private FamilyTreeNode spouse;
//    private FamilyTreeNode father;
//    private FamilyTreeNode mother;
    private List<FamilyTreeNode> children;
    private boolean addedToTree = false;

    public FamilyTreeNode (ExcelFamilyMember familyMember) {
        this.familyMember = familyMember;
    }
    public void addChild(FamilyTreeNode child) {
        if(this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }
}
