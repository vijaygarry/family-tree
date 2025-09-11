package com.neasaa.familytree.constants;

public class ImageConstants {
    // Base directory for storing uploaded images and subdirectories for member and family images
    // This directory is mapped to real directory in WebConfig class in spring boot application.
    public static final String BASE_IMAGE_DIRECTORY = "uploads/";
    public static final String MEMBER_IMAGE_DIRECTORY_NAME = "member/";
    public static final String FAMILY_IMAGE_DIRECTORY_NAME = "family/";


    public static final String DEFAULT_KID_BOY_IMAGE = BASE_IMAGE_DIRECTORY + MEMBER_IMAGE_DIRECTORY_NAME + "kid_boy_avatar.jpg";
    public static final String DEFAULT_KID_GIRL_IMAGE = BASE_IMAGE_DIRECTORY + MEMBER_IMAGE_DIRECTORY_NAME + "kid_girl_avatar.jpg";
    public static final String DEFAULT_UNMARRIED_GIRL_IMAGE = BASE_IMAGE_DIRECTORY + MEMBER_IMAGE_DIRECTORY_NAME + "girl_avatar.jpg";
    public static final String DEFAULT_MARRIED_WOMAN_IMAGE = BASE_IMAGE_DIRECTORY + MEMBER_IMAGE_DIRECTORY_NAME + "woman_avatar.jpg";
    public static final String DEFAULT_OLD_WOMAN_IMAGE = BASE_IMAGE_DIRECTORY + MEMBER_IMAGE_DIRECTORY_NAME + "old_woman_avatar.jpg";
    public static final String DEFAULT_MAN_IMAGE = BASE_IMAGE_DIRECTORY + MEMBER_IMAGE_DIRECTORY_NAME + "user-avatar-man.png";
    public static final String DEFAULT_OLD_MAN_IMAGE = BASE_IMAGE_DIRECTORY + MEMBER_IMAGE_DIRECTORY_NAME + "old_man_avatar.jpg";

    public static final String DEFAULT_FAMILY_IMAGE = BASE_IMAGE_DIRECTORY + FAMILY_IMAGE_DIRECTORY_NAME + "sample-family-image.png";

}
