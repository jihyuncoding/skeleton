package model;

public class UserTravelVO {
    private int no;
    private String district;
    private String title;
    private String description;
    private String address;
    private String phone;

    public UserTravelVO(int no, String district, String title, String description, String address, String phone) {
        this.no = no;
        this.district = district;
        this.title = title;
        this.description = description;
        this.address = address;
        this.phone = phone;
    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s", no, title, district, address);
    }
}
