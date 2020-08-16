package ru.tsystems.school.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tsystems.school.dto.PassengerDto;
import ru.tsystems.school.dto.StationDto;
import ru.tsystems.school.dto.TicketDto;
import ru.tsystems.school.dto.TrainDto;
import ru.tsystems.school.model.Passenger;
import ru.tsystems.school.service.PassengerService;
import ru.tsystems.school.service.StationService;
import ru.tsystems.school.service.TicketService;
import ru.tsystems.school.service.TrainService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class GeneralController {

    private final StationService stationService;
    private final TrainService trainService;
    private final TicketService ticketService;
    private final PassengerService passengerService;

    @GetMapping("account")
    public String showAccountInfo(Model model) {

        List<TicketDto> tickets = ticketService.getTicketsForAuthorizedUser();
        PassengerDto user = passengerService.getAuthorizedPassenger();
        model.addAttribute("user", user);
        model.addAttribute("tickets", tickets);
        return "account";
    }

    @GetMapping
    public String hello() {
        return "home";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/buy_ticket")
    public String buyTicket(Model model) {

        List<StationDto> allDtoStations = stationService.findAllStations();
        model.addAttribute("stations", allDtoStations);
        return "buy_ticket";

    }

    @GetMapping("/suitableTrainList")
    public String getSuitableTrainList(@RequestParam String fromStation,
                                       @RequestParam String toStation,
                                       @RequestParam String fromTime,
                                       @RequestParam String toTime, Model model) {

        PassengerDto passenger = passengerService.getAuthorizedPassenger();
        StationDto from = stationService.findByStationName(fromStation);
        StationDto to = stationService.findByStationName(toStation);
        List<TrainDto> trains = stationService.findSuitableTrains(from, to, fromTime, toTime);

        List<TicketDto> tickets;

        Map<Integer, Boolean> ticketsPassenger = new HashMap<>();

        for (TrainDto train : trains) {
            tickets = ticketService.findTicketsByTrainId(train.getId());

            for (TicketDto ticket : tickets) {
                if (ticket.getPassenger().getId() == passenger.getId())
                    ticketsPassenger.put(train.getId(), true);
            }
        }

        model.addAttribute("ticketsPassenger", ticketsPassenger);
        model.addAttribute("trains", trains);
        model.addAttribute("fromStation", fromStation);

        return "trainsList";
    }

    @GetMapping("deleteTicket/{id}")
    public String deleteTicket(@PathVariable int id) {

        TicketDto ticket = ticketService.findById(id);
        TrainDto train = trainService.findTrainById(ticket.getTrain().getId());
        train.setSeatsAmount(train.getSeatsAmount() + 1);
        trainService.update(train);
        ticketService.deleteById(id);
        return "redirect:/";

    }
}