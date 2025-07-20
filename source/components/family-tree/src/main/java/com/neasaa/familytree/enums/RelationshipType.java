package com.neasaa.familytree.enums;

public enum RelationshipType {
	Father, Mother, Son, Daughter, Husband, Wife;
	
	public static RelationshipType getRelationshipType (String input) {
		for(RelationshipType relationshipType : RelationshipType.values()) {
			if(relationshipType.name().equalsIgnoreCase(input)) {
				return relationshipType;
			}
		}
		return null;
	}

	public static RelationshipType getReverseRelationship(RelationshipType relationshipType) {
        return switch (relationshipType) {
            case Husband -> Wife;
            case Wife -> Husband;
            case Son, Daughter -> Father;
            default -> throw new RuntimeException("Reverse relationship not defined for: " + relationshipType);
        };
	}
}
