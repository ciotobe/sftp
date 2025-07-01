package jp.co.jri.epix.sftp.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "epix_m_api_access")
public class ApiAccess {
    @Id
    private String application;

    private String component;
    private String apiKey;
    private Date expiryDate;
    private Character neverExpired;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Character getNeverExpired() {
        return neverExpired;
    }

    public void setNeverExpired(Character neverExpired) {
        this.neverExpired = neverExpired;
    }
}
