    package com.common.graphql.type;

import java.math.BigInteger;
import java.time.LocalDateTime;
import com.common.util.DateTimeUtil;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLScalarType;

public class GraphQLLocalDateTimeType extends GraphQLScalarType {

    public GraphQLLocalDateTimeType() {
        super("LocalDateTime", "Local Date Time type", new Coercing<LocalDateTime, String>() {

            @Override
            public String serialize(Object input) {
                if (input instanceof LocalDateTime) {
                    return DateTimeUtil.convertDateToString((LocalDateTime) input);
                } else {
                    LocalDateTime result = DateTimeUtil.convertLocalDateTime(input);
                    return DateTimeUtil.convertDateToString(result);
                }
            }

            @Override
            public LocalDateTime parseValue(Object input) {
                LocalDateTime result = DateTimeUtil.convertLocalDateTime(input);
                if (result == null) {
                    throw new CoercingParseValueException("Invalid value '" + input + "' for LocalDateTime");
                }
                return result;
            }

            @Override
            public LocalDateTime parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    String value = ((StringValue) input).getValue();
                    return parseValue(value);
                } else if (input instanceof IntValue) {
                    BigInteger value = ((IntValue) input).getValue();
                    return parseValue(value.longValue());
                }
                return parseValue(input);
            }
        });
    }

}
