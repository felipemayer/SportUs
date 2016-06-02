package com.sportus.sportus.data;


import com.sportus.sportus.R;

public class Events {
    public static int[] eventIds = new int[]{1, 2, 3, 4};
    public static String[] eventNames = new String[]{"Futebol da Maria", "Funcional na Augusta", "Basquete da ACM", "Corrida das Meninas"};
    public static String[] eventTypes = new String[]{"Futebol", "Funcional", "Basquete", "Corrida"};
    public static String[] eventLevels = new String[]{"Iniciante", "Avançado", "Intermediário", "AVançado"};
    public static String[] eventAddress = new String[]{"Mackenzie", "Parque da Augusta", "ACM - Nestor Pestana", "Praça Buenos Aires"};
    public static Double[] eventLatitude = new Double[]{-23.5475007, -23.5501511, -23.5484201, -23.5454891};
    public static Double[] eventLongitude = new Double[]{-46.6520635, -46.6509477, -46.6473857, -46.6599707};
    public static String[] eventDate = new String[]{"08/06/2016", "18/06/2016", "10/06/2016", "31/05/2016"};
    public static String[] eventTime = new String[]{"19:30", "06:40", "10:00", "07:00"};
    public static int[] eventIcon = new int[]{R.drawable.ic_ball, R.drawable.ic_ball, R.drawable.ic_ball, R.drawable.ic_ball};
    public static boolean[] eventPayMethod = new boolean[]{true, true, false, true};
    public static String[] eventCost = new String[]{"R$ 30", "R$ 100", "-", "R$ 5"};

    public static boolean[] getEventPayMethod() {
        return eventPayMethod;
    }

    public static void setEventPayMethod(boolean[] eventPayMethod) {
        Events.eventPayMethod = eventPayMethod;
    }

    public static String[] getEventCost() {
        return eventCost;
    }

    public static void setEventCost(String[] eventCost) {
        Events.eventCost = eventCost;
    }




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

    public static int[] getEventIcon() {
        return eventIcon;
    }

    public static void setEventIcon(int[] eventIcon) {
        Events.eventIcon = eventIcon;
    }


}
