package com.leon.domain;

import com.leon.domain.aggregate.SpeechContext;
import com.leon.domain.dto.SpeechDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface Converter {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    SpeechDTO of(SpeechContext.Chat chat, Integer audioSample);

}
