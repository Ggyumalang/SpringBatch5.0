package com.example.batch.job;

import com.example.batch.domain.Member;
import com.example.batch.domain.repository.MemberRepository;
import com.example.batch.dto.MemberDTO;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "spring.batch.job", name = "names", havingValue = "SimpleChunkJob")
public class SimpleChunkSample {

    private final MemberRepository memberRepository;
    private final EntityManagerFactory entityManagerFactory;
    private static final int CHUNK_SIZE = 3;

    @Bean(name = "simpleChunkJob")
    public Job simpleChunkJob(JobRepository jobRepository, Step simpleChunkStep) {
        log.info(">>> Started simpleChunkJob");
        return new JobBuilder("simpleChunkJob", jobRepository)
                .start(simpleChunkStep)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean(name = "simpleChunkStep")
    public Step simpleChunkStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        log.info(">>> Started simpleChunkStep");
        return new StepBuilder("simpleChunkStep", jobRepository)
                .<Member, MemberDTO>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    private ItemReader<Member> reader() {
        log.info(">>> Started Reader ");
        return new JpaPagingItemReaderBuilder<Member>()
                .name("JpaPagingItemReaderMember")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select m from Member m ORDER BY id")
                .build();
    }

    private ItemProcessor<Member, MemberDTO> processor() {
        log.info(">>> Started Processor ");
        return item -> {
            log.info(item.getName());
            MemberDTO memberDTO = MemberDTO.fromEntity(item);
            memberDTO.changeName(memberDTO.getName() + System.currentTimeMillis());
            return memberDTO;
        };
    }

    private ItemWriter<MemberDTO> writer() {
        log.info(">>> Started Writer ");
        return items -> {
            log.info(">>> Started ItemWriter results : {}", items);
            memberRepository.saveAll(items.getItems().stream().map(Member::fromDto).toList());
        };
    }
}
