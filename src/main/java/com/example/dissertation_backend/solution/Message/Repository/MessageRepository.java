package com.example.dissertation_backend.solution.Message.Repository;

import com.example.dissertation_backend.solution.Customers.Model.ApplicationUser;
import com.example.dissertation_backend.solution.Message.Model.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
  List<Message> findByReceiver(ApplicationUser receiver);
  // List<Message> findByReceiverOrderByDateSentDesc(ApplicationUser receiver);
  List<Message> findBySenderAndReceiver(
    ApplicationUser sender,
    ApplicationUser receiver
  );
  List<Message> findBySenderOrReceiver(
    ApplicationUser user,
    ApplicationUser user2
  );

  Long countByReceiverAndIsRead(ApplicationUser receiver, boolean isRead);
}
