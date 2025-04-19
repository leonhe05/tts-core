package com.leon.application;

import com.leon.application.protocol.SynthesisRequest;
import com.leon.domain.aggregate.SpeechContext;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface Converter {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    SpeechContext of(SynthesisRequest req);

}
