package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.constants.ImageConstants;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.operation.family.model.UpdateImageRequest;
import com.neasaa.familytree.operation.family.model.UpdateImageResponse;
import com.neasaa.familytree.utils.FileUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_IMAGE;

@Log4j2
@Component("UpdateFamilyImageOperation")
@Scope("prototype")
public class UpdateFamilyImageOperation extends AbstractOperation<UpdateImageRequest, UpdateImageResponse> {

    @Autowired
    private FamilyDao familyDao;

    @Override
    public String getOperationName() {
        return UPDATE_MY_FAMILY_IMAGE;
    }

    @Override
    public void doValidate(UpdateImageRequest opRequest) throws OperationException {
        if(opRequest == null) {
            throw new ValidationException("Invalid request provided.");
        }
        if(opRequest.getFamilyId() == null || opRequest.getFamilyId() <= 0) {
            throw new ValidationException("Family id is not provided.");
        }
        Path tmpUploadedFilePath = opRequest.getTmpUploadedFilePath();
        if(tmpUploadedFilePath == null) {
            throw new ValidationException("Temporary uploaded file path is not provided.");
        }
        if(!tmpUploadedFilePath.toFile().exists()) {
            log.info("Temporary uploaded file does not exist at path: {}", tmpUploadedFilePath.toString());
            throw new ValidationException("Temporary uploaded file does not exist");
        }
    }

    @Override
    public UpdateImageResponse doExecute(UpdateImageRequest opRequest) throws OperationException {
        int familyId = opRequest.getFamilyId();
        Path tmpUploadedFilePath = opRequest.getTmpUploadedFilePath();
        Path tmpDirectory = tmpUploadedFilePath.getParent();
        Path uploadDirectory = tmpDirectory.getParent();
        String fileExtension = FileUtils.getFileExtension(tmpUploadedFilePath.getFileName().toString());

        // Build the path for the family image based on the family ID and predefined constants
        String familyImageAbsolutePath = buildFamilyImageAbsolutePath(uploadDirectory, familyId, fileExtension);
        Path familyImagePath = Path.of(familyImageAbsolutePath);
        String familyImageRelativePath = buildFamilyImageRelativePath(familyImagePath);

        // move the image from the temporary uploaded file path to the family image location
        try {
            Files.move(tmpUploadedFilePath, familyImagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("Error occurred while moving the uploaded file to family image path: {}", familyImagePath, e);
            throw new InternalServerException("Failed to upload the file");
        }

        // Update the family record in the database with the new image path
        familyDao.updateFamilyImagePath (familyId, familyImageRelativePath, getContext().getAuditInfo());

        // Return an empty response indicating the operation was successful
        UpdateImageResponse updateImageResponse = new UpdateImageResponse();
        updateImageResponse.setUploadedImagePath(familyImageRelativePath);
        updateImageResponse.setOperationMessage("Family image updated successfully.");
        return updateImageResponse;
    }

    private String buildFamilyImageAbsolutePath(Path uploadDirectory, int familyId, String fileExtension) {
        return uploadDirectory.toAbsolutePath() + "/" + ImageConstants.FAMILY_IMAGE_DIRECTORY_NAME + "/family_" + familyId + "_" + System.currentTimeMillis() + "." + fileExtension;
    }

    private String buildFamilyImageRelativePath(Path familyImagePath) {
        return ImageConstants.BASE_IMAGE_DIRECTORY + "/" + ImageConstants.FAMILY_IMAGE_DIRECTORY_NAME + "/" + familyImagePath.getFileName().toString();
    }



}
