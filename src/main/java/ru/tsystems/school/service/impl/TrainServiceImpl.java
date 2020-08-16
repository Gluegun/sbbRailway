package ru.tsystems.school.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tsystems.school.dao.TrainDao;
import ru.tsystems.school.dto.*;
import ru.tsystems.school.exceptions.CantDeleteException;
import ru.tsystems.school.mapper.PassengerMapper;
import ru.tsystems.school.mapper.StationMapper;
import ru.tsystems.school.mapper.TrainMapper;
import ru.tsystems.school.model.Train;
import ru.tsystems.school.service.PassengerService;
import ru.tsystems.school.service.StationService;
import ru.tsystems.school.service.TicketService;
import ru.tsystems.school.service.TrainService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Log4j
public class TrainServiceImpl implements TrainService {

    private final TrainDao trainDao;

    private final PassengerMapper passengerMapper;
    private final StationMapper stationMapper;
    private final TrainMapper trainMapper;

    private final PassengerService passengerService;
    private final StationService stationService;

    @Override
    public List<TrainDto> findAllDtoTrains() {

        return trainDao.findAll()
                .stream()
                .map(this::convertTrainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TrainDto findTrainById(int id) {
        return trainMapper.toDto(trainDao.findById(id));
    }

    @Override
    public void save(TrainDto trainDto) {

        Train train = convertTrainToEntity(trainDto);
        trainDao.save(train);
    }

    @Override
    public void deleteById(int id) {

        TrainDto train = findTrainById(id);
        if (train.getPassengers() != null) {
            if (train.getPassengers().size() > 0) {
                throw new CantDeleteException("Train is not empty");
            }
        }
        trainDao.deleteById(id);
    }

    @Override
    public List<StationDto> findAllStations(int id) {
        return trainDao.findAllStations(id)
                .stream()
                .map(stationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PassengerDto> findAllPassengers(int id) {
        return trainDao.findAllPassengers(id)
                .stream()
                .map(passengerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainDto> findTrainDtoByNumber(String number) {

        if (number == null) {
            return null;
        }

        return trainDao.findByTrainNumber(number)
                .stream()
                .map(this::convertTrainToDto)
                .collect(Collectors.toList());

    }

    @Override
    public void update(TrainDto trainDto) {

        PassengerDto authorizedPassenger = passengerService.getAuthorizedPassenger();
        log.info("user " + authorizedPassenger.getUsername() + " changed train:\n " +
                "train has: " + trainDto.getTrainNumber() + " number before");
        Train train = trainDao.findById(trainDto.getId());
        train.setSeatsAmount(trainDto.getSeatsAmount());
        train.setTrainNumber(trainDto.getTrainNumber());
        trainDao.update(train);
        log.info("now it has: " + train.getTrainNumber());

    }

    private TrainDto convertTrainToDto(Train train) {
        return trainMapper.toDto(train);
    }

    private Train convertTrainToEntity(TrainDto trainDto) {
        return trainMapper.toEntity(trainDto);
    }

    @Override
    public void addStationToTrain(String departureTime, String station, TrainDto trainDto, int id) {

        StationDto stationFoundByName = stationService.findByStationName(station);
        ScheduleDto scheduleDto = new ScheduleDto();
        LocalTime departureTimeLocalTime = LocalTime.parse(departureTime);
        scheduleDto.setDepartureTime(departureTimeLocalTime);
        scheduleDto.setTrain(trainDto);
        scheduleDto.setStation(stationFoundByName);
        stationService.saveSchedule(scheduleDto);

    }

    @Override
    public List<StationDto> potentialStationsForTrain(int trainId) {

        List<StationDto> allStations = stationService.findAllStations();

        List<StationDto> allStationsForTrain = findAllStations(trainId);

        List<StationDto> resultStationDtoForTrain = new ArrayList<>();

        for (StationDto stationDto : allStations) {
            boolean temp1 = false;
            for (StationDto stationForTrain : allStationsForTrain) {
                if (stationDto.getId() == stationForTrain.getId())
                    temp1 = true;
            }
            if (!temp1)
                resultStationDtoForTrain.add(stationDto);
        }


        return resultStationDtoForTrain;
    }
}