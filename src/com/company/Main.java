package com.company;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Scanner numberScanner;
        Cinema cinema = new Cinema();
        String choice = "";
        boolean registrationIsInProgress = true;
        boolean wantsToSeeDetails = true;
        boolean isOrderInProgress = true;
        boolean deleteInProgress = true;
        int customerId = 0;
        int orderId = 0;
        int filmNumber = 0;
        int amountTickets = 0;
        int timeId = 0;
        String name = "";

        while(registrationIsInProgress) {
            System.out.println("Melden Sie sich neu an (1) \noder loggen Sie sich ein (2)");
            choice = scan.nextLine();

            if (choice.equalsIgnoreCase("1")) {
                System.out.println("Wie lautet Ihr Name?");
                name = scan.nextLine();
                customerId = cinema.addCustomerAndGetId(name);
                System.out.println("Ihre Kunden_ID: " + customerId);
                registrationIsInProgress = false;
            } else if (choice.equalsIgnoreCase("2")) {
                System.out.println("Geben Sie Ihre Kunden_ID ein:");
                try {
                    numberScanner = new Scanner(System.in);
                    customerId = numberScanner.nextInt();
                }
                catch (InputMismatchException ex){
                    System.out.println("Die Kunden_ID muss eine Zahl sein.");
                    continue;
                }
                name = cinema.getCustomerName(customerId);
                if (name == "") {
                    System.out.println("Kundenkonto wurde nicht gefunden.");
                    continue;
                } else {
                    System.out.println("Die Anmeldung war erfolgreich, "+name+".");
                    registrationIsInProgress = false;
                }
            } else {
                System.out.println("Versuchen Sie es noch einmal.");
            }
        }
        System.out.println("Möchten Sie das Programm ansehen (1) oder eine Bestellung stornieren (2)?");
        choice = scan.nextLine();

        if (choice.equalsIgnoreCase("1")) {
            cinema.showProgram();
            while (wantsToSeeDetails) {
                System.out.println("Geben Sie die Filmnummer ein, um sich die Spielzeiten anzusehen.");
                System.out.println("Mit 0 kommen Sie einen Schritt weiter.");
                try {
                    numberScanner = new Scanner(System.in);
                    filmNumber = numberScanner.nextInt();
                    if (filmNumber == 0) {
                        wantsToSeeDetails = false;
                    } else {
                        cinema.showDetails(filmNumber);
                    }
                } catch (InputMismatchException ex) {
                    System.out.println("Falsche Eingabe.");
                    continue;
                }
            }
            while (isOrderInProgress) {
                System.out.println("Für welchen Film möchten Sie Karten?");
                System.out.println("Sie beenden den Bestellvorgang mit 0");
                try {
                    numberScanner = new Scanner(System.in);
                    filmNumber = numberScanner.nextInt();
                    if (filmNumber == 0) {
                        break;
                    } else {
                        System.out.println("Um welche Uhrzeit möchten Sie den Film ansehen?");
                        timeId = numberScanner.nextInt();
                    }
                    if (timeId == 0) {
                        break;
                    } else {
                        System.out.println("Wie viele Karten möchten Sie bestellen?");
                        amountTickets = numberScanner.nextInt();
                    }
                    if (amountTickets == 0) {
                        isOrderInProgress = false;
                    } else {
                        cinema.addOrder(customerId, filmNumber, amountTickets, timeId);
                    }
                } catch (InputMismatchException ex) {
                    System.out.println("Falsche Eingabe.");
                    continue;
                }
            }
            cinema.printReservation(customerId);
        }
        else if (choice.equalsIgnoreCase("2")){

            while(deleteInProgress) {
                System.out.println("Geben Sie die Bestell-Nummer der Tickets an, die Sie stornieren möchten.");
                System.out.println("Sie können den Vorgang mit 0 beenden");
                try {
                    numberScanner = new Scanner(System.in);
                    orderId = numberScanner.nextInt();
                    if (orderId == 0) {
                        deleteInProgress = false;
                    } else {
                        cinema.deleteOrder(customerId,orderId);
                    }
                } catch (InputMismatchException ex) {
                    System.out.println("Falsche Eingabe.");
                    continue;
                }
                cinema.printReservation(customerId);
            }
        }
    }
}
