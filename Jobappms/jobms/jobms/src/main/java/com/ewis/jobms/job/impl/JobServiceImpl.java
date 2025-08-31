package com.ewis.jobms.job.impl;

import com.ewis.jobms.job.Job;
import com.ewis.jobms.job.JobRepository;
import com.ewis.jobms.job.JobService;
import com.ewis.jobms.job.clients.CompanyClients;
import com.ewis.jobms.job.clients.ReviewClient;
import com.ewis.jobms.job.dto.JobDTO;
import com.ewis.jobms.job.external.Company;
import com.ewis.jobms.job.external.Review;
import com.ewis.jobms.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    //private List<Job> jobs = new ArrayList<>();
    JobRepository jobRepository;
    //private Long nextId = 1L;

    //RestTemplate restTemplate;

    private CompanyClients companyClients;
    private ReviewClient reviewClient;

    int attempt = 0;

    public JobServiceImpl(JobRepository jobRepository, CompanyClients companyClients, ReviewClient reviewClient) {
        this.jobRepository = jobRepository;
        this.companyClients = companyClients;
        this.reviewClient = reviewClient;
    }

    @Override
//    @CircuitBreaker(name="companyBreaker", fallbackMethod = "companyBreakerFallback")
//    @Retry(name="companyBreaker", fallbackMethod = "companyBreakerFallback")
    @RateLimiter(name="companyBreaker", fallbackMethod = "companyBreakerFallback")
    public List<JobDTO> findAll() {
        System.out.println("Attempt: " + ++attempt);
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobDTOS = new ArrayList<>();

        //converts the jobs into streams (stream is an element that can be process in the pipeline)
        return jobs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<String> companyBreakerFallback(Exception e){
        List<String> list = new ArrayList<>();
        list.add("Dummy");
        return list;
    }

    private JobDTO convertToDto(Job job) {
        Company company = companyClients.getCompany(job.getCompanyID());
        List<Review> reviews = reviewClient.getReviews(job.getCompanyID());

        JobDTO jobDTO = JobMapper.mapToJobWithCompanyDto(job,company,reviews);

        return jobDTO;

    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        return convertToDto(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJobById(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();

            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setLocation(updatedJob.getLocation());

            jobRepository.save(job);

            return true;
        }
        return false;
    }
}
