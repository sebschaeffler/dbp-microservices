package com.dbp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SharePriceServiceApplication.class)
@WebAppConfiguration
public class SharePriceRestControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private SharePriceRepository sharePriceRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.sharePriceRepository.deleteAllInBatch();
        sharePriceRepository.save(new SharePrice("NewCorp", 100.0));
    }

    @Test
    public void sharePricesOK() throws Exception {
        mockMvc.perform(get("/sharePrices"))
                .andExpect(status().isOk());
    }

    @Test
    public void sharePricesFound() throws Exception {
        mockMvc.perform(get("/sharePrices/0"))
                .andExpect(status().isOk());
    }

    @Test
    public void sharePricesNotFound() throws Exception {
        mockMvc.perform(get("/sharePrices/dummy"))
                .andExpect(status().isBadRequest());
    }

}