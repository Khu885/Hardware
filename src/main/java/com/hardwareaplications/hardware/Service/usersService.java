package com.hardwareaplications.hardware.Service;

import com.hardwareaplications.hardware.AdmUsers;
import com.hardwareaplications.hardware.HardwaresRepo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class usersService {

    @Autowired
    private UserRepo urepo;

    public AdmUsers saveuser(AdmUsers user) {
        return urepo.save(user);
    }

    // List<user> users = new ArrayList<>((Arrays.asList(new
    // user(1,"Akif","Akif123","akif@gmail.com","Admin"),
    // new user(2,"Arshad","aaa123","ars@gmail.com","Admin"))));
    //
    // public List<user> getusers() {return users ;}
    //
    // public user getuserbyID(int uId) {
    // return users.stream()
    // .filter(u -> u.getUId() == uId)
    // .findFirst().orElse(null);
    // }
    //
    // public user addusers(user u) {
    // users.add(u);
    // return u;
    // }
    //
    // public user updateusers(user u) {
    // for (int i = 0; i < users.size(); i++) {
    // if (users.get(i).getUId() == u.getUId()) {
    // users.set(i, u);
    // return u;
    // }
    // }
    // return null;
    // }
    //
    // public List<user> deleteusers(int uId) {
    // int index = -1;
    // for (int i=0;i<users.size();i++){
    // if(users.get(i).getUId()== uId){
    // index=i;
    // break;
    // }
    // }
    // if (index != -1) {
    // users.remove(index);
    // }
    // return users;
    // }
}
