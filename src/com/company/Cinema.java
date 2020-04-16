package com.company;

import java.sql.*;

public class Cinema {
    String url = "jdbc:mysql://localhost:3306/cinema?user=root";
    Connection connection;
    Statement statement;
    String sql = "";

    public int addCustomerAndGetId(String name) {
        int id = 0;
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            sql = "INSERT INTO customer (`name`) VALUES ('" + name + "')";
            statement.executeUpdate(sql);
            sql = "SELECT max(id) from customer";
            ResultSet rs = statement.executeQuery(sql);
            rs.next();
            id = rs.getInt(1);
            connection.close();
        } catch (SQLException ex) {
            ex.fillInStackTrace();
        }
        return id;
    }

    public String getCustomerName(int id) {
        String name = "";
        try {
            String url = "jdbc:mysql://localhost:3306/cinema?user=root";
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM customer WHERE id = " + id + "";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                name = rs.getString("name");
            }
            connection.close();
        } catch (SQLException ex) {
            ex.fillInStackTrace();
        }
        return name;
    }

    public void showProgram() {
        int number = 0;
        String title = "";
        try {
            String url = "jdbc:mysql://localhost:3306/cinema?user=root";
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            sql = "SELECT number, title from movie ";
            ResultSet rs = statement.executeQuery(sql);
            System.out.println("Programm:");
            while (rs.next()) {
                number = rs.getInt("number");
                title = rs.getString("title");
                System.out.println("Film " + number + ": " + title);
            }
            connection.close();
        } catch (SQLException ex) {
            ex.fillInStackTrace();
        }
    }

    public void showDetails(int filmNumber) {
        int number = 0;
        Time time = null;
        int timeId = 0;
        try {
            String url = "jdbc:mysql://localhost:3306/cinema?user=root";
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            sql = "SELECT projection.time, projection.id, cinema_hall_movie.nr_cinema from movie_projection " +
                    "INNER JOIN projection ON movie_projection.time = projection.id " +
                    "INNER JOIN cinema_hall_movie ON cinema_hall_movie.nr_movie = movie_projection.number " +
                    "WHERE movie_projection.number = " + filmNumber + "";
            ResultSet rs = statement.executeQuery(sql);
            System.out.println("Spielzeiten:");
            System.out.println("Geben Sie bei der Auswahl der Zeit die Zahl in der Klammer an.");
            while (rs.next()) {
                number = rs.getInt("nr_cinema");
                time = rs.getTime("time");
                timeId = rs.getInt("id");
                System.out.println("Saal " + number + ": " + time + " (" + timeId + ")");
            }
            connection.close();
        } catch (SQLException ex) {
            ex.fillInStackTrace();
        }
    }

    public void addOrder(int id, int filmNumber, int amount, int timeId) {
        boolean areTimeAndFilmNumberValid = checkTimeAndFilmNumberValidity(filmNumber, timeId);
        if (!areTimeAndFilmNumberValid) {
            System.out.println("Filmnummer oder Uhrzeit stimmen nicht.");
        } else {
            boolean areEnoughSeats = checkIfEnoughTickets(filmNumber, timeId, amount);
            if (areEnoughSeats) {
                try {
                    connection = DriverManager.getConnection(url);
                    statement = connection.createStatement();
                    sql = "INSERT INTO `reservation`(`amount`, `customer_id`, `movie_id`, `time_id`) " +
                            "VALUES (" + amount + "," + id + "," + filmNumber + "," + timeId + ")";
                    statement.executeUpdate(sql);
                    //subtract the ordered amount of seats
                    sql = "UPDATE `movie_projection` SET `seats`= (seats - " + amount + ") WHERE number = " + filmNumber +
                            " AND time = " + timeId + "";
                    statement.executeUpdate(sql);
                    connection.close();
                } catch (SQLException ex) {
                    ex.fillInStackTrace();
                }
            } else {
                System.out.println("Es gibt leider nicht mehr genug Plätze für diese Vorführung.");
            }
        }
    }

    public boolean checkTimeAndFilmNumberValidity(int filmNumber, int timeId) {
        boolean isInputValid = true;
        int number = 0;
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            sql = "SELECT seats from movie_projection WHERE number = " + filmNumber + " AND time = " + timeId + "";
            ResultSet rs = statement.executeQuery(sql);
            rs.next();
            number = rs.getInt(1);
            connection.close();
        } catch (SQLException ex) {
            ex.fillInStackTrace();
            isInputValid = false;
        }
        return isInputValid;
    }

    public boolean checkIfEnoughTickets(int filmNumber, int timeId, int amount) {
        int actualNumberOfSeats = 0;
        boolean areEnoughSeats = false;
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            sql = "SELECT seats from movie_projection WHERE number = " + filmNumber + " AND time = " + timeId + "";
            ResultSet rs = statement.executeQuery(sql);
            rs.next();
            actualNumberOfSeats = rs.getInt(1);
            connection.close();
        } catch (SQLException ex) {
            ex.fillInStackTrace();
        }
        if (actualNumberOfSeats >= amount) {
            areEnoughSeats = true;
        }
        return areEnoughSeats;
    }

    public void printReservation(int customerId) {
        int orderId;
        int amount;
        String title;
        Time time;
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            sql = "SELECT reservation.id, reservation.amount, movie.title, projection.time from reservation " +
                    "INNER JOIN movie ON reservation.movie_id = movie.number " +
                    "INNER JOIN projection ON reservation.time_id = projection.id " +
                    "WHERE customer_id = " + customerId + "";
            ResultSet rs = statement.executeQuery(sql);
            System.out.println("Ihre Bestellung(en):");
            while (rs.next()) {
                orderId = rs.getInt("id");
                amount = rs.getInt("amount");
                title = rs.getString("title");
                time = rs.getTime("time");
                System.out.println("Bestell-Nummer: " + orderId + " Anzahl Karten: " + amount + " Film: " + title + " Uhrzeit: " + time + "");
            }
            connection.close();
        } catch (SQLException ex) {
            ex.fillInStackTrace();
        }
    }

    public void deleteOrder(int customerId, int orderId) {
        int amount = 0;
        int filmNumber = 0;
        int timeId = 0;
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            sql = "SELECT movie_id, time_id, amount from reservation where id = " +
                    "" + orderId + " AND customer_id = " + customerId + "";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                filmNumber = rs.getInt("movie_id");
                timeId = rs.getInt("time_id");
                amount = rs.getInt("amount");
            }
            //add the amount of seats from the order we are going to delete to movie_projection
            sql = "UPDATE `movie_projection` SET `seats`= (seats + " + amount + ") WHERE number = " + filmNumber +
                    " AND time = " + timeId + "";
            statement.executeUpdate(sql);
            sql = "DELETE FROM `reservation` WHERE id = " + orderId + " AND customer_id = " + customerId + "";
            statement.executeUpdate(sql);
            connection.close();
        } catch (SQLException ex) {
            ex.fillInStackTrace();
            System.out.println("Diese Bestell-Nummer wurde nicht gefunden.");
        }
    }
}


