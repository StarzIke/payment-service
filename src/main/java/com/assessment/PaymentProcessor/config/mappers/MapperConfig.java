package com.assessment.PaymentProcessor.config.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper
                .getConfiguration()
                .setDeepCopyEnabled(true)
                .setPropertyCondition(Conditions.isNotNull())
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true);

        modelMapper.addConverter(new AbstractConverter<String, LocalDate>() {
            @Override
            protected LocalDate convert(String source) {
                return LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        });

        modelMapper.addConverter(new AbstractConverter<String, LocalDateTime>() {
            @Override
            protected LocalDateTime convert(String source) {
                return LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        });

        modelMapper.addConverter(new AbstractConverter<LocalDate, String>() {
            @Override
            protected String convert(LocalDate source) {
                return source.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
        });

        modelMapper.addConverter(new AbstractConverter<LocalDateTime, String>() {
            @Override
            protected String convert(LocalDateTime source) {
                return source.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        });

        return modelMapper;

    }


    @Bean
    public DateTimeFormatter dateTimeFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

}
