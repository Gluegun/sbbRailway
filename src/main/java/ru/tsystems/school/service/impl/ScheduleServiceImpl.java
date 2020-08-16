package ru.tsystems.school.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsystems.school.dao.ScheduleDao;
import ru.tsystems.school.dto.ScheduleDto;
import ru.tsystems.school.mapper.ScheduleMapper;
import ru.tsystems.school.mapper.StationMapper;
import ru.tsystems.school.mapper.TrainMapper;
import ru.tsystems.school.model.Schedule;
import ru.tsystems.school.service.ScheduleService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleDao scheduleDao;
    private final ScheduleMapper scheduleMapper;
    private final TrainMapper trainMapper;
    private final StationMapper stationMapper;


    @Override
    public List<ScheduleDto> findAll() {
        return scheduleDao.findAll().stream().map(scheduleMapper::toDto).collect(Collectors.toList());

    }

    @Override
    public List<ScheduleDto> findSchedulesDtoByTrainId(int id) {

        List<ScheduleDto> schedulesDto = new ArrayList<>();

        List<Schedule> schedulesByTrainId = scheduleDao.findSchedulesByTrainId(id);
        for (Schedule schedule : schedulesByTrainId) {
            schedulesDto.add(
                    new ScheduleDto(
                            trainMapper.toDto(schedule.getTrain()),
                            schedule.getDepartureTime(),
                            stationMapper.toDto(schedule.getStation())
                    )
            );
        }

        return schedulesDto;
    }

    @Override
    public void deleteById(int id) {
        scheduleDao.deleteById(id);
    }
}