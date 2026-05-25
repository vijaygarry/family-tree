package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetFamiliesByRegionRequest extends OperationRequest {
  @Serial private static final long serialVersionUID = 1L;

  private String city;
  private String state;
  private String country;

  @Override
  public void normalize() {
    if (city != null) city = city.trim();
    if (state != null) state = state.trim();
    if (country != null) country = country.trim();
  }
}
