package com.example.hanan.nim_gp.ManageDevices;


public class Device
    {

    private String adreess;
    private DeviceType deviceType;
    private String name;
    private Boolean selected;

        public Device() {
        }

        public Device(String adreess, DeviceType deviceType, String name) {
        this.adreess = adreess;
        this.deviceType = deviceType;
        this.name = name;
        selected = true;
    }



    public String getName() {
        return name;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
            return adreess;
        }

    public DeviceType getType() {
            return deviceType;
        }

        public void setAddress(String adreess) {
            this.adreess = adreess;
        }

        public void setType(DeviceType deviceType) {
            this.deviceType = deviceType;
        }
    }


