package com.pinyougou.sellergoods.service.impl;

import util.DateUtils;

import java.util.Date;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        try {
            List<String> days = DateUtils.getDays("2019-05-29", "2019-06-05");
            for (String day : days) {
                System.out.println(day);
            }
            Date pastDate = DateUtils.getPastDate(7);
            System.out.println(pastDate);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}