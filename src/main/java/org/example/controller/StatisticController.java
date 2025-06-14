package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.SecurityUtils;
import org.example.model.Reservation;
import org.example.model.Subordination;
import org.example.model.Worker;
import org.example.repository.ReservationRepository;
import org.example.repository.SubordinationRepository;
import org.example.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.sql.Time;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistic")
public class StatisticController {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private SubordinationRepository subordinationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private TreeMap<DayOfWeek, Long> reservationDayStat(){
        Long all = (long) reservationRepository.findAll().size();

        TreeMap<DayOfWeek, Long> reservationSumByWeekDay = reservationRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                      r -> r.getDate().getDayOfWeek(),
                      TreeMap::new,
                      Collectors.counting()));

        TreeMap<DayOfWeek, Long> reservationPercentByWeekDay = reservationSumByWeekDay.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (long) (entry.getValue()  * 100.0 / all),
                        (existing, replacement) -> existing,
                        TreeMap::new));

        return reservationPercentByWeekDay;
    }

    private TreeMap<Integer, Long> reservationTimeStat(){
        Long all = (long) reservationRepository.findAll().size();

        TreeMap<Integer, Long> reservationSumByTime = reservationRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> r.getTime().getHour(),
                        TreeMap::new,
                        Collectors.counting()));

        TreeMap<Integer, Long> reservationPercentByTime = reservationSumByTime.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (long) (entry.getValue()  * 100.0 / all),
                        (existing, replacement) -> existing,
                        TreeMap::new));

        return reservationPercentByTime;
    }

    private String meanGuest(){
        Double all = (double) reservationRepository.findAll().size();

        OptionalDouble sumGuest = reservationRepository.findAll().stream()
                .mapToDouble(r -> r.getAmountOfPeople()).reduce((x,y) -> x+y);

        Double meanGuest = sumGuest.getAsDouble() / all;

        DecimalFormat df = new DecimalFormat("#.##");

        return df.format(meanGuest);
    }

    private int totalReserv(){
        return reservationRepository.findAll().size();
    }

    private int totalManager(){
        return workerRepository.findAllByRole("MANAGER").size();
    }

    private int subordination(String username){
        return workerRepository.findByUsername(username).get().getSubordinates().size();
    }

    @GetMapping("")
    public String statistic(Model model, Principal principal) {
        if (principal != null && SecurityUtils.hasRole(principal, "ADMIN")) {
            TreeMap<DayOfWeek, Long> Daystats = reservationDayStat();
            TreeMap<Integer, Long> Timestats = reservationTimeStat();

            // Преобразуем в JSON
            ObjectMapper mapper = new ObjectMapper();
            try {
                model.addAttribute("dataLabels", mapper.writeValueAsString(new ArrayList<>(Daystats.keySet()
                        .stream()
                        .map(d -> d.toString())
                        .toList())));
                model.addAttribute("dataValues", mapper.writeValueAsString(new ArrayList<>(Daystats.values())));


                model.addAttribute("timeLabels", mapper.writeValueAsString(new ArrayList<>(Timestats.keySet()
                        .stream()
                        .map(d -> d.toString())
                        .toList())));
                model.addAttribute("timeValues", mapper.writeValueAsString(new ArrayList<>(Timestats.values())));


                model.addAttribute("meanGuest", meanGuest());
                model.addAttribute("totalReserv", totalReserv());
                model.addAttribute("totalManager", totalManager());
                model.addAttribute("subordination", subordination(principal.getName()));

                model.addAttribute("page", "statistic");

            } catch (JsonProcessingException e) {
                model.addAttribute("error", "Error processing data");
            }

            return "statisticAdmin";
        }
        return "welcome";
    }

    @GetMapping("/managers")
    public String managers(Model model, Principal principal) {
        if (principal != null && SecurityUtils.hasRole(principal, "ADMIN")) {
            List<Worker> subordinations =  subordinationRepository.findAllSubordinationByWorker(
                            workerRepository.findByUsername(principal.getName()).get());

            model.addAttribute("subordinations", subordinations);
            return "subordinations";
        }return "welcome";
    }

    @PostMapping("/managers/{id}/makeadmin")
    public String makeAdmin(@PathVariable Long id, Principal principal, Model model) {
        if (principal != null && SecurityUtils.hasRole(principal, "ADMIN")) {
            Worker worker = workerRepository.findById(id).get();
            worker.setRole("ADMIN");

            Worker admin = workerRepository.findByUsername(principal.getName()).get();

            Long subId = subordinationRepository.findSubordinationByManager( admin, worker);

            worker.setSupervisor(null);
            admin.getSubordinates().removeIf(s -> s.getId().equals(subId));

            subordinationRepository.deleteById(subId);

            List<Worker> subordinations =  subordinationRepository.findAllSubordinationByWorker(
                    workerRepository.findByUsername(principal.getName()).get());


            model.addAttribute("subordinations", subordinations);

            return "subordinations";
        }return "welcome";
    }

    @PostMapping("/managers/{id}/delete")
    public String deleteWorker(@PathVariable Long id, Principal principal) {
        if (principal != null && SecurityUtils.hasRole(principal, "ADMIN")) {
            workerRepository.deleteById(id);

            return "subordinations";
        }return "welcome";
    }
}
