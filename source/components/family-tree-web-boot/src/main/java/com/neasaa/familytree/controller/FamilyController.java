package com.neasaa.familytree.controller;

import com.neasaa.base.app.operation.exception.InternalServerException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.base.app.operation.model.EmptyOperationRequest;
import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.familytree.webutils.WebRequestHandler;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.account.GetAccountListOperation;
import com.neasaa.familytree.operation.account.GetAccountStatementOperation;
import com.neasaa.familytree.operation.account.model.GetAccountListResponse;
import com.neasaa.familytree.operation.account.model.GetAccountStatementRequest;
import com.neasaa.familytree.operation.account.model.GetAccountStatementResponse;
import com.neasaa.familytree.operation.events.GetEventsOperation;
import com.neasaa.familytree.operation.events.model.GetEventsRequest;
import com.neasaa.familytree.operation.events.model.GetEventsResponse;
import com.neasaa.familytree.operation.family.AddFamilyMemberOperation;
import com.neasaa.familytree.operation.family.AddFamilyOperation;
import com.neasaa.familytree.operation.family.FamilyRegistrationOperation;
import com.neasaa.familytree.operation.family.GetFamilyDetailsOperation;
import com.neasaa.familytree.operation.family.GetMemberProfileOperation;
import com.neasaa.familytree.operation.family.ManageRelationshipOperation;
import com.neasaa.familytree.operation.family.ProcessFamilyRegistrationOperation;
import com.neasaa.familytree.operation.family.SearchFamilyMemberOperation;
import com.neasaa.familytree.operation.family.SearchFamilyOperation;
import com.neasaa.familytree.operation.family.UpdateFamilyDetailsOperation;
import com.neasaa.familytree.operation.family.UpdateFamilyImageOperation;
import com.neasaa.familytree.operation.family.UpdateFamilyMemberImageOperation;
import com.neasaa.familytree.operation.family.GetFamiliesByRegionOperation;
import com.neasaa.familytree.operation.family.GetFamilyCountByCityOperation;
import com.neasaa.familytree.operation.family.UpdateFamilyMemberProfileOperation;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberResponse;
import com.neasaa.familytree.operation.family.model.AddFamilyRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyResponse;
import com.neasaa.familytree.operation.family.model.FamilyRegistrationRequest;
import com.neasaa.familytree.operation.family.model.FamilyRegistrationResponse;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsResponse;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.ManageRelationshipRequest;
import com.neasaa.familytree.operation.family.model.ManageRelationshipResponse;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationRequest;
import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationResponse;
import com.neasaa.familytree.operation.family.model.SearchFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.SearchFamilyMemberResponse;
import com.neasaa.familytree.operation.family.model.SearchFamilyRequest;
import com.neasaa.familytree.operation.family.model.SearchFamilyResponse;
import com.neasaa.familytree.operation.family.model.UpdateFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.UpdateFamilyDetailsResponse;
import com.neasaa.familytree.operation.family.model.UpdateFamilyMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.UpdateFamilyMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.GetFamiliesByRegionRequest;
import com.neasaa.familytree.operation.family.model.GetFamiliesByRegionResponse;
import com.neasaa.familytree.operation.family.model.UpdateImageRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyCountByCityResponse;
import com.neasaa.familytree.operation.samaj.GetSamajStatisticsOperation;
import com.neasaa.familytree.operation.samaj.model.GetSamajStatisticsRequest;
import com.neasaa.familytree.operation.samaj.model.GetSamajStatisticsResponse;
import com.neasaa.familytree.utils.Constants;
import com.neasaa.familytree.utils.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
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
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequestMapping(value = "/api/family", method = RequestMethod.POST)
public class FamilyController {

  // TODO: Read this config from AppProperties class
  @Value("${app.upload.dir}")
  private String uploadDir;

  @RequestMapping(value = "/addfamily")
  @ResponseBody
  public ResponseEntity<AddFamilyResponse> addFamily(@RequestBody AddFamilyRequest addFamilyRequest)
      throws Exception {
    return WebRequestHandler.processRequest(AddFamilyOperation.class, addFamilyRequest);
  }

