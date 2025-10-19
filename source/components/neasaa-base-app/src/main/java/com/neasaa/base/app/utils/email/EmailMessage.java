package com.neasaa.base.app.utils.email;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailMessage {
  public enum EmailType {
    TEXT,
    HTML
  }

  private String from;
  private String fromDisplayName;
  private String replyTo;
  private List<String> to;
  private List<String> cc;
  private List<String> bcc;
  private String subject;
  private String body;
  private EmailType type;

  // Map<AttachmentName, FilePath>
  private Map<String, String> attachments;

  // Map<ContentId, FilePath> for inline images
  private Map<String, String> inlineResources;
}
