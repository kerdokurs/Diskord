package diskord.client;

import diskord.client.payload.Payload;
import diskord.client.payload.PayloadType;
import javafx.scene.image.Image;

import java.util.UUID;

public class TestData {
    public static Payload getLoginData(){
        return new Payload()
            .putBody("username","TestUser")
            .putBody("uuid","bf867bfc-9541-11eb-a8b3-0242ac130003")
            .putBody("userIconBase64","iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAAAAXNSR0IArs4c6QAA" +
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
             "wwAAAABJRU5ErkJggg==")
            .putBody("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrZXJkbyIsImlzcyI6ImRpc2tvcmQiLCJpYXQiOjE2MTkyNzk3MDh9.ZbZ38J6BdBVCPNxjxfw0icHQRASn5zcV4y0hWqlHBVc")
            .setType(PayloadType.LOGIN_OK);
    }

    public  static  Payload getRegisterData(){
        return new Payload()
                .setType(PayloadType.REGISTER_OK);
    }
}