  @RequestMapping(value = "/familyRegistrationRequest")
  @ResponseBody
  public ResponseEntity<FamilyRegistrationResponse> registerFamily(@RequestBody FamilyRegistrationRequest familyRegistrationRequest)
      throws Exception {
    return WebRequestHandler.processRequest(FamilyRegistrationOperation.class, familyRegistrationRequest);
  }

  @RequestMapping(value = "/processFamilyRegistrationRequest")
  @ResponseBody
  public ResponseEntity<ProcessFamilyRegistrationResponse> registerFamily(@RequestBody ProcessFamilyRegistrationRequest processFamilyRegistrationRequest)
          throws Exception {
    return WebRequestHandler.processRequest(ProcessFamilyRegistrationOperation.class, processFamilyRegistrationRequest);
  }

  @RequestMapping(value = "/getfamilydetails")
  @ResponseBody
  public ResponseEntity<GetFamilyDetailsResponse> getFamilyDetails(
      @RequestBody GetFamilyDetailsRequest getFamilyDetailsRequest) throws Exception {
    return WebRequestHandler.processRequest(
        GetFamilyDetailsOperation.class, getFamilyDetailsRequest);
  }

  @RequestMapping(value = "/searchfamily")
  @ResponseBody
  public ResponseEntity<SearchFamilyResponse> searchFamily(
      @RequestBody SearchFamilyRequest searchFamilyRequest) throws Exception {
    return WebRequestHandler.processRequest(SearchFamilyOperation.class, searchFamilyRequest);
  }

  @PostMapping(value = "/updateFamilyDetails")
  @ResponseBody
  public ResponseEntity<UpdateFamilyDetailsResponse> updateFamilyDetails(
      @RequestBody UpdateFamilyDetailsRequest request) throws Exception {
    return WebRequestHandler.processRequest(UpdateFamilyDetailsOperation.class, request);
  }

