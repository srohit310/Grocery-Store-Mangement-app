package com.stancorp.grocerystorev1.Classes;

import javax.annotation.Nullable;

public class Agent {
    public String Code;
    public long codeno;
    public String Name;
    public String AgentType;
    public String CustomerType;
    public String Email;
    public long Phoneno;
    public DeliveryAddress deladdress;
    public AdditionalContact altcontact;
    public Boolean valid;

    public Agent() { }

    public Agent(String Name, String AgentType, String CustomerType, String Email, long phoneno, DeliveryAddress address
            , AdditionalContact contact,Boolean valid) {
        this.Name = Name;
        this.AgentType = AgentType;
        this.CustomerType = CustomerType;
        this.Email = Email;
        this.Phoneno = phoneno;
        this.deladdress = address;
        this.altcontact = contact;
        this.valid = valid;
    }
}
