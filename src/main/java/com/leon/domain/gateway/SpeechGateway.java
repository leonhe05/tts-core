package com.leon.domain.gateway;

import com.leon.domain.dto.SpeechDTO;

import java.io.IOException;

public interface SpeechGateway {

    byte[] speech(SpeechDTO speechDTO) throws IOException;

}
