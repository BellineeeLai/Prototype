package tarc.edu.prototype.Model;

@SuppressWarnings("unused")
public class User {
    private String name,email,password, staffId, phoneNo, preferences,incharge;

    public User() {
    }

    public User(String name, String email, String password, String preferences) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.preferences = preferences;
    }

    public User(String name, String password, String staffId, String phoneNo, String preferences, String incharge) {
        this.name = name;
        this.password = password;
        this.staffId = staffId;
        this.phoneNo = phoneNo;
        this.preferences = preferences;
        this.incharge = incharge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getIncharge() {
        return incharge;
    }

    public void setIncharge(String incharge) {
        this.incharge = incharge;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
