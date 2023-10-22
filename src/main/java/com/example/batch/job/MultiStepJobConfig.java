package com.example.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.batch.job", name = "names", havingValue = "MultiStepJob")
public class MultiStepJobConfig {

    @Bean
    public Job MultiStepJob(
            JobRepository jobRepository, Step firstStep, Step secondStep,
            Step lastStep
    ) {
        return new JobBuilder("multiStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(firstStep)
                .next(secondStep)
                .next(lastStep)
                .build();
    }

    @Bean
    @JobScope
    public Step firstStep(
            JobRepository jobRepository, PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("firstStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("firstStep!");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step secondStep(
            JobRepository jobRepository, PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("secondStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("secondStep!");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step lastStep(
            JobRepository jobRepository, PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("lastStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("lastStep!");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
