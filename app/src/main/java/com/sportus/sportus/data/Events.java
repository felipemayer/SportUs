package com.sportus.sportus.data;


import com.sportus.sportus.R;

public class Events {
    public static int[] eventIds = new int[]{1, 2, 3};
    public static String[] eventNames = new String[]{"Volei da Paulinha","Funcional no Ibira","Futebol das meninas"};
    public static String[] eventTypes = new String[]{"Volei","Funcional","Futebol"};
    public static String[] eventLevels = new String[]{"Iniciante","Avançado","Intermediário"};
    public static String[] eventAddress = new String[]{"Av. Paulista, 432", "Ibirapuera","Morumbi"};
    public static Double[] eventLatitude = new Double[]{-23.5864667, -23.5870111, -23.5888411};
    public static Double[] eventLongitude = new Double[]{-46.6431481, -46.6595177, -46.6545777};
    public static String[] eventDate = new String[]{"08/07/2016","18/07/2016","10/07/2016"};
    public static String[] eventTime = new String[]{"19h30","06h40","10h00"};
    public static int[] resourceIds = new int[]{R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
    public static String[] paymentRules = new String[]{"$","$",""};

    public static int[] getEventIds() {
        return eventIds;
    }

    public static void setEventIds(int[] eventIds) {
        Events.eventIds = eventIds;
    }

    public static String[] getEventNames() {
        return eventNames;
    }

    public static void setEventNames(String[] eventNames) {
        Events.eventNames = eventNames;
    }

    public static String[] getEventTypes() {
        return eventTypes;
    }

    public static void setEventTypes(String[] eventTypes) {
        Events.eventTypes = eventTypes;
    }

    public static String[] getEventLevels() {
        return eventLevels;
    }

    public static void setEventLevels(String[] eventLevels) {
        Events.eventLevels = eventLevels;
    }

    public static String[] getEventAddress() {
        return eventAddress;
    }

    public static void setEventAddress(String[] eventAddress) {
        Events.eventAddress = eventAddress;
    }

    public static Double[] getEventLatitude() {
        return eventLatitude;
    }

    public static void setEventLatitude(Double[] eventLatitude) {
        Events.eventLatitude = eventLatitude;
    }

    public static Double[] getEventLongitude() {
        return eventLongitude;
    }

    public static void setEventLongitude(Double[] eventLongitude) {
        Events.eventLongitude = eventLongitude;
    }

    public static String[] getEventDate() {
        return eventDate;
    }

    public static void setEventDate(String[] eventDate) {
        Events.eventDate = eventDate;
    }

    public static String[] getEventTime() {
        return eventTime;
    }

    public static void setEventTime(String[] eventTime) {
        Events.eventTime = eventTime;
    }

    public static int[] getResourceIds() {
        return resourceIds;
    }

    public static void setResourceIds(int[] resourceIds) {
        Events.resourceIds = resourceIds;
    }

    public static String[] getPaymentRules() {
        return paymentRules;
    }

    public static void setPaymentRules(String[] paymentRules) {
        Events.paymentRules = paymentRules;
    }


}
