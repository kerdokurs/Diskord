package diskord.client.controllers.listview;

import diskord.client.ChatFile;
import diskord.client.User;
import lombok.Getter;

/**
 * Custom Listview Listcell for . It holds the content of single ListViewRow
 */
public class ListViewChatRow {
    @Getter
    private final User user; // Get username and icon from user object
    @Getter
    private final String message;
    @Getter
    private final String timestamp; // The date when message was sent
    @Getter
    private final ChatFile file;

    /**
     * Custom listview row object for chat listview.
     *
     * @param user      User object that has UUID, name and icon
     * @param message   Message data
     * @param timestamp Date when message was sent
     * @param file      ChatFile object that has file name,base64 and UUID. UUID is necessary so file can be
     *                  requested from server with UUID.
     */

    public ListViewChatRow(User user, String message, String timestamp, ChatFile file) {
        super();
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
        this.file = file;
    }
}