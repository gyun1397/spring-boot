package com.common.graphql.type;

import java.math.BigInteger;
import java.time.LocalDate;
import com.common.util.DateTimeUtil;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLScalarType;

public class GraphQLLocalDateType extends GraphQLScalarType {

    public GraphQLLocalDateType() {
        super("LocalDate", "Local Date Time type", new Coercing<LocalDate, String>() {

            @Override
            public String serialize(Object input) {
                if (input instanceof LocalDate) {
                    return DateTimeUtil.convertDateToString((LocalDate) input);
                } else {
                    LocalDate result = DateTimeUtil.convertLocalDate(input);
                    return DateTimeUtil.convertDateToString(result);
                }
            }

            @Override
            public LocalDate parseValue(Object input) {
                LocalDate result = DateTimeUtil.convertLocalDate(input);
                if (result == null) {
                    throw new CoercingParseValueException("Invalid value '" + input + "' for LocalDate");
                }
                return result;
            }

            @Override
            public LocalDate parseLiteral(Object input) {
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
