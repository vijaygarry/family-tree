package com.neasaa.familytree.operation.family;

import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_MEMBER_IMAGE;

import com.neasaa.base.app.operation.exception.AccessDeniedException;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.constants.ImageConstants;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.operation.family.model.UpdateImageRequest;
import com.neasaa.familytree.operation.family.model.UpdateImageResponse;
import com.neasaa.familytree.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("UpdateFamilyMemberImageOperation")
@Scope("prototype")
public class UpdateFamilyMemberImageOperation
    extends FamilyAbstractOperation<UpdateImageRequest, UpdateImageResponse> {

  @Override
  public String getOperationName() {
    return UPDATE_MY_FAMILY_MEMBER_IMAGE;
  }

  @Override
  public void doValidate(UpdateImageRequest opRequest) throws OperationException {
    if (opRequest == null) {
      throw new ValidationException("Invalid request provided.");
    }
    if (opRequest.getMemberId() == null || opRequest.getMemberId() <= 0) {
      throw new ValidationException("Member id is not provided.");
    }
    Path tmpUploadedFilePath = opRequest.getTmpUploadedFilePath();
    if (tmpUploadedFilePath == null) {
      throw new ValidationException("Temporary uploaded file path is not provided.");
    }
    if (!tmpUploadedFilePath.toFile().exists()) {
      log.info(
          "Temporary uploaded file does not exist at path: {}", tmpUploadedFilePath.toString());
      throw new ValidationException("Temporary uploaded file does not exist");
    }
  }

  @Override
  public UpdateImageResponse doExecute(UpdateImageRequest opRequest) throws OperationException {
    int memberId = opRequest.getMemberId();
    FamilyMemberEntity memberEntity = familyMemberDao.getMemberById(memberId);
    if (memberEntity == null) {
      log.info("Family member not found for the provided member id: {}", memberId);
      throw new ValidationException("Family member not found ");
    }

    if (!canLoggedInUserUpdateMember(memberEntity.getFamilyId())) {
      log.info("User is not allowed to update the member details for member id: {}", memberId);
      throw new AccessDeniedException("You are not allowed to update the member image.");
    }

    Path tmpUploadedFilePath = opRequest.getTmpUploadedFilePath();
    Path tmpDirectory = tmpUploadedFilePath.getParent();
    Path uploadDirectory = tmpDirectory.getParent();
    String fileExtension = FileUtils.getFileExtension(tmpUploadedFilePath.getFileName().toString());

    // Build the path for the member image based on the member ID and predefined constants
    String memberImageAbsolutePath =
        buildMemberImageAbsolutePath(uploadDirectory, memberId, fileExtension);
    Path memberImagePath = Path.of(memberImageAbsolutePath);
    String memberImageRelativePath = buildMemberImageRelativePath(memberImagePath);

    // move the image from the temporary uploaded file path to the member image location
    try {
      Files.move(tmpUploadedFilePath, memberImagePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      log.error(
          "Error occurred while moving the uploaded file to family image path: {}",
          memberImagePath,
          e);
      throw new InternalServerException("Failed to upload the file");
    }

    // Update the member record in the database with the new image path
    familyMemberDao.updateMemberImagePath(
        memberId, memberImageRelativePath, getContext().getAuditInfo());

    // Return an empty response indicating the operation was successful
    UpdateImageResponse updateImageResponse = new UpdateImageResponse();
    updateImageResponse.setUploadedImagePath(memberImageRelativePath);
    updateImageResponse.setOperationMessage("Member image updated successfully.");
    return updateImageResponse;
  }

  private String buildMemberImageAbsolutePath(
      Path uploadDirectory, int memberId, String fileExtension) {
    return uploadDirectory.toAbsolutePath()
        + "/"
        + ImageConstants.MEMBER_IMAGE_DIRECTORY_NAME
        + "/member_"
        + memberId
        + "_"
        + System.currentTimeMillis()
        + "."
        + fileExtension;
  }

  private String buildMemberImageRelativePath(Path familyImagePath) {
    return ImageConstants.BASE_IMAGE_DIRECTORY
        + "/"
        + ImageConstants.MEMBER_IMAGE_DIRECTORY_NAME
        + "/"
        + familyImagePath.getFileName().toString();
  }
}
