package reservation.mealkit;

import reservation.CleaningReservation;

public class Mealkit {
    private int mealkitNum;     //밀키트 개수
    private String mealkitMenu; //밀키트 메뉴
    private int mealkitPrice;   //밀키트 가격
    private int mealkitWeeks;   //밀키트 몇주차

    public void setMealkitNum(int mealkitNum) {
        this.mealkitNum = mealkitNum;
    }
    public void setMealkitMenu(String mealkitMenu){
        this.mealkitMenu = mealkitMenu;
    }
    public void setMealkitPrice(int mealkitPrice){
        this.mealkitPrice = mealkitPrice;
    }
    public void setMealkitWeeks(int mealkitWeeks){
        this.mealkitWeeks = mealkitWeeks;
    }

    public int getMealkitNum(){
        return mealkitNum;
    }
    public String getMealkikMenu(){
        return mealkitMenu;
    }
    public int getMealkitPrice(){
        return mealkitPrice;
    }
    public int getMealkitWeeks(){
        return mealkitWeeks;
    }

    //밀키트 예약요청 함수
    public static void requestMealkit(String reserveType){
        if(reserveType=="장기"){

        }else{ // 1회
            this.setMealkitWeeks(1);
        }
        System.out.println("")
    }
}
