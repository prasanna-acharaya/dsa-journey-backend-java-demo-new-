package com.bom.dsa.service;

import com.bom.dsa.dto.request.DsaRequestDto;
import com.bom.dsa.dto.response.DsaResponseDto;
import com.bom.dsa.entity.Dsa;
import com.bom.dsa.enums.DsaStatus;
import com.bom.dsa.repository.DsaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DsaServiceTest {

    @Mock
    private DsaRepository dsaRepository;

    @InjectMocks
    private DsaService dsaService;

    private Dsa testDsa;
    private DsaRequestDto requestDto;

    @BeforeEach
    void setUp() {
        testDsa = Dsa.builder()
                .id(UUID.randomUUID())
                .name("Test DSA")
                .uniqueCode("DSA123")
                .status(DsaStatus.PENDING)
                .build();

        requestDto = DsaRequestDto.builder()
                .name("Test DSA")
                .mobileNumber("9876543210")
                .email("test@example.com")
                .build();
    }

    @Test
    void createDsa_Success() {
        when(dsaRepository.save(any(Dsa.class))).thenReturn(testDsa);

        DsaResponseDto response = dsaService.createDsa(requestDto, "admin");

        assertNotNull(response);
        assertEquals("Test DSA", response.getName());
        assertEquals(DsaStatus.PENDING, response.getStatus());
    }

    @Test
    void updateDsa_Success() {
        when(dsaRepository.findById(testDsa.getId())).thenReturn(Optional.of(testDsa));
        when(dsaRepository.save(any(Dsa.class))).thenReturn(testDsa);

        DsaResponseDto response = dsaService.updateDsa(testDsa.getId(), requestDto);

        assertNotNull(response);
        assertEquals("Test DSA", response.getName());
    }

    @Test
    void updateDsaStatus_Success() {
        when(dsaRepository.findById(testDsa.getId())).thenReturn(Optional.of(testDsa));
        testDsa.setStatus(DsaStatus.EMPANELLED);
        when(dsaRepository.save(any(Dsa.class))).thenReturn(testDsa);

        DsaResponseDto response = dsaService.updateDsaStatus(testDsa.getId(), DsaStatus.EMPANELLED);

        assertNotNull(response);
        assertEquals(DsaStatus.EMPANELLED, response.getStatus());
    }

    @Test
    void getAllDsas_Success() {
        Page<Dsa> page = new PageImpl<>(Collections.singletonList(testDsa));
        when(dsaRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<DsaResponseDto> responsePage = dsaService.getAllDsas(null, null, PageRequest.of(0, 10));

        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Test DSA", responsePage.getContent().get(0).getName());
    }
}
