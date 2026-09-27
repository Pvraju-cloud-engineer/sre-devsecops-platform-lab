package com.example.claims;

import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClaimsApiApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void createsAndReadsPersistedClaim() throws Exception {
        MvcResult result = mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"integration test claim"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String id = jsonMapper.readTree(responseBody).get("id").stringValue();

        mockMvc.perform(get("/claims/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsBlankDescription() throws Exception {
        mockMvc.perform(post("/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"   "}
                                """))
                .andExpect(status().isBadRequest());
    }
}
