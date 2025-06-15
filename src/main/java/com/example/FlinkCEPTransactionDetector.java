package com.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlinkCEPTransactionDetector {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final double ALERT_THRESHOLD = 1000.0;

    public static void main(String[] args) throws Exception {
        LOG.info(">>> FlinkCEPTransactionDetector starting...");

        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);  // For easier debugging

        // Define pattern
        Pattern<Transaction, ?> pattern = Pattern.<Transaction>begin("first")
            .where(new SimpleCondition<Transaction>() {
                @Override
                public boolean filter(Transaction t) {
                    LOG.debug("First event: Account={}, Amount={}", t.getAccountId(), t.getAmount());
                    return true;
                }
            })
            .next("second")
            .where(new org.apache.flink.cep.pattern.conditions.IterativeCondition<Transaction>() {
                @Override
                public boolean filter(Transaction t2, Context<Transaction> ctx) throws Exception {
                    for (Transaction t : ctx.getEventsForPattern("first")) {
                        if (t2.getAccountId().equals(t.getAccountId())) {
                            LOG.debug("Second event matches Account={}", t2.getAccountId());
                            return true;
                        }
                    }
                    return false;
                }
            })
            .next("third")
            .where(new org.apache.flink.cep.pattern.conditions.IterativeCondition<Transaction>() {
                @Override
                public boolean filter(Transaction t3, Context<Transaction> ctx) throws Exception {
                    for (Transaction t : ctx.getEventsForPattern("first")) {
                        if (t3.getAccountId().equals(t.getAccountId())) {
                            LOG.debug("Third event matches Account={}", t3.getAccountId());
                            return true;
                        }
                    }
                    return false;
                }
            })
            .within(Time.hours(1));

        // Read transactions from file
        DataStream<String> input = env.readTextFile("data/transactions_bulk.csv");

        DataStream<Transaction> transactions = input
            .filter(line -> !line.startsWith("timestamp"))
            .map(new MapFunction<String, Transaction>() {
                @Override
                public Transaction map(String line) {
                    String[] fields = line.split(",");
                    String accountId = fields[1];
                    double amount = Double.parseDouble(fields[2]);
                    long timestamp = LocalDateTime.parse(fields[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli();
                    LOG.debug("Parsed: Account={}, Amount={}, Timestamp={}", accountId, amount, fields[0]);
                    return new Transaction(accountId, amount, timestamp);
                }
            })
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<Transaction>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                    .withTimestampAssigner((event, timestamp) -> event.getTimestamp())
            );

        // ✅ Key the stream before applying pattern
        PatternStream<Transaction> patternStream = CEP.pattern(
            transactions.keyBy(Transaction::getAccountId),
            pattern
        );

        patternStream.select(new PatternSelectFunction<Transaction, String>() {
            @Override
            public String select(Map<String, List<Transaction>> pattern) {
                List<Transaction> matched = new ArrayList<>();
                matched.addAll(pattern.getOrDefault("first", List.of()));
                matched.addAll(pattern.getOrDefault("second", List.of()));
                matched.addAll(pattern.getOrDefault("third", List.of()));

                String accountId = matched.get(0).getAccountId();
                double total = matched.stream().mapToDouble(Transaction::getAmount).sum();

                LOG.info("Pattern match:");
                for (Transaction t : matched) {
                    LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(t.getTimestamp()), ZoneId.systemDefault());
                    LOG.info("  Account={}, Amount={}, Time={}", t.getAccountId(), t.getAmount(), time);
                }

                if (total >= ALERT_THRESHOLD) {
                    String alert = String.format("ALERT: Account %s total $%.2f", accountId, total);
                    LOG.info(alert);
                    return alert;
                }
                return null;
            }
        })
        .filter(alert -> alert != null)
        .print();

        // Run the Flink job
        env.execute("Flink CEP Transaction Detector");
    }
}
