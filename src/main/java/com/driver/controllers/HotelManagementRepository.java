package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;

import java.util.*;

public class HotelManagementRepository {
    private final Map<String,Hotel> hotelDb = new HashMap<>();
    private final Map<Integer, User>userDb = new HashMap<>();
    private final Map<String, List<Booking>> bookingsByHotel = new HashMap<>();
    private final Map<Integer, List<Booking>> bookingsByAadhar = new HashMap<>();
    public String addHotel(Hotel hotel) {
        if(hotel == null || hotel.getHotelName() == null){
            return "FAILURE";
        }
        if(hotelDb.containsKey(hotel.getHotelName())){
            return "FAILURE";
        }
        hotelDb.put(hotel.getHotelName(),hotel);
        bookingsByHotel.put(hotel.getHotelName(),new ArrayList<>());
        return "SUCCESS";
    }

    public Integer addUser(User user) {
        if(user != null){
            userDb.put(user.getaadharCardNo(),user);
            return user.getaadharCardNo();
        }
        return null;
    }

    public String getHotelWithMostFacilities() {
        String result = "";
        int maxFacilities = -1;

        for(Map.Entry<String, Hotel> entry : hotelDb.entrySet()){
            List<Facility> facilities = entry.getValue().getFacilities();
            int numFacilities = facilities.size();

            if(numFacilities > maxFacilities || (numFacilities == maxFacilities && entry.getKey().compareTo(result) < 0)){
                result = entry.getKey();
                maxFacilities = numFacilities;
            }
        }
        return maxFacilities > 0 ? result : "";
    }

    public int bookARoom(Booking booking) {
        if (booking == null) {
            return -1;
        }

        Hotel hotel = hotelDb.get(booking.getHotelName());
        if (hotel == null || hotel.getAvailableRooms() < booking.getNoOfRooms()) {
            return -1;
        }

        String bookingId = UUID.randomUUID().toString();
        booking.setBookingId(bookingId);
        int totalAmount = booking.getNoOfRooms() * hotel.getPricePerNight();
        booking.setAmountToBePaid(totalAmount);

        List<Booking> hotelBookings = bookingsByHotel.get(booking.getHotelName());
        hotelBookings.add(booking);
        bookingsByAadhar.computeIfAbsent(booking.getBookingAadharCard(), k -> new ArrayList<>()).add(booking);

        return totalAmount;
    }

    public List<Booking> getBookings(Integer aadharCard) {
        List<Booking> bookings = new ArrayList<>();

        for (List<Booking> userBookings : bookingsByAadhar.values()) {
            for (Booking booking : userBookings) {
                if (booking.getBookingAadharCard() == aadharCard) {
                    bookings.add(booking);
                }
            }
        }

        return bookings;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
        Hotel hotel = hotelDb.get(hotelName);
        if (hotel == null || newFacilities == null) {
            return null;
        }

        List<Facility> currentFacilities = hotel.getFacilities();
        for (Facility facility : newFacilities) {
            if (!currentFacilities.contains(facility)) {
                currentFacilities.add(facility);
            }
        }

        return hotel;
    }

    public Map<String, Hotel> getHotelDb() {
        return hotelDb;
    }

    public Map<Integer, User> getUserDb() {
        return userDb;
    }

    public Map<String, List<Booking>> getBookingsByHotel() {
        return bookingsByHotel;
    }

    public Map<Integer, List<Booking>> getBookingsByAadhar() {
        return bookingsByAadhar;
    }
}
