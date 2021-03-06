package ru.tsystems.school.service;

import ru.tsystems.school.model.dto.PassengerDto;
import ru.tsystems.school.model.dto.StationDto;
import ru.tsystems.school.model.dto.TrainDto;

import java.util.List;

public interface TrainService {

    List<TrainDto> findAllDtoTrains();

    TrainDto findTrainById(int id);

    void save(TrainDto trainDto);

    void deleteById(int id);

    List<StationDto> findAllStations(int id);

    List<PassengerDto> findAllPassengersForTrain(int trainId);

    void update(TrainDto trainDto);

    void addStationToTrain(String departureTime, String arrivalTime, String station, TrainDto trainDto, int id);

    List<StationDto> potentialStationsForTrain(int trainId);


}
