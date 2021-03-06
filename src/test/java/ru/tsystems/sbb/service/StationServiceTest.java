package ru.tsystems.sbb.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import ru.tsystems.school.dao.StationDao;
import ru.tsystems.school.dao.TrainDao;
import ru.tsystems.school.model.dto.StationDto;
import ru.tsystems.school.model.entity.Station;
import ru.tsystems.school.model.mapper.ScheduleMapper;
import ru.tsystems.school.model.mapper.StationMapper;
import ru.tsystems.school.model.mapper.TrainMapper;
import ru.tsystems.school.service.impl.StationServiceImpl;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {


    @Mock
    private StationDao stationDao;
    @Mock
    private StationMapper stationMapper;
    @Mock
    private TrainMapper trainMapper;
    @Mock
    private ScheduleMapper scheduleMapper;
    @Mock
    private TrainDao trainDao;
    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private StationServiceImpl stationService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void updateStationTest() {

        Station station = new Station("stationName");

        StationDto stationDto = new StationDto("stationName");
        stationDto.setId(1);

        when(stationDao.findById(anyInt())).thenReturn(station);

        stationService.update(0, stationDto);

        verify(stationDao, times(1)).findById(eq(0));
        verifyNoInteractions(trainDao);
        verifyNoInteractions(trainMapper);
        verifyNoInteractions(scheduleMapper);

    }

    @Test
    public void findAllTrainsForCurrentStationTest() {


        given(stationDao.findAllTrainsForCurrentStation(anyInt())).willReturn(anyList());

    }


}
