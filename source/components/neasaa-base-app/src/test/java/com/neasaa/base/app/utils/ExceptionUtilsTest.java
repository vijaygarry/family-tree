package com.neasaa.base.app.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.neasaa.base.app.operation.exception.InternalServerException;
import org.junit.jupiter.api.Test;

public class ExceptionUtilsTest {

  @Test
  void getInternalException_withMessage_returnsExceptionWithMessage() {
    InternalServerException ex = ExceptionUtils.getInternalException("something went wrong");
    assertNotNull(ex);
    assertEquals("something went wrong", ex.getMessage());
  }

  @Test
  void getInternalException_withMessageAndCause_returnsExceptionWithCause() {
    Throwable cause = new RuntimeException("root cause");
    InternalServerException ex = ExceptionUtils.getInternalException("something went wrong", cause);
    assertNotNull(ex);
    assertEquals("something went wrong", ex.getMessage());
    assertSame(cause, ex.getCause());
  }
}
