package gmsproduction.com.pushpush.Models;


public class UsersModel {

    String id,name,image;
    public UsersModel(){}

    public UsersModel(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
