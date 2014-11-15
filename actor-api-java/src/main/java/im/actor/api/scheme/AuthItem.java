package im.actor.api.scheme;
import com.droidkit.bser.Bser;
import com.droidkit.bser.BserObject;
import com.droidkit.bser.BserValues;
import com.droidkit.bser.BserWriter;
import java.io.IOException;
import im.actor.api.parser.*;
import java.util.List;

public class AuthItem extends BserObject {

    private int id;
    private int authHolder;
    private int appId;
    private String appTitle;
    private String deviceTitle;
    private int authTime;
    private String authLocation;
    private Double latitude;
    private Double longitude;

    public AuthItem(int id, int authHolder, int appId, String appTitle, String deviceTitle, int authTime, String authLocation, Double latitude, Double longitude) {
        this.id = id;
        this.authHolder = authHolder;
        this.appId = appId;
        this.appTitle = appTitle;
        this.deviceTitle = deviceTitle;
        this.authTime = authTime;
        this.authLocation = authLocation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public AuthItem() {

    }

    public int getId() {
        return this.id;
    }

    public int getAuthHolder() {
        return this.authHolder;
    }

    public int getAppId() {
        return this.appId;
    }

    public String getAppTitle() {
        return this.appTitle;
    }

    public String getDeviceTitle() {
        return this.deviceTitle;
    }

    public int getAuthTime() {
        return this.authTime;
    }

    public String getAuthLocation() {
        return this.authLocation;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    @Override
    public void parse(BserValues values) throws IOException {
        this.id = values.getInt(1);
        this.authHolder = values.getInt(2);
        this.appId = values.getInt(3);
        this.appTitle = values.getString(4);
        this.deviceTitle = values.getString(5);
        this.authTime = values.getInt(6);
        this.authLocation = values.getString(7);
        this.latitude = values.optDouble(8);
        this.longitude = values.optDouble(9);
    }

    @Override
    public void serialize(BserWriter writer) throws IOException {
        writer.writeInt(1, this.id);
        writer.writeInt(2, this.authHolder);
        writer.writeInt(3, this.appId);
        if (this.appTitle == null) {
            throw new IOException();
        }
        writer.writeString(4, this.appTitle);
        if (this.deviceTitle == null) {
            throw new IOException();
        }
        writer.writeString(5, this.deviceTitle);
        writer.writeInt(6, this.authTime);
        if (this.authLocation == null) {
            throw new IOException();
        }
        writer.writeString(7, this.authLocation);
        if (this.latitude != null) {
            writer.writeDouble(8, this.latitude);
        }
        if (this.longitude != null) {
            writer.writeDouble(9, this.longitude);
        }
    }

}
