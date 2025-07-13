package com.findmymeme.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.lambda")
public class AwsLambdaProperties {
    private String functionName;
}
