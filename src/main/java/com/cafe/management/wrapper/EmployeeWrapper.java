package com.cafe.management.wrapper;


import lombok.Data;

@Data
public class EmployeeWrapper {
    Integer id;

    String name;


    String status;


    public EmployeeWrapper() {


    }

    public EmployeeWrapper(Integer id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;


    }


    public EmployeeWrapper(Integer id, String name) {

        this.id = id;
        this.name = name;


    }

}


