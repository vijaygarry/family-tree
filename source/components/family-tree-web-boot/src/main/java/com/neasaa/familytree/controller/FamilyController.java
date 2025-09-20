package com.neasaa.familytree.controller;

import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.account.GetAccountListOperation;
import com.neasaa.familytree.operation.account.GetAccountStatementOperation;
import com.neasaa.familytree.operation.account.model.GetAccountListResponse;
import com.neasaa.familytree.operation.account.model.GetAccountStatementRequest;
import com.neasaa.familytree.operation.account.model.GetAccountStatementResponse;
import com.neasaa.familytree.operation.events.GetEventsOperation;
import com.neasaa.familytree.operation.events.model.GetEventsRequest;
import com.neasaa.familytree.operation.events.model.GetEventsResponse;
import com.neasaa.familytree.operation.family.GetFamilyDetailsOperation;
import com.neasaa.familytree.operation.family.GetMemberProfileOperation;
import com.neasaa.familytree.operation.family.ManageRelationshipOperation;
import com.neasaa.familytree.operation.family.SearchFamilyOperation;
import com.neasaa.familytree.operation.family.UpdateFamilyDetailsOperation;
import com.neasaa.familytree.operation.family.UpdateFamilyImageOperation;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberResponse;
import com.neasaa.familytree.operation.family.model.AddFamilyRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyResponse;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsResponse;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.ManageRelationshipRequest;
import com.neasaa.familytree.operation.family.model.ManageRelationshipResponse;
import com.neasaa.familytree.operation.family.model.SearchFamilyRequest;
import com.neasaa.familytree.operation.family.model.SearchFamilyResponse;
import com.neasaa.familytree.operation.family.model.UpdateFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.UpdateFamilyDetailsResponse;
import com.neasaa.familytree.operation.family.model.UpdateImageRequest;
import com.neasaa.familytree.utils.Constants;
import com.neasaa.familytree.utils.FileUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.neasaa.familytree.WebRequestHandler;
import com.neasaa.familytree.operation.family.AddFamilyMemberOperation;
import com.neasaa.familytree.operation.family.AddFamilyOperation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


@Log4j2
@RestController
@RequestMapping(value = "/api/family", method = RequestMethod.POST)
public class FamilyController {

	@Value("${app.upload.dir}")
	private String uploadDir;

	@RequestMapping(value = "/addfamily")
	@ResponseBody
	public ResponseEntity<AddFamilyResponse> addFamily (@RequestBody AddFamilyRequest addFamilyRequest) throws Exception {
		return WebRequestHandler.processRequest(AddFamilyOperation.class, addFamilyRequest);
	}
	
	@RequestMapping(value = "/addfamilymember")
	@ResponseBody
	public ResponseEntity<AddFamilyMemberResponse> addFamilyMember (@RequestBody AddFamilyMemberRequest addFamilyMemberRequest) throws Exception {
		return WebRequestHandler.processRequest(AddFamilyMemberOperation.class, addFamilyMemberRequest);
	}

	@RequestMapping(value = "/manageRelationship")
	@ResponseBody
	public ResponseEntity<ManageRelationshipResponse> addFamilyMember (@RequestBody ManageRelationshipRequest manageRelationshipRequest) throws Exception {
		return WebRequestHandler.processRequest(ManageRelationshipOperation.class, manageRelationshipRequest);
	}


	@RequestMapping(value = "/getmemberprofile")
	@ResponseBody
	public ResponseEntity<GetMemberProfileResponse> getMemberProfile (@RequestBody GetMemberProfileRequest getMemberProfileRequest) throws Exception {
		return WebRequestHandler.processRequest(GetMemberProfileOperation.class, getMemberProfileRequest);
	}

	@RequestMapping(value = "/getfamilydetails")
	@ResponseBody
	public ResponseEntity<GetFamilyDetailsResponse> getFamilyDetails (@RequestBody GetFamilyDetailsRequest getFamilyDetailsRequest) throws Exception {
		return WebRequestHandler.processRequest(GetFamilyDetailsOperation.class, getFamilyDetailsRequest);
	}

