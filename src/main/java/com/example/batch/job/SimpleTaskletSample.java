package com.example.batch.job;

import com.example.batch.domain.Member;
import com.example.batch.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "spring.batch.job", name = "names" , havingValue = "SimpleTaskletJob")
public class SimpleTaskletSample {

    private final MemberRepository memberRepository;

    @Bean
    public Job simapleTaskletJob(JobRepository jobRepository, Step simpleTaskletStep) {
        log.info(">>> Started simpleTaskletJob");
        return new JobBuilder("simpleTaskletJob", jobRepository)
                .start(simpleTaskletStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step simpleTaskletStep(
            JobRepository jobRepository,
            Tasklet testTasklet,
            PlatformTransactionManager transactionManager
    ) {
        log.info(">>> Started simpleTaskletStep");
        return new StepBuilder("simpleTaskletStep", jobRepository)
                .tasklet(testTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet testTasklet() {
        return ((contribution, chunkContext) -> {
            log.info(">>>>> testTasklet Started");
            memberRepository.saveAll(setMemberList());
            return RepeatStatus.FINISHED;
        });
    }

    private List<Member> setMemberList() {
        return List.of(Member.builder()
                        .memberId("khg1")
                        .name("kim")
                        .password("1")
                        .build(),
                Member.builder()
                        .memberId("khg2")
                        .name("han")
                        .password("2")
                        .build(),
                Member.builder()
                        .memberId("khg3")
                        .name("gyu")
                        .password("3")
                        .build()
        );
    }
}
