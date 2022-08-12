package ru.netology.patient.service.medical;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;


public class MedicalServiceTest {
    MedicalServiceImpl medService;
    public static PatientInfoRepository patientInfoRepository;
    public static SendAlertService sendAlert;

    @BeforeAll
    public static void start(){
        patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("4ab2fe14-bf25-4e53-ac16-5003c7c407b1"))
                .thenReturn(
                        new PatientInfo(
                                "Иван",
                                "Петров",
                                LocalDate.of(1980, 11, 26),
                                new HealthInfo(
                                        new BigDecimal("36.65"),
                                        new BloodPressure(120, 80)
                                )
                        )
                );

        sendAlert = Mockito.mock(SendAlertService.class);
        Mockito.when(sendAlert.send("Warning, patient with id: 4ab2fe14-bf25-4e53-ac16-5003c7c407b1, need help")).thenReturn(null);
    }

    @BeforeEach
    public void init(){
        medService = new MedicalServiceImpl(patientInfoRepository, sendAlert);
    }

    @ParameterizedTest
    @MethodSource("sourceBlood")
    public void checkBloodPressureTest(String patientId, BloodPressure bloodPressure, int count) {
        medService.checkBloodPressure(patientId, bloodPressure);
        Mockito.verify(sendAlert, Mockito.times(count)).send(Mockito.anyString());
    }

    public static Stream<Arguments> sourceBlood() {
        return Stream.of(
                Arguments.of("4ab2fe14-bf25-4e53-ac16-5003c7c407b1", new BloodPressure(120, 80), 0),
                Arguments.of("4ab2fe14-bf25-4e53-ac16-5003c7c407b1", new BloodPressure(150, 100), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTemperature")
    public void checkTemperatureTest(String patientId, BigDecimal temperature, int count) {
        medService.checkTemperature(patientId, temperature);
        Mockito.verify(sendAlert, Mockito.times(count)).send(Mockito.anyString());
    }

    public static Stream<Arguments> sourceTemperature() {
        return Stream.of(
                Arguments.of("4ab2fe14-bf25-4e53-ac16-5003c7c407b1", new BigDecimal("37.65"), 0),
                Arguments.of("4ab2fe14-bf25-4e53-ac16-5003c7c407b1", new BigDecimal("40.65"), 1)
        );
    }

    @AfterEach
    public void finalized() {
        medService = null;
    }
}
