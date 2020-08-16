package ru.tsystems.school.dao.impl;

import org.springframework.stereotype.Repository;
import ru.tsystems.school.dao.AbstractJpaDao;
import ru.tsystems.school.dao.ScheduleDao;
import ru.tsystems.school.dao.StationDao;
import ru.tsystems.school.dao.TrainDao;
import ru.tsystems.school.model.Schedule;
import ru.tsystems.school.model.Station;
import ru.tsystems.school.model.Train;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StationDaoImpl extends AbstractJpaDao<Station> implements StationDao {

    private final ScheduleDao scheduleDao;
    private final TrainDao trainDao;

    public StationDaoImpl(ScheduleDao scheduleDao, TrainDao trainDao) {
        setClazz(Station.class);
        this.trainDao = trainDao;
        this.scheduleDao = scheduleDao;
    }

    @Override
    public List<Train> findAllTrainsForCurrentStation(int id) {

        List<Schedule> stationsForTrain =
                getEntityManager().createQuery(
                        "select s from Schedule s where s.station.id =:id", Schedule.class)
                        .setParameter("id", id)
                        .getResultList();

        return stationsForTrain.stream().map(Schedule::getTrain).collect(Collectors.toList());
    }

    @Override
    public List<Schedule> findScheduleForStation(int id) {

        return getEntityManager().createQuery(
                "select s from Schedule s where s.station.id=:id", Schedule.class
        ).setParameter("id", id).getResultList();

    }

    @Override
    public List<Schedule> findScheduleForStationAndTrain(int stationId, int trainId) {

        return getEntityManager()
                .createQuery(
                        "select s from Schedule s where s.station.id=:stationId and s.train.id=:trainId",
                        Schedule.class)
                .setParameter("stationId", stationId)
                .setParameter("trainId", trainId)
                .getResultList();

    }

    @Override
    public void addSchedule(int stationId, int trainId, LocalTime arrivalTime) {

        try {

            Station stationFromDb = findById(stationId);
            if (stationFromDb == null) {
                return;
            }

            Train trainFromDb = trainDao.findById(trainId);
            if (trainFromDb == null) {
                return;
            }

            Schedule schedule = new Schedule(trainFromDb, arrivalTime, stationFromDb);

            scheduleDao.save(schedule);

        } catch (Exception ex) {

        }
    }

    @Override
    public List<Train> findSuitableTrains(Station from, Station to, String fromTime, String toTime) {

        List<Train> allTrains = trainDao.findAll();

        List<Train> findTrains = new ArrayList<>();

        for (Train train : allTrains) {
            if (train.getSeatsAmount() > 0) { // no free places
                List<Schedule> schedules =
                        getEntityManager().createQuery(
                                "select s from Schedule s where s.train.id =:id order by s.departureTime",
                                Schedule.class)
                                .setParameter("id", train.getId())
                                .getResultList();

                List<Station> trainRoute = schedules.stream().map(Schedule::getStation).collect(Collectors.toList());

                LocalTime now = LocalTime.now();
                if (trainRoute.contains(from) && trainRoute.contains(to))
                    if (trainRoute.indexOf(from) < trainRoute.indexOf(to))
                        for (Schedule schedule : schedules) {
                            if (schedule.getStation().getId() == from.getId()) {
                                if (schedule.getDepartureTime().minusHours(3).isAfter(LocalTime.parse(fromTime))
                                        && schedule.getDepartureTime().minusHours(3).isBefore(LocalTime.parse(toTime))) {
                                    if (Math.abs(ChronoUnit.MINUTES.between(schedule.getDepartureTime().minusHours(3), now)) > 10)
                                        findTrains.add(train);
                                }
                            }
                        }
            }
        }
        return findTrains;
    }

    @Override
    public Station findByStationName(String name) {

        return getEntityManager()
                .createQuery("select t from Station t where t.name= :name", Station.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    @Override
    public void saveSchedule(Schedule schedule) {
        scheduleDao.save(schedule);
    }

    @Override
    public void deleteById(int id) {

        List<Schedule> schedules = scheduleDao.findSchedulesByStationId(id);
        for (Schedule schedule : schedules) {
            getEntityManager().remove(schedule);
        }

        Station station = findById(id);
        getEntityManager().remove(station);

    }
}