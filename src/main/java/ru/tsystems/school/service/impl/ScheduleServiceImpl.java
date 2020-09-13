package ru.tsystems.school.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsystems.school.dao.ScheduleDao;
import ru.tsystems.school.dao.StationDao;
import ru.tsystems.school.dao.TrainDao;
import ru.tsystems.school.dto.ScheduleDto;
import ru.tsystems.school.dto.ScheduleDtoRest;
import ru.tsystems.school.mapper.ScheduleMapper;
import ru.tsystems.school.model.Schedule;
import ru.tsystems.school.service.ScheduleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Log4j
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleDao scheduleDao;
    private final ScheduleMapper scheduleMapper;
    private final JmsTemplate jmsTemplate;
    private final TrainDao trainDao;
    private final StationDao stationDao;

    @Override
    public List<ScheduleDto> findAll() {
        return scheduleDao.findAll().stream().map(scheduleMapper::toDto).sorted().collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDto> findSchedulesDtoByTrainId(int id) {

        return scheduleDao.findSchedulesByTrainId(id)
                .stream()
                .map(scheduleMapper::toDto)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDtoRest> findScheduleByStationId(int stationId) {

        List<Schedule> schedulesByStationId = scheduleDao.findSchedulesByStationId(stationId);
        List<ScheduleDto> schedules = schedulesByStationId.stream()
                .map(scheduleMapper::toDto).sorted().collect(Collectors.toList());

        return schedules.stream().map(ScheduleDtoRest::new).collect(Collectors.toList());

    }

    @Override
    public void deleteById(int id) {
        Schedule byId = scheduleDao.findById(id);
        scheduleDao.deleteById(id);
        log.info("Schedule for " + byId.getStation().getName() + " was deleted");
        jmsTemplate.send(session -> session.createTextMessage("schedule deleted"));
    }

    @Override
    public void deleteTrainFromSchedule(int stationId, int trainId) {

        scheduleDao.deleteTrainFromSchedule(stationId, trainId);
        jmsTemplate.send(session -> session.createTextMessage("schedule was deleted"));

    }
}
