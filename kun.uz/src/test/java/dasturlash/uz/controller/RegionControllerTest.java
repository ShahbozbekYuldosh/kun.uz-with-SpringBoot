package dasturlash.uz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dasturlash.uz.config.JwtAuthenticationFilter;
import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.service.RegionService;
import dasturlash.uz.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RegionController.class)
class RegionControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegionService regionService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegionDTO mockDto() {
        RegionDTO dto = new RegionDTO();
        dto.setId(1);
        dto.setNameUz("Toshkent");
        dto.setNameRu("Ташкент");
        dto.setNameEn("Tashkent");
        dto.setNameKr("Тошкент");
        dto.setRegionKey("TASHKENT");
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRegion_success() throws Exception {
        RegionDTO dto = mockDto();

        when(regionService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameUz").value("Toshkent"))
                .andExpect(jsonPath("$.regionKey").value("TASHKENT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRegion_success() throws Exception {
        RegionDTO dto = mockDto();
        dto.setNameUz("Yangi Toshkent");

        when(regionService.update(eq(1), any())).thenReturn(dto);

        mockMvc.perform(put("/api/v1/region/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameUz").value("Yangi Toshkent"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRegion_success() throws Exception {
        when(regionService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/region/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRegions_admin() throws Exception {
        when(regionService.getAll()).thenReturn(List.of(mockDto()));

        mockMvc.perform(get("/api/v1/region/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nameUz").value("Toshkent"));
    }

    @Test
    void getRegionsByLang_success() throws Exception {
        when(regionService.getAllByLang(AppLanguageEnum.UZ))
                .thenReturn(List.of(mockDto()));

        mockMvc.perform(get("/api/v1/region/lang")
                        .header("Accept-Language", "UZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nameUz").value("Toshkent"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRegion_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto())))
                .andExpect(status().isForbidden());
    }
}