  @PostMapping("/updateFamilyImage")
  public ResponseEntity<?> updateFamilyImage(
      @RequestParam("familyId") Integer familyId, @RequestParam("image") MultipartFile imageFile) {
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

  @RequestMapping(value = "/addFamilyMember")
  @ResponseBody
  public ResponseEntity<AddFamilyMemberResponse> addFamilyMember(
      @RequestBody AddFamilyMemberRequest addFamilyMemberRequest) throws Exception {
    return WebRequestHandler.processRequest(AddFamilyMemberOperation.class, addFamilyMemberRequest);
  }

  @RequestMapping(value = "/manageRelationship")
  @ResponseBody
  public ResponseEntity<ManageRelationshipResponse> addFamilyMember(
      @RequestBody ManageRelationshipRequest manageRelationshipRequest) throws Exception {
    return WebRequestHandler.processRequest(
        ManageRelationshipOperation.class, manageRelationshipRequest);
  }

  @RequestMapping(value = "/getmemberprofile")
  @ResponseBody
  public ResponseEntity<GetMemberProfileResponse> getMemberProfile(
      @RequestBody GetMemberProfileRequest getMemberProfileRequest) throws Exception {
    return WebRequestHandler.processRequest(
        GetMemberProfileOperation.class, getMemberProfileRequest);
  }

  @RequestMapping(value = "/searchMember")
  @ResponseBody
  public ResponseEntity<SearchFamilyMemberResponse> searchMember (
          @RequestBody SearchFamilyMemberRequest searchFamilyMemberRequest) throws Exception {
    return WebRequestHandler.processRequest(SearchFamilyMemberOperation.class, searchFamilyMemberRequest);
  }

  @PostMapping(value = "/updateMemberProfile")
  @ResponseBody
  public ResponseEntity<UpdateFamilyMemberProfileResponse> updateMemberProfile(
      @RequestBody UpdateFamilyMemberProfileRequest request) throws Exception {
    return WebRequestHandler.processRequest(UpdateFamilyMemberProfileOperation.class, request);
  }

  @PostMapping("/updateMemberImage")
  public ResponseEntity<?> updateMemberImage(
      @RequestParam("memberId") Integer memberId, @RequestParam("image") MultipartFile imageFile) {
    log.info("Updating Member image");
    Path path = null;
    try {
      path = saveFileToTempDir(imageFile, uploadDir);
    } catch (ValidationException | InternalServerException e) {
      return WebRequestHandler.buildResponse(e, e.getHttpResponseCode());
    }

    UpdateImageRequest updateImageRequest = new UpdateImageRequest();
    updateImageRequest.setMemberId(memberId);
    updateImageRequest.setTmpUploadedFilePath(path);
    updateImageRequest.setOperationName(OperationNames.UPDATE_MY_FAMILY_MEMBER_IMAGE);
    return WebRequestHandler.processRequest(
        UpdateFamilyMemberImageOperation.class, updateImageRequest);
  }

  @RequestMapping(value = "/getEvents")
  @ResponseBody
  public ResponseEntity<GetEventsResponse> getEvents(@RequestBody GetEventsRequest request)
      throws Exception {
    return WebRequestHandler.processRequest(GetEventsOperation.class, request);
  }

  @RequestMapping(value = "/getAccountList")
  @ResponseBody
  public ResponseEntity<GetAccountListResponse> getAccountList() throws Exception {
    return WebRequestHandler.processRequest(
        GetAccountListOperation.class, new EmptyOperationRequest());
  }

  @RequestMapping(value = "/getAccountStatement")
  @ResponseBody
  public ResponseEntity<GetAccountStatementResponse> getAccountStatement(
      @RequestBody GetAccountStatementRequest request) throws Exception {
    return WebRequestHandler.processRequest(GetAccountStatementOperation.class, request);
  }

  private static Path saveFileToTempDir(MultipartFile imageFile, String uploadDir) {
    log.info("Uploading file to tmp directory");

    if (imageFile.isEmpty()) {
      throw new ValidationException("No image file provided.");
    }

    String contentType = imageFile.getContentType();
    String originalFilename = imageFile.getOriginalFilename();
    log.info("Uploading {} image", originalFilename);
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new ValidationException("File is not an image.");
    }

    if (originalFilename == null
        || !(originalFilename.endsWith(".jpg")
            || originalFilename.endsWith(".jpeg")
            || originalFilename.endsWith(".png"))) {
      throw new ValidationException("Image type is not supported.");
    }

    try {
      log.info("File size from image : {}", imageFile.getSize());
      long fileSizeInKB = imageFile.getSize() / 1024;
      if (fileSizeInKB > Constants.MAX_IMAGE_SIZE_ALLOWED_IN_KB) { // 5 MB limit
        throw new ValidationException("Image file size exceeds the maximum limit of 5 MB.");
      }

      Random rand = new Random(); // pseudorandom generator

      int n = rand.nextInt(100);

      // Save file with a unique name (timestamp + random number)
      String filename =
          System.currentTimeMillis() + "-" + n + "." + FileUtils.getFileExtension(originalFilename);
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

  @RequestMapping(value = "/getSamajStats")
  @ResponseBody
  public ResponseEntity<GetSamajStatisticsResponse> getSamajStats (
          @RequestBody GetSamajStatisticsRequest getSamajStatisticsRequest) throws Exception {
    return WebRequestHandler.processRequest(GetSamajStatisticsOperation.class, getSamajStatisticsRequest);
  }

  @RequestMapping(value = "/getFamilyCountByCity")
  @ResponseBody
  public ResponseEntity<GetFamilyCountByCityResponse> getFamilyCountByCity (
          @RequestBody EmptyOperationRequest request) throws Exception {
    return WebRequestHandler.processRequest(GetFamilyCountByCityOperation.class, request);
  }

  @RequestMapping(value = "/getFamiliesByRegion")
  @ResponseBody
  public ResponseEntity<GetFamiliesByRegionResponse> getFamiliesByRegion(
          @RequestBody GetFamiliesByRegionRequest request) throws Exception {
    return WebRequestHandler.processRequest(GetFamiliesByRegionOperation.class, request);
  }

  private static ResponseEntity<? extends OperationResponse> buildValidationExceptionResponse(
      String responseMessage) {
    ValidationException ve = new ValidationException(responseMessage);
    return WebRequestHandler.buildResponse(ve, ve.getHttpResponseCode());
  }
}
