package diskord.client;
import diskord.payload.Payload;
import diskord.payload.PayloadType;

import java.util.*;

public class TestData {
    // Main stage
    public static User[] getTestUsers(int numberOfUsers){
        User[] users = new User[numberOfUsers];
        for (int i = 0; i< numberOfUsers; i++){
            users[i] =
                    new User(
                            "Test user: " + i,
                            UUID.randomUUID(),
                            base64Icon());
        }
        return users;
    }

    private static String base64Icon(){
        return  "iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAAAAXNSR0IArs4c6QAA" +
                "AARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAJySURBVFhH7Zi9" +
                "dcIwEMdtSEFJSQlkAVYIEzACJSVTwAahw2QJYKLQhQ6agKIzPnI6naxzgAfJi977" +
                "WbJ1H39LwpZJzcGY5BYlLeoLS62oH7b8C7y0PLzA4I/keOEiv9adB+O8LRbJc7eT" +
                "1GupB1zv919yG01Zr9dJx/qk1hfqhdIvLzCCnNnrDEY1SqPRMIejdREwBavlyjSb" +
                "Tc+33W7nedAuhC/QXmy1Wl7AECzeGTiExFFAaDbP/AAFXgaoaIDdfs9NnH7eh2jE" +
                "ITATYhBLVCDrVvUDfBZ6vV5eDwYDs/3YmuFw6PSLQSxiBuo4zzLPhPbzPoTajEaj" +
                "/OLmfeMYURt6nSJmgCGnzrBOUOhytXL6mOsZaiOtMVgC1Ib3I2KG6WTqOJch+QPS" +
                "TVKhfAlQX0pwCDQiceokqtxkWZzwHBWEEpUFRbQiJV8kKvAalAmN3WgKB2v4sOV/" +
                "u3Vp+QX7wZutwVhY3Ybzb04x3YCGqVmbbrXNqVToMycGvD/hlQVuWuCVl83n1t8G" +
                "cJBzcNQCq+zvOLJIOQ9HJTAkLvQWkN4cvkjfTyIqUBKnew9PHB/AFSn7caIC+bZI" +
                "I+6EPVi4UBB5VYE0uF6cC59yuMZMPLBRSWDZ11cMGgfOWbcHNqICYUow8Gl6ZLsy" +
                "pO09M/HARlSg9IuMfctypO09M/HARlQgMB6PnQQUEBv6h0B6sOM6ZqYe2FAJ3O/2" +
                "3nfsT8GYJLwINlQCKdrvDIl89PhfLZ8MvF7kqyyQoh1Z5/FExQERgTfbD5rAdi8t" +
                "smHW9AiHUzsvcA7l6VTdbz+IoviNsPO7jeC5QPqaJZdR/xZoDvaQJF9nEvS0a9iL" +
                "wwAAAABJRU5ErkJggg==";
    }

    public static User currentUser (){
        return new CurrentUser("test user",
                UUID.fromString("0fde0c13-f951-450d-be81-4498f9118722"),
                "user", base64Icon());
    }

    public static Payload getUserSuscribedServers(){
        Payload response = new Payload();
        response.setType(PayloadType.INFO_USER_SERVERS_OK);

        Set<Server> joined = new HashSet<>();
        joined.add(new Server(UUID.fromString("5a5ecd37-c5af-4983-90ea-53f1f18491dc"),
                "Test server1",
                "Test desc",
                base64Icon())); // Request server channels separately
        response.putBody("joined", joined);
        Set<UUID> privileged = new HashSet<>();
        privileged.add(UUID.fromString("5a5ecd37-c5af-4983-90ea-53f1f18491dc"));
        response.putBody("privileged", privileged);
        return response;
    }


    public static Payload getChannelJoin(){
        Payload response = new Payload();
        response.setType(PayloadType.JOIN_CHANNEL_OK);
        response.putBody("users", getTestUsers(10));
        return response;
    }

    public static Payload getSendChat(){
        Payload response = new Payload();
        response.setType(PayloadType.MSG_OK);
        return response;
    }

    public static Payload getServerChannels(UUID serverUUID){
        Payload response = new Payload();
        response.setType(PayloadType.INFO_CHANNELS_OK);

        List<Channel> channels = new ArrayList<>();
        channels.add(new Channel("Test channel 1",
                UUID.fromString("b760dbb0-d626-4579-8997-2d50037bb369"),
                base64Icon()));
        channels.add(new Channel("Test channel 2",
                UUID.fromString("79e4a52b-6ab2-43b4-b142-2e12a0476c79"),
                base64Icon()));
        channels.add(new Channel("Test channel 3",
                UUID.fromString("ba793031-f95b-445b-9f2f-2ddfe5b1cf20"),
                base64Icon()));
        response.putBody("channels", channels);
        return response;
    }

    public static Payload getServerRegistrationResponse(){
        Payload response = new Payload();
        response.setType(PayloadType.REGISTER_SERVER_OK);
        return response;
    }

    public static Payload getChannelRegistrationResponse(){
        Payload response = new Payload();
        response.setType(PayloadType.REGISTER_CHANNEL_OK);
        return response;
    }

    // Login
    public static Payload getLogin(){
        Payload response = new Payload();
        response.setType(PayloadType.LOGIN_OK);
        response.putBody("username", "TestCurrentUser");
        response.putBody("uuid",UUID.randomUUID());
        response.putBody("token", "TESTTOKEN");
        response.putBody("icon", base64Icon());

        return response;
    }

    // register
    public static Payload getRegister(){
        Payload response = new Payload();
        response.setType(PayloadType.REGISTER_OK);
        return response;
    }

    // Join server

    public static Payload getJoinServer(){
        Payload response = new Payload();
        response.setType(PayloadType.JOIN_SERVER_OK);
        return response;
    }
}
