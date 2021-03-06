package ru.tsystems.school.model.mapper.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tsystems.school.model.dto.ScheduleDto;
import ru.tsystems.school.model.mapper.ScheduleMapper;
import ru.tsystems.school.model.mapper.StationMapper;
import ru.tsystems.school.model.mapper.TrainMapper;
import ru.tsystems.school.model.entity.Schedule;

@Component
@Transactional
@AllArgsConstructor
public class ScheduleMapperImpl implements ScheduleMapper {


    private final TrainMapper trainMapper;
    private final StationMapper stationMapper;

    @Override
    public ScheduleDto toDto(Schedule schedule) {

        ScheduleDto scheduleDto = new ScheduleDto();
        if (schedule != null) {
            scheduleDto.setId(schedule.getId());
            scheduleDto.setTrain(
                    trainMapper.toDto(schedule.getTrain())
            );
            scheduleDto.setDepartureTime(schedule.getDepartureTime());
            scheduleDto.setArrivalTime(schedule.getArrivalTime());
            scheduleDto.setStation(
                    stationMapper.toDto(schedule.getStation())
            );
        }
        return scheduleDto;
    }

    @Override
    public Schedule toEntity(ScheduleDto scheduleDto) {

        Schedule schedule = new Schedule();

        if (scheduleDto != null) {
            schedule.setId(scheduleDto.getId());
            schedule.setDepartureTime(scheduleDto.getDepartureTime());
            schedule.setArrivalTime(scheduleDto.getArrivalTime());
            schedule.setTrain(trainMapper.toEntity(scheduleDto.getTrain()));
            schedule.setStation(stationMapper.toEntity(scheduleDto.getStation()));
        }
        return schedule;
    }

}
