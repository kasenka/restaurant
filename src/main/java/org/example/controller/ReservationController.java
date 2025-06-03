package org.example.controller;

import org.example.config.SecurityUtils;
import org.example.model.Reservation;
import org.example.repository.ReservationRepository;
import org.example.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.example.config.SecurityUtils.hasRole;

@Controller
@RequestMapping("/reservation")
@SessionAttributes("page")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;


    private List<String> generateTimeSlots(String start, String end, int interval, LocalDate localDate) {
        List<String> timeSlots = new ArrayList<>();
        LocalTime time = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);

        List<LocalTime> thatDayReservation = reservationRepository.findAllByDate(Optional.ofNullable(localDate))
                .stream()
                .map(r -> r.getTime())
                .toList();

        while (!time.isAfter(endTime)) {
            LocalTime currentTime = time;
            if (thatDayReservation.stream()
                    .filter(t -> t.equals(currentTime))
                    .count() < 10){
                timeSlots.add(time.toString()); // Формат "HH:mm"
                time = time.plusMinutes(interval);
            }else{
                time = time.plusMinutes(60);
            }
        }
        return timeSlots;
    }


    @GetMapping("/check-date")
    @ResponseBody
    public List<String> generateValidTimeSlots(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();

        if (localDate.isEqual(today)) {
            int hours = LocalTime.now().getHour();
            int minutes = LocalTime.now().getMinute();
            int minMinutes = (minutes / 15 + 1) * 15;

            hours = (minMinutes == 60)? hours+1: hours;
            minMinutes = (minMinutes == 60)? 0: minMinutes;

            String minTime = String.format("%02d:%02d", hours, minMinutes);
            return generateTimeSlots(minTime, "22:00", 15, localDate);
        }
        return generateTimeSlots("08:00", "22:00", 15, localDate);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('MANAGER')")
    public String reservation(Model model, Principal principal){

        model.addAttribute("page", "reservation");

        if(principal != null && hasRole(principal, "MANAGER")){
            List<Reservation> reservations = reservationRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(Reservation::getDate))
                    .toList();

            List<Reservation> todayReservations = reservations.stream()
                    .filter(r -> r.getDate().equals(LocalDate.now())).toList();

            model.addAttribute("date", LocalDate.now());
            model.addAttribute("reservations", todayReservations);
            return "reservationManager";
        }
        model.addAttribute("timeSlots", generateTimeSlots("08:00", "22:00", 15, null));
        return "reservation";
    }

    @PostMapping("/manager/filter")
    public String filter(@RequestParam String date, Model model, Principal principal){
        if(principal != null && SecurityUtils.hasRole(principal, "MANAGER")) {
            List<Reservation> reservations = reservationRepository.findAll()
                    .stream()
                    .filter(r -> r.getDate().equals(LocalDate.parse(date)))
                    .sorted(Comparator.comparing(Reservation::getTime))
                    .toList();
            model.addAttribute("date", date);
            model.addAttribute("reservations", reservations);
            return "reservationManager";
        }return "reservation";
    }

    @PostMapping("")
    public String addReservation(@RequestParam String name,
                                 @RequestParam String phoneNumber,
                                 @RequestParam int amountOfPeople,
                                 @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date,
                                 @RequestParam @DateTimeFormat(pattern = "HH:mm")LocalTime time,
                                 Model model){

        String errorMessage;
        if (amountOfPeople < 1) {
            errorMessage = " Пожалуйста, выберите минимум 1 персону.";
            model.addAttribute("message", errorMessage);
            return "reservation";
        } else if (amountOfPeople > 15) {
            errorMessage = " Кажется, мы должны уделить Вам больше времени.\n" +
                    " Пожалуйста, позвоните нам для оформления брони.";
            model.addAttribute("message", errorMessage);
            return "reservation";
        } else if (phoneNumber.contains("_")) {
            errorMessage = "Введите корректный номер телефона";
            model.addAttribute("message", errorMessage);
            return "reservation";
        }


        Reservation reservation = new Reservation();

        reservation.setName(name);
        reservation.setPhoneNumber(phoneNumber);
        reservation.setAmountOfPeople(amountOfPeople);
        reservation.setDate(date);
        reservation.setTime(time);

        reservationRepository.save(reservation);
        errorMessage = "Ваша бронь успешно оформлена, мы перезвоним Вам в течении 15 минут.";
        model.addAttribute("message", errorMessage);
        return "reservation";
    }
}