	@RequestMapping(value = "/searchfamily")
	@ResponseBody
	public ResponseEntity<SearchFamilyResponse> searchFamily (@RequestBody SearchFamilyRequest searchFamilyRequest) throws Exception {
		return WebRequestHandler.processRequest(SearchFamilyOperation.class, searchFamilyRequest);
	}

	@RequestMapping(value = "/getEvents")
	@ResponseBody
	public ResponseEntity<GetEventsResponse> getEvents (@RequestBody GetEventsRequest request) throws Exception {
		return WebRequestHandler.processRequest(GetEventsOperation.class, request);
	}

	@RequestMapping(value = "/getAccountList")
	@ResponseBody
	public ResponseEntity<GetAccountListResponse> getAccountList () throws Exception {
		return WebRequestHandler.processRequest(GetAccountListOperation.class, new EmptyOperationRequest());
	}

	@RequestMapping(value = "/getAccountStatement")
	@ResponseBody
	public ResponseEntity<GetAccountStatementResponse> getAccountStatement (@RequestBody GetAccountStatementRequest request) throws Exception {
		return WebRequestHandler.processRequest(GetAccountStatementOperation.class, request);
	}

	@PostMapping(value = "/updateFamilyDetails")
	@ResponseBody
	public ResponseEntity<UpdateFamilyDetailsResponse> updateFamilyDetails (@RequestBody UpdateFamilyDetailsRequest request) throws Exception {
		return WebRequestHandler.processRequest(UpdateFamilyDetailsOperation.class, request);
	}


	@PostMapping("/updateFamilyImage")
	public ResponseEntity<?> updateFamilyImage (
			@RequestParam("familyId") Integer familyId,
			@RequestParam("image") MultipartFile imageFile) {
		log.info("Updating family image");
		Path path = null;
		try {
			path = saveFileToTempDir(imageFile, uploadDir);
		} catch (ValidationException | InternalServerException e) {
			return WebRequestHandler.buildResponse(e, e.getHttpResponseCode());
		}

		UpdateImageRequest updateImageRequest = new UpdateImageRequest();
		updateImageRequest.setFamilyId(familyId);
		updateImageRequest.setTmpUploadedFilePath(path);
		updateImageRequest.setOperationName(OperationNames.UPDATE_MY_FAMILY_IMAGE);
		return WebRequestHandler.processRequest(UpdateFamilyImageOperation.class, updateImageRequest);
	}


	private static Path saveFileToTempDir (MultipartFile imageFile, String uploadDir) {
		log.info("Uploading file to tmp directory");

		if (imageFile.isEmpty()) {
			throw new ValidationException("No image file provided.");
		}

		String contentType = imageFile.getContentType();
		String originalFilename = imageFile.getOriginalFilename();
		log.info("Uploading {} family image", originalFilename);
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new ValidationException ("File is not an image.");
		}

		if (originalFilename == null ||
				!(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg") || originalFilename.endsWith(".png"))) {
			throw new ValidationException ("Image type is not supported.");
		}

		try {
			log.info("File size from image : {}", imageFile.getSize());
			long fileSizeInKB = imageFile.getSize() / 1024;
			if(fileSizeInKB > Constants.MAX_IMAGE_SIZE_ALLOWED_IN_KB) { // 5 MB limit
				throw new ValidationException("Image file size exceeds the maximum limit of 5 MB.");
			}

			Random rand = new Random(); // pseudorandom generator

			int n = rand.nextInt(100);

			// Save file with a unique name (timestamp + random number)
			String filename = System.currentTimeMillis() + "-" + n + "." + FileUtils.getFileExtension(originalFilename);
			log.info("Saving image file to tmp directory with filename: {}", filename);
			Path filePath = Paths.get(uploadDir + "/tmp", filename);
			Files.write(filePath, imageFile.getBytes());

			// Return temp file path
			return filePath;

		} catch (IOException e) {
			log.info("Failed to upload image to tmp directory", e);
			throw new InternalServerException("Failed to save image.");
		}
	}

	private static ResponseEntity<? extends OperationResponse> buildValidationExceptionResponse (String responseMessage ) {
		ValidationException ve = new ValidationException(responseMessage);
		return WebRequestHandler.buildResponse(ve, ve.getHttpResponseCode());
	}


}
