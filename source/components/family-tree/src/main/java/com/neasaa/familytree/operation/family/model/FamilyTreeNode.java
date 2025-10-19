package com.neasaa.familytree.operation.family.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FamilyTreeNode {

  private final MemberSummaryDto member;
  private MemberSummaryDto spouse;
  private List<FamilyTreeNode> children;

  public FamilyTreeNode(MemberSummaryDto member) {
    this.member = member;
  }

  public void addChild(FamilyTreeNode child) {
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(child);
  }
}
