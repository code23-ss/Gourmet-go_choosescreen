package com.example.mainscreen;

public class Reservation {
    private String reservationId;
    private String restaurantId;
    private String restaurantName;
    private String reservationDate;
    private String reservationTime;
    private int reservationPeople;
    private String restaurantImage;

    // Firestore에서 객체를 생성할 때 필요함
    public Reservation() {}

    public Reservation(String reservationId, String restaurantId, String restaurantName, String reservationDate, String reservationTime, int reservationPeople, String restaurantImage) {
        this.reservationId = reservationId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.reservationPeople = reservationPeople;
        this.restaurantImage = restaurantImage;
    }

    // 필요한 새로운 생성자 추가
    public Reservation(int reservationPeople, String reservationDate, String reservationTime, String restaurantId) {
        this.reservationPeople = reservationPeople; // String to int 변환
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.restaurantId = restaurantId;
    }


    public String getReservationId() {
        return reservationId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public int getReservationPeople() {
        return reservationPeople;
    }

    public String getRestaurantImage() {
        return restaurantImage;
    }
}
