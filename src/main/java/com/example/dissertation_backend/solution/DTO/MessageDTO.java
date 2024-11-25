package com.example.dissertation_backend.solution.DTO;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Message.Model.Message;
import java.util.List;

public class MessageDTO {

  private ApplicationUser otherParty;
  private List<Message> messages;

  public MessageDTO(ApplicationUser otherParty, List<Message> messages) {
    this.otherParty = otherParty;
    this.messages = messages;
  }

  public ApplicationUser getOtherParty() {
    return otherParty;
  }

  public void setOtherParty(ApplicationUser otherParty) {
    this.otherParty = otherParty;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
