package com.ewis.jobms.job.mapper;

import com.ewis.jobms.job.Job;
import com.ewis.jobms.job.dto.JobDTO;
import com.ewis.jobms.job.external.Company;

public class JobMapper {
    public static JobDTO mapToJobWithCompanyDto(Job job, Company company){

        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setTitle(job.getTitle());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setMinSalary(job.getMinSalary());
        jobDTO.setMaxSalary(job.getMaxSalary());
        jobDTO.setCompany(company);

        return jobDTO;
    }
}
