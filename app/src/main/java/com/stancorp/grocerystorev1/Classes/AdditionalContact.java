package com.stancorp.grocerystorev1.Classes;

public class AdditionalContact {
    public String Name;
    public String Email;
    public long Phoneno;
    public String Designation;
    public String Department;

    public AdditionalContact(){}

    public AdditionalContact(String name, String email, long Phoneno,String Designation,String Department){
        this.Name = name;
        this.Email = email;
        this.Phoneno = Phoneno;
        this.Designation = Designation;
        this.Department = Department;
    }
}
