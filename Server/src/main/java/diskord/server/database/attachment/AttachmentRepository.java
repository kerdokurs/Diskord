package diskord.server.database.attachment;

import diskord.server.database.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttachmentRepository extends Repository<Attachment, UUID> {
  public AttachmentRepository() {
    super(Attachment.class);
  }

  @Override
  public List<Attachment> getAll() {
    return new ArrayList<>();
  }
}
