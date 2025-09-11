package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateImageResponse extends OperationResponse {
    String uploadedImagePath;
}
