package com.common.graphql.type;

import java.math.BigInteger;
import java.util.Date;
import com.common.util.DateUtil;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLScalarType;

public class GraphQLDateType extends GraphQLScalarType {
    public GraphQLDateType() {
        super("Date", "Local Date Time type", new Coercing<Date, String>() {
            @Override
            public String serialize(Object input) {
                if (input instanceof Date) {
                    return DateUtil.dateToString((Date) input);
                } else {
                    Date result = DateUtil.convertDate(input);
                    return DateUtil.dateToString(result);
                }
            }

            @Override
            public Date parseValue(Object input) {
                Date result = DateUtil.convertDate(input);
                if (result == null) {
                    throw new CoercingParseValueException("Invalid value '" + input + "' for Date");
                }
                return result;
            }

            @Override
            public Date parseLiteral(Object input) {
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
